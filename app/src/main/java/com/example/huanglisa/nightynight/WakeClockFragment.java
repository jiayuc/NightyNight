package com.example.huanglisa.nightynight;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;


public class WakeClockFragment extends Fragment {
    private List<ClockItem> clockList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ClockListAdapter clockAdapter;
    private Button finishBtn;
    private TimePicker tp;
    //private static final int REQUEST_ADD = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wake_clock, container, false);
        System.out.format("wakeup%n");

        //can't call it here since pagers are created when initiated
        //((Toolbar)getActivity().findViewById(R.id.my_toolbar)).setTitle("Wakeup Time");

        finishBtn = (Button)view.findViewById(R.id.button);
        tp = (TimePicker)view.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                ClockItem curItem = ((ClockSetterActivity)getActivity()).clockItem;
                curItem.setWakeupHour(tp.getHour());
                curItem.setWakeupMin(tp.getMinute());

                data.putExtra("sleepHour", curItem.getSleepHour());
                data.putExtra("sleepMin", curItem.getSleepMin());
                data.putExtra("wakeupHour", curItem.getWakeupHour());
                data.putExtra("wakeupMin", curItem.getWakupMin());
                getActivity().setResult(getActivity().RESULT_OK, data);
                getActivity().finish();

            }
        });
        return view;


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if( requestCode == REQUEST_ADD ) {
            System.out.format("add %n");
        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }



}

