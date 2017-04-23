package com.example.huanglisa.nightynight.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.models.ReceivedFriend;
import com.example.huanglisa.nightynight.SessionManager;
import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.UserApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huanglisa on 11/20/16.
 */

public class FriendEmailSearchFragment extends DialogFragment {
    private static final String TAG = "FriendSearchFragment";

    // Use this instance of the interface to deliver action events
    FriendFragment mListener;
    SessionManager session;
    private UserApiInterface userApiInterface;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        mListener = (FriendFragment) getTargetFragment();

        //initialize api
        userApiInterface = ApiClient.getClient().create(UserApiInterface.class);

        //session
        session = new SessionManager(getContext().getApplicationContext());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_friend_email_search, null);
        final TextInputEditText emailInput = (TextInputEditText) view.findViewById(R.id.friend_email);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int int_id) {
                        String email = emailInput.getText().toString();
                        //search server
                        Call<ReceivedFriend> call = userApiInterface.userGetFriend(session.getToken(), email);
                        call.enqueue(new Callback<ReceivedFriend>() {
                            @Override
                            public void onResponse(Call<ReceivedFriend> call, Response<ReceivedFriend> response) {
                                if (!response.isSuccessful()) {
                                    try {
                                        Log.e(TAG, response.errorBody().string());
                                        Toast.makeText(getContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(getActivity(), "failed to get user info", Toast.LENGTH_LONG).show();
                                    } finally {
                                        return;
                                    }
                                }

                                mListener.onFriendSearchDialogPositiveClick(true, response.body().friendId, response.body().name, response.body().status);

                            }

                            @Override
                            public void onFailure(Call<ReceivedFriend> call, Throwable t) {
                                Log.d(TAG, "onFailure");
                                Log.e(TAG, t.toString());
                            }
                        });
                        //String name = "Grayce";
                        //String id = "temp";
                        //mListener.onFriendSearchDialogPositiveClick(true, id, name, email);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(FriendEmailSearchFragment.this);
                        FriendEmailSearchFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

}
