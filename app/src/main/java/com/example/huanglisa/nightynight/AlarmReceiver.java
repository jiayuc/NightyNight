package com.example.huanglisa.nightynight;

/**
 * Created by huanglisa on 11/16/16.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.UserApiInterface;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AlarmReceiver extends BroadcastReceiver {

    private static final String KEY_STATUS = "status";
    private final String TAG = "AlarmReceiver";
    private UserApiInterface userApiInterface;
    private SessionManager session;
    private boolean status;

    @Override
    /**
     * Called when alarm to sleep or wake up is received
     */
    public void onReceive(Context context, Intent intent) {
        status = intent.getExtras().getBoolean(KEY_STATUS);
        Log.d(TAG, "onReceive: " + status + ", current time" + System.currentTimeMillis());
        userApiInterface = ApiClient.getClient().create(UserApiInterface.class);
        session = new SessionManager(context.getApplicationContext());
        changeUserStatus(status);
        if (status) {//awake
            Notification(context, "time to wake up", 0);
        } else {
            Notification(context, "time to sleep", 1);
        }

    }

    /**
     * Change user status between sleep and awake
     *
     * @param status user sleeping status
     */
    public void changeUserStatus(boolean status) {
        Call<User> call = userApiInterface.userUpdateStatus(session.getToken(), status);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "failed to change user status");
                    try {
                        Log.e(TAG, response.errorBody().string());
                    } catch (IOException e) {
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

    /**
     * Create push notification when alarm goes off
     *
     * @param context
     * @param message
     * @param signal
     */
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
        // Set ringtone
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        builder.setSound(alarmSound);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }


}