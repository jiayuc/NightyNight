package com.example.huanglisa.nightynight.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.huanglisa.nightynight.SessionManager;
import com.example.huanglisa.nightynight.fragments.FriendFragment;

/**
 * Created by huanglisa on 11/20/16.
 */

public class FriendViewAdapter extends FragmentPagerAdapter {
    Context ctxt = null;

    private SessionManager session;


    public FriendViewAdapter(Context ctxt, FragmentManager mgr) {
        super(mgr);
        this.ctxt = ctxt;
        session = new SessionManager(ctxt.getApplicationContext());
    }

    @Override
    public int getCount() {
        int size = session.getBuildingList().size();
        return size;
    }

    @Override
    public Fragment getItem(int position) {
        return (FriendFragment.newInstance(position));
    }


}