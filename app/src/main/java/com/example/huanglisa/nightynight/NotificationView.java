package com.example.huanglisa.nightynight;

/**
 * Created by huanglisa on 11/17/16.
 */
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationView extends Activity {
    // Declare Variable
    String title;
    String text;
    TextView txttitle;
    TextView txttext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_notification);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Dismiss Notification
        notificationmanager.cancel(0);

        // Retrive the data from MainActivity.java
        Intent i = getIntent();

        title = i.getStringExtra("title");
        text = i.getStringExtra("text");

        // Locate the TextView
        txttitle = (TextView) findViewById(R.id.title);
        txttext = (TextView) findViewById(R.id.text);

        // Set the data into TextView
        txttitle.setText(title);
        txttext.setText(text);
    }
}
