package com.example.huanglisa.nightynight.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huanglisa.nightynight.activities.MainActivity;
import com.example.huanglisa.nightynight.R;
import com.example.huanglisa.nightynight.SessionManager;
import com.example.huanglisa.nightynight.models.ChatMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jiayu on 4/23/2017.
 */

public class MessageFragment extends Fragment {
    public static final String TAG = "MessageFragment";

    private boolean sessionAttached = false;
    private TextView receiverNameTextView;

    private String myEmail = "";
    private String myName = "";
    private String receiverEmail = "";
    private String receiverName = "";

    private FirebaseAuth mAuth;
    private FirebaseListAdapter<ChatMessage> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        sessionAttached = true;
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        receiverNameTextView = (TextView) view.findViewById(R.id.receiverName);
        return view;
    }

    @Override
    public void onDestroyView(){
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
        sessionAttached = false;
        myEmail = "";
        myName = "";
        receiverEmail = "";
        receiverName = "";
    }


        @Override
    public void onStart(){
        super.onStart();
        prepareChat();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser)
            return;
        if (sessionAttached)
            prepareChat();
    }

    private void prepareChat(){
        Log.d(TAG, "prepareChat");

        //update sender and receiver information
        getSenderReceiver();

        //show receiver name on screen
        if(receiverName.equals("")){
            receiverNameTextView.setText("Please select a friend to message");
        }else{
            receiverNameTextView.setText(receiverName);
        }

        if(receiverEmail.equals(""))
            return;

        //get chatroom string
        String chatRoom = setChatRoomInfo();

        Log.d(TAG, "update chat history");
        contactFirebase(chatRoom);
    }

    private void getSenderReceiver(){
        SessionManager session = new SessionManager(getContext().getApplicationContext());
        MainActivity mainActivity = (MainActivity)getActivity();

        if (myEmail.equals(session.getEmail()))
            if(receiverEmail.equals(mainActivity.getSelectedFriendEmail()))
                return;
        myEmail = session.getEmail();
        myName = session.getName();
        receiverEmail = mainActivity.getSelectedFriendEmail();
        receiverName = mainActivity.getSelectedFriendName();
    }

    private String setChatRoomInfo(){
        String chatRoom;
        if(myEmail.compareTo(receiverEmail) < 0)
            chatRoom = myEmail + receiverEmail;
        else
            chatRoom = receiverEmail + myEmail;
        return chatRoom.replaceAll("[.#$]","-");
    }

    private void contactFirebase(final String chatRoom){
        mAuth = FirebaseAuth.getInstance();
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

        FloatingActionButton fab =
                (FloatingActionButton) getView().findViewById(R.id.fab_f);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject messageJson = new JSONObject();
                try {
                    EditText input = (EditText) getActivity().findViewById(R.id.input_f);
                    messageJson.put("message", input.getText().toString());
                    messageJson.put("sender", myName);
                    input.setText("");
                } catch (JSONException e) {
                    Log.e(TAG, "can't encode message+sender as json");
                }

                //send chat message to server
                FirebaseDatabase.getInstance()
                        .getReference().child(chatRoom)
                        .push()
                        .setValue(new ChatMessage(messageJson.toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );
            }
        });

    }

    private void displayChatMessages(String chatRoom) {
        ListView listOfMessages = (ListView) getView().findViewById(R.id.list_of_messages_f);

        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child(chatRoom)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                try {
                    JSONObject messageJson = new JSONObject(model.getMessageText());
                    // Set their text
                    messageText.setText(messageJson.getString("message"));
                    messageUser.setText(messageJson.getString("sender"));

                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                            model.getMessageTime()));
                } catch (JSONException e) {
                    Log.e(TAG, "can't decode message+sender json from server");
                    Log.e(TAG, model.getMessageText());
                }
            }
        };

        listOfMessages.setAdapter(adapter);
    }

}
