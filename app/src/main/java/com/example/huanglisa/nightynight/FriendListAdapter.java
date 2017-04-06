package com.example.huanglisa.nightynight;

/**
 * Created by huanglisa on 11/20/16.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;



public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder> {

    private List<FriendItem> friendList;


    public class FriendListViewHolder extends RecyclerView.ViewHolder {
        public TextView name, status;
        public ImageView profile;

        public FriendListViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.friend_name);
            status = (TextView) view.findViewById(R.id.friend_status);
            profile = (ImageView) view.findViewById(R.id.friend_profile);


        }
    }


    public FriendListAdapter(List<FriendItem> friendList) {
        this.friendList = friendList;
    }

    @Override
    public FriendListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_list_row, parent, false);

        return new FriendListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FriendListViewHolder holder, int position) {

        //setNeedUpdate(false);
        FriendItem friendItem = friendList.get(position);
        System.out.format("position: %d, friendList length: %d%n", position, friendList.size());
        holder.name.setText(friendItem.getName());
        holder.status.setText(friendItem.getTextStatus());

    }


    @Override
    public int getItemCount() {
        return friendList.size();
    }
}
