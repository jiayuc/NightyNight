package com.example.huanglisa.nightynight;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
/**
 * Created by huanglisa on 11/1/16.
 */

public class PagerClockSetterAdapter extends FragmentStatePagerAdapter{
    int mNumOfTabs;

    public PagerClockSetterAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    /**
     * method for switch fragment for the pager
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                SleepClockFragment tab1 = new SleepClockFragment();
                return tab1;
            case 1:
                WakeClockFragment tab2 = new WakeClockFragment();
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