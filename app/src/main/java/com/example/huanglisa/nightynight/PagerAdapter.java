package com.example.huanglisa.nightynight;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
/**
 * Created by huanglisa on 11/1/16.
 */

public class PagerAdapter extends FragmentStatePagerAdapter{
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
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
                System.out.format("case 0%n");
                BuildingFragment tab1 = new BuildingFragment();
                return tab1;
            case 1:
                System.out.format("case 1%n");
                ClockFragment tab2 = new ClockFragment();
                return tab2;
            case 2:
                System.out.format("case 2%n");
                FriendPagerFragment tab3 = new FriendPagerFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

