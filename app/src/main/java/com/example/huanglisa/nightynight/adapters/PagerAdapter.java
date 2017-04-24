package com.example.huanglisa.nightynight.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.example.huanglisa.nightynight.fragments.BuildingFragment;
import com.example.huanglisa.nightynight.fragments.ClockFragment;
import com.example.huanglisa.nightynight.fragments.FriendPagerFragment;
import com.example.huanglisa.nightynight.fragments.MessagePagerFragment;

/**
 * Created by huanglisa on 11/1/16.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
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
                Log.d("fragment tab switch: ", "case 0");
                BuildingFragment tab1 = new BuildingFragment();
                return tab1;
            case 1:
                Log.d("fragment tab switch: ", "case 1");
                ClockFragment tab2 = new ClockFragment();
                return tab2;
            case 2:
                Log.d("fragment tab switch: ", "case 2 ");
                FriendPagerFragment tab3 = new FriendPagerFragment();
                return tab3;
            case 3:
                Log.d("fragment tab switch: ", "case 3");
                MessagePagerFragment tab4 = new MessagePagerFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

