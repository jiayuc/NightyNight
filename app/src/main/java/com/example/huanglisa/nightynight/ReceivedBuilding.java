package com.example.huanglisa.nightynight;

/**
 * Created by huanglisa on 11/25/16.
 */

public class ReceivedBuilding {
    public String id;
    public String name;
    public String userId;
    public int index;

    public ReceivedBuilding(String id, String name, int index, String userId){
        this.id = id;
        this.name = name;
        this.index= index;
        this.userId = userId;
    }
}
