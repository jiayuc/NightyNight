package com.example.huanglisa.nightynight.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.SessionManager;
import com.example.huanglisa.nightynight.activities.MainActivity;
import com.example.huanglisa.nightynight.models.ChatMessage;
import com.example.huanglisa.nightynight.models.User;
import com.example.huanglisa.nightynight.rest.ApiClient;
import com.example.huanglisa.nightynight.rest.FriendApiInterface;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jiayu on 4/23/2017.
 */

public class MessageFragment extends Fragment {
    public static final String TAG = "MessageFragment";

    private boolean sessionAttached = false;
    private TextView receiverNameTextView;

    private String myEmail = "";
    private String myId = "";
    private String receiverId = "";
    private String receiverName = "";

    private FirebaseAuth mAuth;
    private FirebaseListAdapter<ChatMessage> adapter;
    private boolean leftMsg = true;

    private FriendApiInterface friendApiInterface;
    private CircleImageView profilePic, statusImg;

    /**
     * Set a hint to the system about whether this fragment's UI is currently visible to the user.
     * This hint defaults to true and is persistent across fragment instance state save and restore.
     *
     * @param isVisibleToUser indicate whether is visible to user
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser)
            return;
        if (sessionAttached)
            prepareChat();
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (default)
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        sessionAttached = true;
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        // get view elems
        receiverNameTextView = (TextView) view.findViewById(R.id.receiverName);
        profilePic = (CircleImageView) view.findViewById(R.id.profile_image);
        statusImg = (CircleImageView) view.findViewById(R.id.status_img);
        return view;
    }

    /**
     * Called when fragment starts
     */
    @Override
    public void onStart() {
        super.onStart();
        prepareChat();
    }

    /**
     * Called when fragment view is destroyed
     */
    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
        sessionAttached = false;
        myEmail = "";
        myId = "";
        receiverId = "";
        receiverName = "";
    }

    /**
     * Retrieve chat history from firebase
     */
    private void prepareChat() {
        Log.d(TAG, "prepareChat");

        //update sender and receiver information
        getSenderReceiver();

        //show receiver name on screen
        if (receiverName.equals("")) {
            receiverNameTextView.setText("Please select a friend to chat");
        } else {
            displayFriendInfo();
        }

        if (receiverId.equals(""))
            return;

        // get chatroom string
        String chatRoom = setChatRoomInfo();
        Log.d(TAG, "update chat history");
        contactFirebase(chatRoom);
    }

    /**
     * Get sender/receiver emails from the activity
     */
    private void getSenderReceiver() {
        SessionManager session = new SessionManager(getContext().getApplicationContext());
        MainActivity mainActivity = (MainActivity) getActivity();

        if (myEmail.equals(session.getEmail()))
            if (receiverId.equals(mainActivity.getSelectedFriendEmail()))
                return;
        myEmail = session.getEmail();
        Log.d("My email is", myEmail);

        myId = session.getName();
        receiverId = mainActivity.getSelectedFriendEmail();
        receiverName = mainActivity.getSelectedFriendName();
    }

    private void displayFriendInfo() {
        receiverNameTextView.setText(receiverName);
        // get profile pic to display
        friendApiInterface = ApiClient.getClient().create(FriendApiInterface.class);
        Call<User> call = friendApiInterface.getFriend(receiverId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, final Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "displayFriendInfo failure response");
                    try {
                        Log.e(TAG, response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    } finally {
                        return;
                    }
                }
                // load status picture and profile pic
                Glide.with(getContext()).load(response.body().pictureURL).into(profilePic);
                if (response.body().status == true) { //awake
                    statusImg.setImageResource(R.drawable.status_awake);
                    statusImg.setBorderColor(Color.rgb(2, 115, 30)); // green
                } else {
                    statusImg.setImageResource(R.drawable.status_sleep);
                    statusImg.setBorderColor(Color.DKGRAY);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    /**
     * Calculate chatroom name from sender/receiver emails
     * @return chatroom name
     */
    private String setChatRoomInfo() {
        String chatRoom;
        if (myEmail.compareTo(receiverId) < 0)
            chatRoom = myEmail + receiverId;
        else
            chatRoom = receiverId + myEmail;
        return chatRoom.replaceAll("[.#$]", "-");
    }

    /**
     * Contact firebase database, get data on given chatroom
     * @param chatRoom
     */
    private void contactFirebase(final String chatRoom) {
        mAuth = FirebaseAuth.getInstance();
        // signin Anoymously for now
        mAuth.signInAnonymously()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInAnonymously: success");
                            // load chat messages from server
                            displayChatMessages(chatRoom);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously: failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // the send button
        FloatingActionButton fab =
                (FloatingActionButton) getView().findViewById(R.id.fab_f);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject messageJson = new JSONObject();
                try {
                    EditText input = (EditText) getActivity().findViewById(R.id.input_f);
                    messageJson.put("message", input.getText().toString());
                    messageJson.put("sender", myId);
                    input.setText("");
                } catch (JSONException e) {
                    Log.e(TAG, "can't encode message+sender as json");
                }
                Log.d(TAG, "messageJson");
                System.err.print(messageJson);

                //send chat message to server
                FirebaseDatabase.getInstance()
                        .getReference().child(chatRoom)
                        .push()
                        .setValue(new ChatMessage(messageJson.toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                leftMsg, myEmail));
                Log.d(myEmail, "send a msg" + messageJson.toString());
                // flip display side
                leftMsg = !leftMsg;
            }
        });

    }

    /**
     * Display retrived chat history to activity
     * @param chatRoom
     */
    private void displayChatMessages(String chatRoom) {
        ListView listOfMessages = (ListView) getView().findViewById(R.id.list_of_messages_f);

        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class,
                R.layout.activity_clock_setter, FirebaseDatabase.getInstance().getReference().child(chatRoom)) {
            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
                ChatMessage model = getItem(position);

                if (Objects.equals(model.getMessageUserID(), myEmail)) {
                    Log.d(TAG, "My msg");
                    view = mActivity.getLayoutInflater().inflate(R.layout.right, viewGroup, false);
                } else {
                    view = mActivity.getLayoutInflater().inflate(R.layout.left, viewGroup, false);
                }


                // Call out to subclass to marshall this model into the provided view
                populateView(view, model, position);
                return view;
            }

            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
                TextView msgText = (TextView) v.findViewById(R.id.msgr);

                try {
                    JSONObject messageJson = new JSONObject(model.getMessageText());
                    msgText.setText(messageJson.getString("message"));
                    //  Format the date before showing it
                    messageTime.setText(DateFormat.format("MM-dd-yyyy (HH:mm:ss)",
                            model.getMessageTime()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        listOfMessages.setAdapter(adapter);
    }

}
