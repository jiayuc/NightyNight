package com.example.huanglisa.nightynight;

/**
 * Created by huanglisa on 11/20/16.
 */

public class FriendItem {
    private String id, name;
    private boolean status;

    public FriendItem(){
        this.name = "default name";
        this.status = true;
        this.id = null;
    }

    public FriendItem(String id, String name, boolean status){
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public String getName(){
        return this.name;
    }

    public boolean getStatus(){
        return this.status;
    }

    public String getTextStatus(){
        if(this.status){
            return "awake";
        }
        return "sleep";
    }

}
