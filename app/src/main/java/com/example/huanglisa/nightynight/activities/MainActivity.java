package com.example.huanglisa.nightynight.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.huanglisa.nightynight.CustomViewPager;
import com.example.huanglisa.nightynight.EnhancedMenuInflater;
import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.SessionManager;
import com.example.huanglisa.nightynight.adapters.PagerAdapter;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public SessionManager session;
    Toolbar toolbar;
    private MobileServiceClient mClient;
    private String selectedFriendEmail = "";
    private String selectedFriendName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //connent to server
        try {
            mClient = new MobileServiceClient("https://nighty-night.azurewebsites.net", this);
        } catch (MalformedURLException ex) {
            Log.e("Service URL malformed!", ex.getMessage());
        }


        //switch from the splash page
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        session = new SessionManager(getApplicationContext());
        session.checkLogin();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //pager
        final CustomViewPager viewPager = (CustomViewPager) findViewById(R.id.pager);
        PagerAdapter pageAdapter = new PagerAdapter(getSupportFragmentManager(), 4);
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setPagingEnabled(false);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (toolbar == null) {
            EnhancedMenuInflater.inflate(getMenuInflater(), menu, false);
        }
        if (toolbar != null) {
            EnhancedMenuInflater.inflate(getMenuInflater(), toolbar.getMenu(), true);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onOptionsItemSelected(item);
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_building:
                Log.d(TAG, "action_building");
                createTabIntent(0);
                break;
            case R.id.action_clock:
                Log.d(TAG, "action_clock");
                createTabIntent(1);
                break;
            case R.id.action_users:
                Log.d(TAG, "action_users");
                createTabIntent(2);
                break;
            case R.id.action_message:
                Log.d(TAG, "action_message");
                createTabIntent(3);
                break;
            case R.id.action_logout:
                session.logoutUser();
                break;
            case R.id.action_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                intent.putExtra("token", session.getToken());
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void createTabIntent(int index) {
        ((ViewPager) findViewById(R.id.pager)).setCurrentItem(index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Override this method in the activity that hosts the Fragment and call super
        // in order to receive the result inside onActivityResult from the fragment.
        super.onActivityResult(requestCode, resultCode, data);
    }

    //the getter/setter is used to propagate information from FriendFragment to MessageFragment
    public String getSelectedFriendEmail() {
        return selectedFriendEmail;
    }

    public void setSelectedFriendEmail(String email) {
        selectedFriendEmail = email;
    }

    public String getSelectedFriendName() {
        return selectedFriendName;
    }

    public void setSelectedFriendName(String name) {
        selectedFriendName = name;
    }

}