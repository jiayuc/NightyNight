package com.example.huanglisa.nightynight;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


/**
 * Created by huanglisa on 11/26/16.
 */

public class UserInfoEditingDialog extends DialogFragment {
    private static final String TAG = "BuildingGenDialog";

    // Use this instance of the interface to deliver action events
    SettingActivity mListener;
    SessionManager session;
    String type;

    public static UserInfoEditingDialog newInstance(String type) {
        UserInfoEditingDialog f = new UserInfoEditingDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();

        f.setArguments(args);
        args.putString("type", type);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        type = getArguments().getString("type");
        mListener = (SettingActivity) getActivity();


        //session
        session = new SessionManager(getContext().getApplicationContext());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_user_info_editing, null);
        final TextInputEditText editInput = (TextInputEditText) view.findViewById(R.id.edit);
        editInput.setHint(type);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int int_id) {
                        String name = editInput.getText().toString();
                        if (name.length() == 0) {
                            Toast.makeText(getContext(), "please type in valid name", Toast.LENGTH_LONG).show();
                        }

                        mListener.onUserInfoEditingDialogPositiveClick(type, name);

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //mListener.onDialogNegativeClick(FriendEmailSearchFragment.this);
                        UserInfoEditingDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}

