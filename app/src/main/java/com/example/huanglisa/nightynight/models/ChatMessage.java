package com.example.huanglisa.nightynight.models;

import java.util.Date;

/**
 * Created by jiayu on 4/23/2017.
 */

public class ChatMessage {

    public boolean isLeftMsg; // if should be displayed at left
    private String messageUserID;
    private String messageText;
    private String messageUser;
    private long messageTime;

    public ChatMessage(String messageText, String messageUser, Boolean isLeftMsg, String messageUserID) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageUserID = messageUserID;
        this.isLeftMsg = isLeftMsg;

        // Initialize to current time
        this.messageTime = new Date().getTime();
    }

    public ChatMessage() {

    }


    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageUserID() {
        return messageUserID;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    @Override
    public String toString() {
        return "messageText: " + messageText + "\nfrom"
                + messageUser + "of id " + messageUserID;

    }
}