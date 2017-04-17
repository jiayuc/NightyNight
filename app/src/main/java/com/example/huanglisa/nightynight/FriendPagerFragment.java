package com.example.huanglisa.nightynight;

/**
 * Created by huanglisa on 11/20/16.
 */

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.BuildingApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendPagerFragment extends Fragment {
    public static final int DIALOG_FRAGMENT = 1;
    public static final String TAG = "FriendPager";
    private SessionManager session;
    private int size;
    private BuildingApiInterface buildingApiInterface;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.pager_friend, container, false);
        ViewPager pager = (ViewPager) view.findViewById(R.id.friend_pager);
        pager.setAdapter(buildAdapter());

        session = new SessionManager(getContext().getApplicationContext());
        size = session.getBuildingList().size();
        buildingApiInterface = ApiClient.getClient().create(BuildingApiInterface.class);
        if (size == 0) {
            showBuildingGenerationDialog();
        }

        return (view);
    }

    private PagerAdapter buildAdapter() {
        return (new FriendViewAdapter(getActivity(), getChildFragmentManager()));
    }

    public void showBuildingGenerationDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = BuildingGenerationDialog.newInstance("FriendPagerFragment");
        dialog.setTargetFragment(this, DIALOG_FRAGMENT);
        dialog.show(getActivity().getSupportFragmentManager(), "NoticeDialogFragment");
    }

    public void onBuildingGenerationDialogPositiveClick(String name) {
        //search server
        Call<ReceivedBuilding> call = buildingApiInterface.addBuilding(session.getToken(), name, 0);
        call.enqueue(new Callback<ReceivedBuilding>() {
            @Override
            public void onResponse(Call<ReceivedBuilding> call, Response<ReceivedBuilding> response) {
                System.out.format("add new building%n");
                if (!response.isSuccessful()) {
                    try {
                        Log.e(TAG, response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "failed to get friend info");
                    } finally {
                        return;
                    }
                }
                String name = response.body().name;
                String id = response.body().id;
                int index = response.body().index;
                ReceivedBuilding rb = new ReceivedBuilding(id, name, index, session.getToken());
                session.updateBuilding(rb);
                reloadFragment();
            }

            @Override
            public void onFailure(Call<ReceivedBuilding> call, Throwable t) {
                Log.d(TAG, "onFailure");
                Log.e(TAG, t.toString());
            }
        });

    }

    public void reloadFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}