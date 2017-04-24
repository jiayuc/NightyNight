package com.example.huanglisa.nightynight.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.huanglisa.nightynight.models.ClockItem;
import com.example.huanglisa.nightynight.CustomViewPager;
import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.activities.ClockSetterActivity;
import com.example.huanglisa.nightynight.adapters.ClockListAdapter;
import com.example.huanglisa.nightynight.utils.ClockMsgPacker;

import java.util.ArrayList;
import java.util.List;


public class SleepClockFragment extends Fragment {
    private static String TAG = "SleepClockFragment";
    private List<ClockItem> clockList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ClockListAdapter clockAdapter;
    private Button nextBtn;
    private TimePicker tp;
    //private static final int REQUEST_ADD = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if( requestCode == REQUEST_ADD ) {
            System.out.format("add %n");
        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // parse passed in data if any
        String oldClockEncoded = getArguments().getString("oldClockEncoded");
        Log.d(TAG, "oldClockEncoded received: " + oldClockEncoded);
        // create view
        View view = inflater.inflate(R.layout.fragment_sleep_clock, container, false);
        System.out.format("sleep%n");
        ((Toolbar) getActivity().findViewById(R.id.my_toolbar)).setTitle("Sleep Time");
        // edit time picker values
        tp = (TimePicker) view.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);
        // if changing exiting clock, display old value
        if (oldClockEncoded != "") {
            ClockMsgPacker packer = new ClockMsgPacker();
            ClockItem clockOld = packer.StringToClock(oldClockEncoded);
            tp.setHour(clockOld.getSleepHour());
            tp.setMinute(clockOld.getSleepMin());
        }

        nextBtn = (Button) view.findViewById(R.id.button);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.format("go to wakeup%n");
                ((ClockSetterActivity) getActivity()).clockItem.setSleepHour(tp.getHour());
                ((ClockSetterActivity) getActivity()).clockItem.setSleepMin(tp.getMinute());
                CustomViewPager vp = (CustomViewPager) getActivity().findViewById(R.id.pager);
                ((Toolbar) getActivity().findViewById(R.id.my_toolbar)).setTitle("Wakeup Time");
                vp.setCurrentItem(1);
            }
        });
        return view;


    }


}

