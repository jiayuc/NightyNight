package com.example.huanglisa.nightynight.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.example.huanglisa.nightynight.fragments.BuildingFragment;
import com.example.huanglisa.nightynight.fragments.ClockFragment;
import com.example.huanglisa.nightynight.fragments.FriendPagerFragment;
import com.example.huanglisa.nightynight.fragments.MessageFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huanglisa on 11/1/16.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    List<Fragment> mFragments = new ArrayList<Fragment>();

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        for(int i=0; i < NumOfTabs; i++)
            mFragments.add(i, null);
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
                if (mFragments.get(0) != null)
                    return mFragments.get(0);

                Log.d("fragment tab switch: ", "case 0 create");
                BuildingFragment tab1 = new BuildingFragment();
                mFragments.set(0, tab1);
                return tab1;
            case 1:
                Log.d("fragment tab switch: ", "case 1");
                if (mFragments.get(1) != null)
                    return mFragments.get(1);

                Log.d("fragment tab switch: ", "case 1 create");
                ClockFragment tab2 = new ClockFragment();
                mFragments.set(1, tab2);
                return tab2;
            case 2:
                Log.d("fragment tab switch: ", "case 2 ");
                if (mFragments.get(2) != null)
                    return mFragments.get(2);

                Log.d("fragment tab switch: ", "case 2 create");
                FriendPagerFragment tab3 = new FriendPagerFragment();
                mFragments.set(2, tab3);
                return tab3;
            case  3:
                Log.d("fragment tab switch: ", "case 3");
                if (mFragments.get(3) != null)
                    return mFragments.get(3);

                Log.d("fragment tab switch: ", "case 3 create");
                MessageFragment tab4 = new MessageFragment();
                mFragments.set(3, tab4);
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

