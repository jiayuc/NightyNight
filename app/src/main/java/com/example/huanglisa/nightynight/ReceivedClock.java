package com.example.huanglisa.nightynight;

/**
 * Created by huanglisa on 11/15/16.
 */

public class ReceivedClock {
    public int sleepHour;
    public int sleepMin;
    public int wakeHour;
    public int wakeMin;
    public String id;

    public ReceivedClock(int sleepHour, int sleepMin, int wakeHour, int wakeMin, String id){
        this.sleepHour = sleepHour;
        this.sleepMin = sleepMin;
        this.wakeHour = wakeHour;
        this.wakeMin = wakeMin;
        this.id = id;
    }
}
