package com.example.huanglisa.nightynight.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.huanglisa.nightynight.AlarmReceiver;
import com.example.huanglisa.nightynight.models.ClockItem;
import com.example.huanglisa.nightynight.adapters.ClockListAdapter;
import com.example.huanglisa.nightynight.activities.ClockSetterActivity;
import com.example.huanglisa.nightynight.activities.MainActivity;
import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.models.ReceivedClock;
import com.example.huanglisa.nightynight.RecyclerViewSwitchListener;
import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.ClockApiInterface;
import com.example.huanglisa.nightynight.rest.UserApiInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huanglisa on 11/1/16.
 */

public class ClockFragment extends Fragment implements RecyclerViewSwitchListener {
    private static final String TAG = "ClockFragment";
    private static final int REQUEST_ADD = 1;
    private List<ClockItem> clockList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ClockListAdapter clockAdapter;
    private Button addBtn;
    private UserApiInterface userApiInterface;
    private ClockApiInterface clockApiInterface;

    private ProgressDialog progress;


    private AlarmManager alarmManager;
    private PendingIntent pendingWakeIntent;
    private Intent alarmWakeReceiverIntent;
    private PendingIntent pendingSleepIntent;
    private Intent alarmSleepReceiverIntent;

    private Paint p = new Paint();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == REQUEST_ADD) {
                System.out.format("add clock item%n");
                addClockData(data.getExtras().getInt("sleepHour"), data.getExtras().getInt("sleepMin"), data.getExtras().getInt("wakeupHour"), data.getExtras().getInt("wakeupMin"));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //API
        userApiInterface = ApiClient.getClient().create(UserApiInterface.class);
        clockApiInterface = ApiClient.getClient().create(ClockApiInterface.class);

        View view = inflater.inflate(R.layout.fragment_clock, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.clockList_view);
        clockAdapter = new ClockListAdapter(clockList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(clockAdapter);
        setUpItemTouchHelper();
        // TODO: NEWLY ADDED, item touch feature
        final Context context = getActivity().getApplicationContext();
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        ClockItem clock = clockList.get(position);
                        TimePicker picker = new TimePicker(context);
                        picker.setHour(clock.getSleepHour());
                        picker.setMinute(clock.getSleepMin());


                        Log.e(TAG, "clock item  at pos " + position + " id: " + clock.getId());
                        Toast.makeText(context, clock.getId() + " is selected!", Toast.LENGTH_SHORT).show();
                        // jump to activity
                        Intent intent = new Intent(context, ClockSetterActivity.class);
                        intent.putExtra("EXTRA_SESSION_ID", "hello");
                        startActivity(intent);
                    }
                })
        );

        //instance of progressBar
        generateProgressBar();
        prepareClockData();


        //alarm
        // Get the alarm manager service
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);


        //pending intent, will be responsible to create notification
        alarmWakeReceiverIntent = new Intent(getActivity(), AlarmReceiver.class);
        alarmWakeReceiverIntent.putExtra("status", true);
        pendingWakeIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmWakeReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmSleepReceiverIntent = new Intent(getActivity(), AlarmReceiver.class);
        alarmSleepReceiverIntent.putExtra("status", false);
        pendingSleepIntent = PendingIntent.getBroadcast(getActivity(), 1, alarmSleepReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmWakeReceiverIntent == alarmSleepReceiverIntent || pendingWakeIntent == pendingSleepIntent) {
            Log.d(TAG, "same intent.....");
        }


        addBtn = (Button) view.findViewById(R.id.addButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ClockSetterActivity.class);
                startActivityForResult(intent, REQUEST_ADD);
            }
        });
        return view;


    }

    /**
     * set up ItemTouchHelper,  which is a utility that takes care of everything you need to add both drag & drop and swipe-to-dismiss to your RecyclerView
     */
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            /**
             * Delete set of alarm clocks when swiped
             *
             * @param viewHolder
             * @param swipeDir
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                System.out.println("onSwiped");
                int swipedPosition = viewHolder.getAdapterPosition();
                String clockId = clockList.get(swipedPosition).getId();
                clockAdapter.removeClockData(clockId);
                Call<ReceivedClock> call = clockApiInterface.removeClock((((MainActivity) getActivity()).session.getToken()), clockId);
                call.enqueue(new Callback<ReceivedClock>() {
                    @Override
                    public void onResponse(Call<ReceivedClock> call, Response<ReceivedClock> response) {
                        Log.e(TAG, "onResponse message: " + response.message());
                        if (!response.isSuccessful()) {
                            Log.e(TAG, "failed to delete clock item " + response.message());
                            return;
                        }
                        Log.e(TAG, "onResponse call.id: " + response.body().id);
                        clockAdapter.removeClockData(response.body().id);
                    }

                    @Override
                    public void onFailure(Call<ReceivedClock> call, Throwable t) {
                        Log.e(TAG, "failed to add clock item (onFailure) " + t.getMessage());
                    }
                });

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX <= 0) {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void generateProgressBar() {
        progress = new ProgressDialog(getContext());
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
    }

    private void prepareClockData() {
        progress.show();
        Call<ReceivedClock> getActiveClockCall = userApiInterface.userGetActiveClock(((MainActivity) getActivity()).session.getToken());
        getActiveClockCall.enqueue(new Callback<ReceivedClock>() {
            @Override
            public void onResponse(Call<ReceivedClock> call1, Response<ReceivedClock> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "failed to get activated clock " + response.body());
                    progress.dismiss();
                    return;
                }
                final String id = response.body() == null ? null : response.body().id;
                System.out.format("activated id: %s%n", id);
                Call<List<ReceivedClock>> getListCall = clockApiInterface.getUserClocks(((MainActivity) getActivity()).session.getToken());
                getListCall.enqueue(new Callback<List<ReceivedClock>>() {
                    @Override
                    public void onResponse(Call<List<ReceivedClock>> call2, Response<List<ReceivedClock>> response) {

                        if (!response.isSuccessful()) {
                            Log.e(TAG, "failed to get clock items " + response.message());
                            progress.dismiss();
                            return;
                        }
                        for (ReceivedClock clock : response.body()) {
                            boolean ifTurnedOn = false;
                            if (id != null && clock.id.equals(id)) {
                                System.out.format("get activated clock");
                                ifTurnedOn = true;
                            }
                            ClockItem cItem = new ClockItem(clock.sleepHour, clock.sleepMin, clock.wakeHour, clock.wakeMin, clock.id, ifTurnedOn);

                            clockList.add(cItem);

                        }
                        Log.e(TAG, "get clock items" + response.body().size());
                        clockAdapter.notifyDataSetChanged();
                        progress.dismiss();
                    }

                    @Override
                    public void onFailure(Call<List<ReceivedClock>> call2, Throwable t) {
                        Log.e(TAG, "failed to get clock items " + t.getMessage());
                        progress.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(Call<ReceivedClock> call1, Throwable t) {
                Log.e(TAG, "failed to get activated clock(on Failure) " + t.getMessage());
                progress.dismiss();
            }
        });


    }

    /**
     * Add new set of alarm clocks to server via API, and update, notify the adapter
     * @param sleepHour
     * @param sleepMin
     * @param wakeHour
     * @param wakeMin
     */
    public void addClockData(int sleepHour, int sleepMin, int wakeHour, int wakeMin) {

        Call<ReceivedClock> call = clockApiInterface.addClock(((MainActivity) getActivity()).session.getToken(), sleepHour, sleepMin, wakeHour, wakeMin);
        call.enqueue(new Callback<ReceivedClock>() {
            @Override
            public void onResponse(Call<ReceivedClock> call, Response<ReceivedClock> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "failed to add clock item " + response.message());
                    return;
                }


                ClockItem cItem = new ClockItem(response.body().sleepHour, response.body().sleepMin, response.body().wakeHour, response.body().wakeMin, response.body().id, false);
                clockList.add(cItem);
                System.out.format("notify clock list, new list size: %d%n", clockList.size());
                clockAdapter.notifyItemInserted(clockList.size() - 1);
            }

            @Override
            public void onFailure(Call<ReceivedClock> call, Throwable t) {
                Log.e(TAG, "failed to add clock item (onFailure) " + t.getMessage());
            }
        });

    }


    @Override
    public void onViewSwitched(int pos) {
        int activatedItemCount = 0;
        for (int i = 0; i < clockList.size(); i++) {
            if (i == pos) {
                String id = null;
                System.out.format("clock status: %b%n", clockList.get(i).getStatus());
                if (clockList.get(i).getStatus() == false) { //need to set a clock
                    activatedItemCount = activatedItemCount + 1;
                    clockList.get(i).setStatus(true);
                    id = clockList.get(i).getId();
                } else { //cancel a clock
                    Log.d(TAG, "cancel alarm");
                    cancelAlarm();
                    clockList.get(i).setStatus(false);
                }
                Call<ReceivedClock> call = userApiInterface.userUpdateActiveClock(((MainActivity) getActivity()).session.getToken(), id);
                call.enqueue(new Callback<ReceivedClock>() {
                    @Override
                    public void onResponse(Call<ReceivedClock> call, Response<ReceivedClock> response) {
                        if (!response.isSuccessful()) {
                            Log.e(TAG, "failed to update clock item for user");
                            return;
                        }
                        Log.d(TAG, "succeed to update clock item for user");
                        if (response.body() == null) {
                            System.out.format("clock is null");
                        }
                        if (response.body() != null) {
                            setAlarm(response.body().wakeHour, response.body().wakeMin, response.body().sleepHour, response.body().sleepMin);
                        }
                    }

                    @Override
                    public void onFailure(Call<ReceivedClock> call, Throwable t) {
                        Log.e(TAG, "failed to update clock item for user");
                    }
                });
            } else if (clockList.get(i).getStatus() != false) {
                clockList.get(i).setStatus(false);
            }
        }
        if (activatedItemCount > 1) {
            Log.e(TAG, "more than one items are activated");
            return;
        }
        clockAdapter.notifyDataSetChanged();
    }

    public void cancelAlarm() {
        System.out.format("cancel alarm!%n");
        alarmManager.cancel(pendingSleepIntent);
        alarmManager.cancel(pendingWakeIntent);
        pendingSleepIntent.cancel();
        pendingWakeIntent.cancel();

    }

    /**
     * Set android system clock to be waken up
     * @param wakeHour
     * @param wakeMin
     * @param sleepHour
     * @param sleepMin
     */
    public void setAlarm(int wakeHour, int wakeMin, int sleepHour, int sleepMin) {
        cancelAlarm();
        setPendingIntent();

        System.out.format("wake: %d:%d, sleep: %d:%d%n", wakeHour, wakeMin, sleepHour, sleepMin);
        Calendar wakeCalendar = Calendar.getInstance();
        Calendar sleepCalendar = Calendar.getInstance();
        wakeCalendar.setTimeInMillis(System.currentTimeMillis());
        sleepCalendar.setTimeInMillis(System.currentTimeMillis());
        Calendar current = Calendar.getInstance();


        sleepCalendar.set(Calendar.HOUR_OF_DAY, sleepHour);
        sleepCalendar.set(Calendar.MINUTE, sleepMin);
        sleepCalendar.set(Calendar.SECOND, 0);

        wakeCalendar.set(Calendar.HOUR_OF_DAY, wakeHour);
        wakeCalendar.set(Calendar.MINUTE, wakeMin);
        wakeCalendar.set(Calendar.SECOND, 0);


        /*if(sleepCalendar.before(current)){
            Log.d(TAG, "sleep before current time");
            sleepCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }*/
        if (wakeCalendar.before(current)) {
            Log.d(TAG, "wake before current time");
            wakeCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        //System.out.format("time to set: %d:%d%n", hour, min);
        //System.out.format("set alarm: %d:%d %d%n", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.getTimeInMillis());

        System.out.format("sleep clock: %d%n", sleepCalendar.getTimeInMillis());
        alarmManager.set(AlarmManager.RTC_WAKEUP, sleepCalendar.getTimeInMillis(), pendingSleepIntent);

        System.out.format("wake clock: %d%n", wakeCalendar.getTimeInMillis());
        alarmManager.set(AlarmManager.RTC_WAKEUP, wakeCalendar.getTimeInMillis(), pendingWakeIntent);

        AlarmManager.AlarmClockInfo info = alarmManager.getNextAlarmClock();
        if (info != null) {
            System.out.format("tigger time: %d%n", info.getTriggerTime());
        } else {
            System.out.format("no next alarm%n");
        }


    }

    public void setPendingIntent() {
        pendingSleepIntent = PendingIntent.getBroadcast(getActivity(), 1, alarmSleepReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingWakeIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmWakeReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}



