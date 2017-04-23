package com.example.huanglisa.nightynight.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.SessionManager;
import com.example.huanglisa.nightynight.fragments.BuildingFragment;
import com.example.huanglisa.nightynight.fragments.FriendPagerFragment;


/**
 * Created by huanglisa on 11/26/16.
 */

public class BuildingGenerationDialog extends DialogFragment {
    private static final String TAG = "BuildingGenDialog";
    private static final String KEY_BUILDING = "BuildingFragment";
    private static final String KEY_FRIEND = "FriendPagerFragment";
    // Use this instance of the interface to deliver action events
    Fragment mListener;
    SessionManager session;
    String listenerType;

    public static BuildingGenerationDialog newInstance(String listenerType) {
        BuildingGenerationDialog f = new BuildingGenerationDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();

        f.setArguments(args);
        args.putString("listenerType", listenerType);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        listenerType = getArguments().getString("listenerType");
        mListener = getTargetFragment();


        //session
        session = new SessionManager(getContext().getApplicationContext());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_building_generation, null);
        final TextInputEditText nameInput = (TextInputEditText) view.findViewById(R.id.building_name);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int int_id) {
                        String name = nameInput.getText().toString();
                        if (name.length() == 0) {
                            Toast.makeText(getContext(), "please type in valid name", Toast.LENGTH_LONG).show();
                        }
                        if (listenerType == KEY_BUILDING) {
                            ((BuildingFragment) mListener).onBuildingGenerationDialogPositiveClick(name);
                        } else if (listenerType == KEY_FRIEND) {
                            ((FriendPagerFragment) mListener).onBuildingGenerationDialogPositiveClick(name);
                        }

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //mListener.onDialogNegativeClick(FriendEmailSearchFragment.this);
                        BuildingGenerationDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
