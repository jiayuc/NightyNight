package com.example.huanglisa.nightynight.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.huanglisa.nightynight.models.ClockItem;
import com.example.huanglisa.nightynight.CustomViewPager;
import com.example.huanglisa.nightynight.adapters.PagerClockSetterAdapter;
import com.example.huanglisa.nightynight.R;

public class ClockSetterActivity extends AppCompatActivity {
    public CustomViewPager viewPager;
    public ClockItem clockItem = new ClockItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_setter);

        //create back arrow
        Toolbar toolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);

        //create pager
        viewPager = (CustomViewPager) findViewById(R.id.pager);
        PagerClockSetterAdapter pageAdapter = new PagerClockSetterAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(pageAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setPagingEnabled(false);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int index = viewPager.getCurrentItem();
        if (index == 0) {
            System.out.format("return from sleep");
            finish();
        } else {
            viewPager.setCurrentItem(0);
        }
        return true;
    }

    public ClockItem getClockItem() {
        return this.clockItem;
    }


}
