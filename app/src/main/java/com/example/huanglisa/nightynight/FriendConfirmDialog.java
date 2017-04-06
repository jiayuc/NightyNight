package com.example.huanglisa.nightynight;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.FriendApiInterface;
import com.example.huanglisa.nightynight.rest.UserApiInterface;

/**
 * Created by huanglisa on 11/20/16.
 */

public class FriendConfirmDialog extends DialogFragment {
    // Use this instance of the interface to deliver action events
    FriendFragment mListener;
    String friendName, friendEmail;
    boolean friendStatus;
    TextView friendNameView, friendEmailView;

    private SessionManager session;
    private FriendApiInterface friendApiInterface;

    public static FriendConfirmDialog newInstance(String email, String name, boolean status) {
        FriendConfirmDialog f = new FriendConfirmDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("email", email);
        args.putBoolean("status", status);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getContext().getApplicationContext());
        friendApiInterface = ApiClient.getClient().create(FriendApiInterface.class);

        if(getArguments() == null){
            System.out.format("arguments are null !!!%n");
        }
        friendEmail = getArguments().getString("email");
        friendName = getArguments().getString("name");
        friendStatus = getArguments().getBoolean("status");
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        System.out.format("oncreatedialog%n");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mListener = (FriendFragment)getTargetFragment();



        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_friend_confirm_dialog, null);
        friendEmailView = (TextView)view.findViewById(R.id.friend_email);
        friendNameView= (TextView)view.findViewById(R.id.friend_name);
        friendEmailView.setText(friendEmail);
        friendNameView.setText(friendName);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int int_id) {

                        mListener.onFriendConfirmDialogPositiveClick(true, friendEmail, friendName, friendStatus);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(FriendConfirmDialog.this);
                        FriendConfirmDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
