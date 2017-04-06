package com.example.huanglisa.nightynight;

/**
 * Created by huanglisa on 11/16/16.
 */
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.UserApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AlarmReceiver extends BroadcastReceiver {

    private final String TAG = "AlarmReceiver";
    private UserApiInterface userApiInterface;
    private SessionManager session;
    private static final String KEY_STATUS= "status";
    private boolean status;

    @Override
    public void onReceive(Context context, Intent intent) {


        status = intent.getExtras().getBoolean(KEY_STATUS);
        Log.d(TAG, "onReceive: "+status + ", current time" + System.currentTimeMillis());
        userApiInterface = ApiClient.getClient().create(UserApiInterface.class);
        session = new SessionManager(context.getApplicationContext());
        changeUserStatus(status);
        if(status){//awake
            Notification(context, "time to wake up", 0);
        } else {
            Notification(context, "time to sleep", 1);
        }

    }


    public void Notification(Context context, String message, int signal) {
        // Set Notification Title
        String strtitle = "Reminder";
        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(context, MainActivity.class);
        // Send data to NotificationView Class

        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, signal, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                // Set Icon
                .setSmallIcon(R.drawable.ic_alarm_clock)
                // Set Ticker Message
                .setTicker(message)
                // Set Title
                .setContentTitle(strtitle)
                // Set Text
                .setContentText(message)
                // Add an Action Button below Notification
                //.addAction(R.drawable.ic_launcher, "Action Button", pIntent)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Dismiss Notification
                .setAutoCancel(true);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }

    public void changeUserStatus(boolean status){
        Call<User> call = userApiInterface.userUpdateStatus(session.getToken(), status);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    Log.e(TAG, "failed to change user status");
                    try{
                        Log.e(TAG, response.errorBody().string());
                    } catch(IOException e){
                        Log.e(TAG, e.getMessage());
                    }
                    return;
                }
                Log.d(TAG, "get user new status: " + response.body().status);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }




}