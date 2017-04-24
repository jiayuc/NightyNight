package com.example.huanglisa.nightynight.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.huanglisa.nightynight.activities.MainActivity;
import com.example.huanglisa.nightynight.dialogs.BuildingGenerationDialog;
import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.SessionManager;
import com.example.huanglisa.nightynight.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by jiayu on 4/23/2017.
 */

public class MessagePagerFragment extends Fragment {
    public static final String TAG = "MessagePagerFragment";
    private SessionManager session;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {


        this.session = new SessionManager(getContext().getApplicationContext());
        this.mainActivity = (MainActivity)this.getActivity();

        Log.d(TAG, this.session.getEmail());
        Log.d(TAG, this.mainActivity.getSelectedFriendToMessage());

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        // send msg
        FloatingActionButton fab =
                (FloatingActionButton) view.findViewById(R.id.fab_f);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) getActivity().findViewById(R.id.input_f);
                Log.d(TAG, input.getText().toString());
                // Clear the input
                input.setText("");
            }
        });
        return view;
    }

}
