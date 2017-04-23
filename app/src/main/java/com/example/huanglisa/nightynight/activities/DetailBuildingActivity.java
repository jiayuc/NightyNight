package com.example.huanglisa.nightynight.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.models.ReceivedFriend;
import com.example.huanglisa.nightynight.SessionManager;
import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.FriendApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailBuildingActivity extends AppCompatActivity {

    private final String TAG = "DetailBuildingActivity";
    private int[] curtains = {
            R.id.curtain1,
            R.id.curtain2,
            R.id.curtain3,
            R.id.curtain4,
            R.id.curtain5,
            R.id.curtain6,
            R.id.curtain7,
            R.id.curtain8,
            R.id.curtain9,
            R.id.curtain10,
            R.id.curtain11,
            R.id.curtain12,
            R.id.curtain13,
            R.id.curtain14,
            R.id.curtain15,
    };
    private Intent intent;
    private String serverBuildingId;
    private FriendApiInterface friendApiInterface;
    private SessionManager session;
    private List<ReceivedFriend> friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_building);
        Toolbar toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolBar.setTitleTextColor(0xFFFFFFFF);

        //get friend list
        session = new SessionManager(getApplicationContext());
        intent = getIntent();
        serverBuildingId = intent.getExtras().getString("id");
        friendApiInterface = ApiClient.getClient().create(FriendApiInterface.class);
        friendList = new ArrayList<>();
        Log.d(TAG, "serverBuildingId" + serverBuildingId);
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
                friendList.addAll(response.body());
            }

            @Override
            public void onFailure(Call<List<ReceivedFriend>> call, Throwable t) {
                Log.e(TAG, t.getMessage().toString());
            }
        });

        if (friendList.size() >= 15) {
            Log.e(TAG, "friend list size is larger than limit 15");
        }
        for (int i = 0; i < friendList.size(); i++) {
            ReceivedFriend friend = friendList.get(i);
            if (friend.status) { //awake
                ((ImageView) findViewById(curtains[i])).setImageResource(R.drawable.curtain_light);
            }
        }


        for (int i = 0; i < curtains.length; i++) {
            int id = curtains[i];
            ImageView curtain = (ImageView) findViewById(id);
            final int index = i;
            curtain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (index < friendList.size()) {
                        Intent intent = new Intent(getApplicationContext(), individualActivity.class);
                        intent.putExtra("friendId", friendList.get(index).friendId);
                        intent.putExtra("name", friendList.get(index).name);
                        intent.putExtra("status", friendList.get(index).status);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "no friend lives here", Toast.LENGTH_LONG).show();
                    }
                    //finish();
                }
            });
        }


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
