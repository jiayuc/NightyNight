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

import java.util.ArrayList;
import java.util.List;


public class SleepClockFragment extends Fragment {
    private List<ClockItem> clockList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ClockListAdapter clockAdapter;
    private Button nextBtn;
    private TimePicker tp;
    //private static final int REQUEST_ADD = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_clock, container, false);
        System.out.format("sleep%n");
        ((Toolbar)getActivity().findViewById(R.id.my_toolbar)).setTitle("Sleep Time");
        tp = (TimePicker)view.findViewById(R.id.timePicker);
        tp.setIs24HourView(true);

        nextBtn = (Button)view.findViewById(R.id.button);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.format("go to wakeup%n");
                ((ClockSetterActivity)getActivity()).clockItem.setSleepHour(tp.getHour());
                ((ClockSetterActivity)getActivity()).clockItem.setSleepMin(tp.getMinute());
                CustomViewPager vp=(CustomViewPager) getActivity().findViewById(R.id.pager);
                ((Toolbar)getActivity().findViewById(R.id.my_toolbar)).setTitle("Wakeup Time");
                vp.setCurrentItem(1);
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

