package com.example.huanglisa.nightynight.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.huanglisa.nightynight.activities.MainActivity;
import com.example.huanglisa.nightynight.dialogs.FriendConfirmDialog;
import com.example.huanglisa.nightynight.models.FriendItem;
import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.models.ReceivedBuilding;
import com.example.huanglisa.nightynight.models.ReceivedFriend;
import com.example.huanglisa.nightynight.SessionManager;
import com.example.huanglisa.nightynight.adapters.FriendListAdapter;
import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.BuildingApiInterface;
import com.example.huanglisa.nightynight.rest.FriendApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendFragment extends Fragment {
    public static final int DIALOG_FRAGMENT = 1;
    private static final String KEY_POSITION = "position";
    private static final String TAG = "FriendFragment";

    private RecyclerView recyclerView;
    private TextView buildingName;
    private List<FriendItem> friendList = new ArrayList<>();
    private FriendListAdapter friendListAdapter;
    private Button addBtn;
    private SessionManager session;
    private MainActivity mainActivity;
    private FriendApiInterface friendApiInterface;
    private BuildingApiInterface buildingApiInterface;
    private String serverBuildingId;

    public static FriendFragment newInstance(int position) {
        FriendFragment frag = new FriendFragment();
        Bundle args = new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);

        return (frag);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        //TextInputEditText editor=(TextInputEditText)view.findViewById(R.id.friend_editor);
        int position = getArguments().getInt(KEY_POSITION, -1);
        System.out.format("position: %d%n", position);


        session = new SessionManager(getContext().getApplicationContext());
        this.mainActivity = (MainActivity)this.getActivity();

        friendApiInterface = ApiClient.getClient().create(FriendApiInterface.class);
        buildingApiInterface = ApiClient.getClient().create(BuildingApiInterface.class);


        //generate list
        recyclerView = (RecyclerView) view.findViewById(R.id.friendList_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        friendListAdapter = new FriendListAdapter(friendList);
        recyclerView.setAdapter(friendListAdapter);
        final Context context = getActivity().getApplicationContext();
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d(TAG, friendList.get(position).getId());
                        mainActivity.setSelectedFriendEmail(friendList.get(position).getId());
                        mainActivity.setSelectedFriendName(friendList.get(position).getName());
                        mainActivity.createTabIntent(3);
                    }
                })
        );

        buildingName = (TextView) view.findViewById(R.id.buildingName);


        populateFriendList(position);
        getBuildingName();

        //add friend button
        addBtn = (Button) view.findViewById(R.id.addButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFriendSearchDialog();
            }
        });


        return (view);
    }

    public void populateFriendList(int position) {
        System.out.format("populate list%n");
        friendList.clear();
        serverBuildingId = session.getBuildingList().get(position).id;
        System.out.format("current position: %d, buildingId:%s%n", position, serverBuildingId);

        Call<List<ReceivedFriend>> call = friendApiInterface.getFriendInBuilding(session.getToken(), serverBuildingId);
        call.enqueue(new Callback<List<ReceivedFriend>>() {
            @Override
            public void onResponse(Call<List<ReceivedFriend>> call, Response<List<ReceivedFriend>> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "failed to get friend list");
                    try {
                        Log.e(TAG, response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "failed to get friend list");
                    } finally {
                        return;
                    }

                }

                System.out.format("current friend: %d%n", response.body().size());
                for (ReceivedFriend rf : response.body()) {
                    System.out.format("%s, %s, %s%n", rf.friendId, rf.name, rf.status);
                    FriendItem fItem = new FriendItem(rf.friendId, rf.name, rf.status);
                    friendList.add(fItem);
                }

                friendListAdapter.notifyItemRangeChanged(0, friendList.size());
            }

            @Override
            public void onFailure(Call<List<ReceivedFriend>> call, Throwable t) {
                Log.e(TAG, t.getMessage().toString());
            }
        });
        /*FriendItem fItem = new FriendItem("1", "Lisa", true);
        friendList.add(fItem);

        fItem = new FriendItem("2", "Nicole", true);
        friendList.add(fItem);*/


    }

    public void getBuildingName() {
        Call<ReceivedBuilding> call = buildingApiInterface.getBuilding(session.getToken(), serverBuildingId);
        call.enqueue(new Callback<ReceivedBuilding>() {
            @Override
            public void onResponse(Call<ReceivedBuilding> call, Response<ReceivedBuilding> response) {
                if (!response.isSuccessful()) {
                    try {
                        Log.e(TAG, response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "failed to get building item");
                    } finally {
                        return;
                    }
                }
                buildingName.setText(response.body().name);
            }

            @Override
            public void onFailure(Call<ReceivedBuilding> call, Throwable t) {
                Log.e(TAG, t.getMessage().toString());
            }
        });
    }

    public void showFriendSearchDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new FriendEmailSearchFragment();
        dialog.setTargetFragment(this, DIALOG_FRAGMENT);
        dialog.show(getActivity().getSupportFragmentManager(), "NoticeDialogFragment");
    }

    public void onFriendConfirmDialogPositiveClick(boolean isSuccessful, String id, String name, boolean status) {
        // User touched the dialog's positive button
        if (isSuccessful) {
            addFriend(id, name, status);
        }

    }

    public void addFriend(String id, String name, boolean status) {
        Call<ReceivedFriend> call = friendApiInterface.addFriend(session.getToken(), serverBuildingId, id);
        call.enqueue(new Callback<ReceivedFriend>() {
            @Override
            public void onResponse(Call<ReceivedFriend> call, Response<ReceivedFriend> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "failed to add friend");
                    try {
                        Log.e(TAG, response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "failed to add friend");
                    } finally {
                        return;
                    }
                }
                System.out.format("new friend %s%n", response.body().status);
                FriendItem fItem = new FriendItem(response.body().friendId, response.body().name, response.body().status);
                friendList.add(fItem);
                friendListAdapter.notifyItemInserted(friendList.size() - 1);
            }

            @Override
            public void onFailure(Call<ReceivedFriend> call, Throwable t) {
                Log.e(TAG, t.getMessage().toString());
            }
        });

    }

    public void onFriendSearchDialogPositiveClick(boolean isSuccessful, String id, String name, boolean status) {
        // User touched the dialog's positive button
        if (isSuccessful) {
            showFriendConfirmDialog(id, name, status);
        }

    }

    public void showFriendConfirmDialog(String id, String name, boolean status) {
        DialogFragment dialog = FriendConfirmDialog.newInstance(id, name, status);
        dialog.setTargetFragment(this, DIALOG_FRAGMENT);
        dialog.show(getActivity().getSupportFragmentManager(), "NoticeDialogFragment");
    }

    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }

}
