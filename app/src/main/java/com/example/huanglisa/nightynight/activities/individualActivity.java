package com.example.huanglisa.nightynight.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.models.User;
import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.FriendApiInterface;
import com.example.huanglisa.nightynight.rest.UserApiInterface;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class individualActivity extends AppCompatActivity {

    private static final String TAG = "individualActivity";
    private TextView name, status, callButton;
    private FriendApiInterface friendApiInterface;
    private TextView messageBtn;
    private CircleImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);

        name = (TextView) findViewById(R.id.name);
        status = (TextView) findViewById(R.id.status);
        callButton = (TextView) findViewById(R.id.call_link);
        messageBtn = (TextView) findViewById(R.id.message_link);
        profilePic = (CircleImageView) findViewById(R.id.profile_image);


        Intent intent = getIntent();

//        name.setText(intent.getExtras().getString("name"));
//        status.setText(convertStatus(intent.getExtras().getBoolean("status")));
        // get friend info
        friendApiInterface = ApiClient.getClient().create(FriendApiInterface.class);
        String friendId = intent.getExtras().getString("friendId");
        displayFriendInfo(friendId);

        Toolbar toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
    }

    /**
     * Get friend info from database, display on activity
     *
     * @param friendId user id of the friend
     */
    private void displayFriendInfo(String friendId) {
        Call<User> call = friendApiInterface.getFriend(friendId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, final Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "displayFriendInfo failure response");
                    try {
                        Log.e(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    } finally {
                        return;
                    }
                }
                name.setText(response.body().name);
                status.setText(convertStatus(response.body().status));
                Glide.with(getApplicationContext()).load(response.body().pictureURL).into(profilePic);

                final String phoneNo = response.body().phone != null ? response.body().phone.toString() : "";
                // make phone call onclick
                callButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.d(TAG, "calling friend");
                        if (!TextUtils.isEmpty(phoneNo)) {
                            String dial = "tel:" + phoneNo;
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                        } else {
                            Toast.makeText(getApplicationContext(), "Empty phone number", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                // send msg onclick
                messageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String message = "Hi " + response.body().name.split(" ")[0];
                        if (!TextUtils.isEmpty(message) && !TextUtils.isEmpty(phoneNo)) {
                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNo));
                            smsIntent.putExtra("sms_body", message);
                            startActivity(smsIntent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Empty phone number", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//                address.setText(response.body().address);
//                phone.setText(response.body().phone);
//                Log.e("getUserInfo", response.body().pictureURL);
//
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public String convertStatus(boolean isAwake) {
        if (isAwake) {
            return "awake";
        } else {
            return "sleep";
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }


}
