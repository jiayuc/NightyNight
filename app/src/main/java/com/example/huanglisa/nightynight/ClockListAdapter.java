package com.example.huanglisa.nightynight;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class ClockListAdapter extends RecyclerView.Adapter<ClockListAdapter.ClockListViewHolder> {

    private List<ClockItem> clockList;
    private RecyclerViewSwitchListener listener;


    public class ClockListViewHolder extends RecyclerView.ViewHolder {
        public TextView sleepTime, wakeTime;
        public StatusSwitch onOff;

        public ClockListViewHolder(View view, final RecyclerViewSwitchListener listener) {
            super(view);
            sleepTime = (TextView) view.findViewById(R.id.sleepTime);
            wakeTime = (TextView) view.findViewById(R.id.wakeTime);
            onOff = (StatusSwitch) view.findViewById(R.id.onOff);

            onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if(listener != null && onOff.checkNeedUpdate()){
                        listener.onViewSwitched(getAdapterPosition());
                    }
                }
            });
        }
    }


    public ClockListAdapter(List<ClockItem> clockList, RecyclerViewSwitchListener listener) {
        this.clockList = clockList;
        this.listener = listener;
    }

    @Override
    public ClockListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.clock_list_row, parent, false);

        return new ClockListViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(ClockListViewHolder holder, int position) {

        //setNeedUpdate(false);
        ClockItem clockItem = clockList.get(position);
        //System.out.format("clocklist length: %d%n", clockList.size());
        //System.out.format("onBindViewHolder: position: %d,  %s; %s%n", position, clockItem.getSleepTime(), clockItem.getWakeupTime());
        holder.sleepTime.setText(clockItem.getSleepTime());
        holder.wakeTime.setText(clockItem.getWakeupTime());

        holder.onOff.setChecked(clockItem.getStatus(), false);

    }


    @Override
    public int getItemCount() {
        return clockList.size();
    }
}
