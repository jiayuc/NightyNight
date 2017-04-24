package com.example.huanglisa.nightynight.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.huanglisa.nightynight.R;

public class individualActivity extends AppCompatActivity {

    private TextView name, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);

        name = (TextView) findViewById(R.id.name);
        status = (TextView) findViewById(R.id.status);
        Intent intent = getIntent();

        name.setText(intent.getExtras().getString("name"));
        status.setText(convertStatus(intent.getExtras().getBoolean("status")));

        Toolbar toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
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
