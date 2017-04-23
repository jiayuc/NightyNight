package com.example.huanglisa.nightynight.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.huanglisa.nightynight.dialogs.BuildingGenerationDialog;
import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.SessionManager;

/**
 * Created by jiayu on 4/23/2017.
 */

public class MessagePagerFragment extends Fragment {
    public static final int DIALOG_FRAGMENT = 1;
    public static final String TAG = "FriendPager";
    private SessionManager session;
    private int size;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {


//        View view = inflater.inflate(R.layout.pager_message, container, false);
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

//        ViewPager pager = (ViewPager) view.findViewById(R.id.message_pager);
//        pager.setAdapter(buildAdapter());

//        session = new SessionManager(getContext().getApplicationContext());
//        if (true) {
//            showBuildingGenerationDialog();
//        }

        return (rootView);
    }

//    private PagerAdapter buildAdapter() {
//        return (new FriendViewAdapter(getActivity(), getChildFragmentManager()));
//    }

    public void showBuildingGenerationDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = BuildingGenerationDialog.newInstance("MessagePagerFragment");
        dialog.setTargetFragment(this, DIALOG_FRAGMENT);
        dialog.show(getActivity().getSupportFragmentManager(), "NoticeDialogFragment");
    }


    public void reloadFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}
