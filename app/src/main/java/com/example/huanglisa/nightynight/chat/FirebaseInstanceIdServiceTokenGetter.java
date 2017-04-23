package com.example.huanglisa.nightynight.chat;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by jiayu on 4/17/2017.
 */

/**
 * You can access the token's value by creating a new class which extends FirebaseInstanceIdService .
 * In that class, call getToken within onTokenRefresh , and log the value.
 */
public class FirebaseInstanceIdServiceTokenGetter extends FirebaseInstanceIdService {
    private static String TAG = "Firebase..TokenGetter";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
    }
}
