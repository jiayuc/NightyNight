package com.example.huanglisa.nightynight.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.huanglisa.nightynight.fragments.SleepClockFragment;
import com.example.huanglisa.nightynight.fragments.WakeClockFragment;
import com.example.huanglisa.nightynight.models.ClockItem;
import com.example.huanglisa.nightynight.utils.ClockMsgPacker;

/**
 * Created by huanglisa on 11/1/16.
 */

public class PagerClockSetterAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private String oldClockEncoded; // encoded old clock

    public PagerClockSetterAdapter(FragmentManager fm, int NumOfTabs, String params) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.oldClockEncoded = params;
    }

    /**
     * method for switch fragment for the pager
     *
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                SleepClockFragment tab1 = new SleepClockFragment();
                Bundle args = new Bundle();
                args.putString("oldClockEncoded", oldClockEncoded);
                tab1.setArguments(args);
                return tab1;
            case 1:
                WakeClockFragment tab2 = new WakeClockFragment();
                Bundle args2 = new Bundle();
                args2.putString("oldClockEncoded", oldClockEncoded);
                tab2.setArguments(args2);
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}