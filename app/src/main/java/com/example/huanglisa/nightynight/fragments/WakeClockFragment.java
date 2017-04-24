package com.example.huanglisa.nightynight.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.huanglisa.nightynight.models.ClockItem;
import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.activities.ClockSetterActivity;
import com.example.huanglisa.nightynight.adapters.ClockListAdapter;
import com.example.huanglisa.nightynight.utils.ClockMsgPacker;

import java.util.ArrayList;
import java.util.List;


public class WakeClockFragment extends Fragment {
    private static String TAG = "WakeClockFragment";
    private static String oldClockId = "";
    private List<ClockItem> clockList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ClockListAdapter clockAdapter;
    private Button finishBtn;
    private TimePicker tp;
    //private static final int REQUEST_ADD = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if( requestCode == REQUEST_ADD ) {
            System.out.format("add %n");
        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // parse passed in data if any
        String oldClockEncoded = getArguments().getString("oldClockEncoded");
        Log.e(TAG, "oldClockEncoded received: " + oldClockEncoded);
        // create view
        View view = inflater.inflate(R.layout.fragment_wake_clock, container, false);
        System.out.format("wakeup%n");

        // edit time picker values
        tp = (TimePicker) view.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);
        // if changing exiting clock, display old value
        if (oldClockEncoded != "") {
            ClockMsgPacker packer = new ClockMsgPacker();
            ClockItem clockOld = packer.StringToClock(oldClockEncoded);
            oldClockId = clockOld.getId();
            tp.setHour(clockOld.getWakeupHour());
            tp.setMinute(clockOld.getWakupMin());
        }
        //can't call it here since pagers are created when initiated
        //((Toolbar)getActivity().findViewById(R.id.my_toolbar)).setTitle("Wakeup Time");

        finishBtn = (Button) view.findViewById(R.id.button);
        tp = (TimePicker) view.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                ClockItem curItem = ((ClockSetterActivity) getActivity()).clockItem;
                curItem.setWakeupHour(tp.getHour());
                curItem.setWakeupMin(tp.getMinute());

                data.putExtra("sleepHour", curItem.getSleepHour());
                data.putExtra("sleepMin", curItem.getSleepMin());
                data.putExtra("wakeupHour", curItem.getWakeupHour());
                data.putExtra("wakeupMin", curItem.getWakupMin());
                data.putExtra("id", oldClockId);
                getActivity().setResult(getActivity().RESULT_OK, data);
                getActivity().finish();

            }
        });
        return view;


    }


}

