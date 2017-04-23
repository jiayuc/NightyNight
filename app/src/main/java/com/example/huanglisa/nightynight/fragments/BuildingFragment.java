package com.example.huanglisa.nightynight.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.huanglisa.nightynight.dialogs.BuildingGenerationDialog;
import com.example.huanglisa.nightynight.activities.DetailBuildingActivity;
import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.models.ReceivedBuilding;
import com.example.huanglisa.nightynight.SessionManager;
import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.BuildingApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by huanglisa on 11/1/16.
 */

public class BuildingFragment extends Fragment {
    public static final int DIALOG_FRAGMENT = 1;
    public static final String TAG = "BuildingFragment";
    SessionManager session;
    List<ReceivedBuilding> buildingList;
    //buildings in the page
    private int[] buildings = {
            R.id.building1,
            R.id.building2
    };
    private int[] images = {
            R.drawable.building1,
            R.drawable.building2
    };
    private View view;
    private BuildingApiInterface buildingApiInterface;
    //clicked building
    private int clickedIndex = -1;
    private int clickedId = -1;


    private View.OnClickListener clickListener = new View.OnClickListener() {
        //@Override
        public void onClick(View v) {
            buildingList = session.getBuildingList();
            ImageView imgV = (ImageView) v;
            if (imgV != null) {
                clickedId = imgV.getId();
                if (clickedId == R.id.building1)
                    clickedIndex = 0;
                if (clickedId == R.id.building2)
                    clickedIndex = 1;
                imgV.setImageLevel(2);
            }

            //switch building outlook when click
            if (view != null) {
                for (int i = 0; i < images.length; i++) {
                    if (i != clickedIndex) {
                        ImageView curView = (ImageView) (view.findViewById(buildings[i]));
                        if (curView != null) {
                            curView.setImageLevel(1);
                        }
                    }
                }
            }


            if (checkBuildingExist(clickedIndex)) {
                Toast.makeText(getContext(), buildingList.get(clickedIndex).name, Toast.LENGTH_LONG).show();
                toDetailBuildingView(Integer.toString(clickedId), buildingList.get(clickedIndex));
            } else {
                String text = "Building has not created yet";
                Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
                showBuildingGenerationDialog();
                imgV.setImageLevel(1);
            }
        }
    };

    public boolean checkBuildingExist(int index) {
        for (ReceivedBuilding rb : buildingList) {
            if (rb.index == index) {
                return true;
            }
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_building, container, false);
        session = new SessionManager(getContext().getApplicationContext());
        buildingList = session.getBuildingList();
        buildingApiInterface = ApiClient.getClient().create(BuildingApiInterface.class);
        //set click event listener when the view is gnerated
        for (int building : buildings) {
            ImageView imageView = (ImageView) view.findViewById(building);
            imageView.setOnClickListener(clickListener);
        }
        return view;
    }

    /**
     * Called when the user clicks the Send button
     * Move to detail page
     **/
    private void toDetailBuildingView(String buildingId, ReceivedBuilding rb) {
        Intent intent = new Intent(getActivity(), DetailBuildingActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, buildingId);
        intent.putExtra("id", rb.id);
        intent.putExtra("name", rb.name);
        intent.putExtra("index", rb.index);
        startActivity(intent);
    }

    public void showBuildingGenerationDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = BuildingGenerationDialog.newInstance("BuildingFragment");
        dialog.setTargetFragment(this, DIALOG_FRAGMENT);
        dialog.show(getActivity().getSupportFragmentManager(), "NoticeDialogFragment");
    }

    public void onBuildingGenerationDialogPositiveClick(String name) {
        //search server
        Call<ReceivedBuilding> call = buildingApiInterface.addBuilding(session.getToken(), name, clickedIndex);
        call.enqueue(new Callback<ReceivedBuilding>() {
            @Override
            public void onResponse(Call<ReceivedBuilding> call, Response<ReceivedBuilding> response) {
                if (!response.isSuccessful()) {
                    try {
                        Log.e(TAG, response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, "failed to get building info");
                    } finally {
                        return;
                    }
                }
                String name = response.body().name;
                String id = response.body().id;
                int index = response.body().index;
                ReceivedBuilding rb = new ReceivedBuilding(id, name, index, session.getToken());
                buildingList.add(rb);
                session.updateBuilding(rb);
            }

            @Override
            public void onFailure(Call<ReceivedBuilding> call, Throwable t) {
                Log.d(TAG, "onFailure");
                Log.e(TAG, t.toString());
            }
        });

    }


}


