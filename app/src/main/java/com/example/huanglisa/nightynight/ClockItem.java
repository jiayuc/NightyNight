package com.example.huanglisa.nightynight;

/**
 * Created by huanglisa on 11/5/16.
 */

public class ClockItem {
    private int sleep_hour, sleep_min, wakup_hour, wakeup_min;
    private boolean ifTurnedOn;
    private String id;

    public ClockItem(){
        this.sleep_hour = 0;
        this.sleep_min = 0;
        this.wakup_hour = 0;
        this.wakeup_min = 0;
        this.ifTurnedOn = false;
        this.id = null;
    }
    public ClockItem(int sl, int sm, int wh, int wm, String id, boolean ifTurnedOn){
        this.sleep_hour = sl;
        this.sleep_min = sm;
        this.wakup_hour = wh;
        this.wakeup_min = wm;
        this.ifTurnedOn = ifTurnedOn;
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    public void setSleepHour(int sleep_hour){
        this.sleep_hour = sleep_hour;
    }

    public void setSleepMin(int sleep_min){
        this.sleep_min = sleep_min;
    }

    public void setWakeupHour(int wakup_hour){
        this.wakup_hour = wakup_hour;
    }

    public void setWakeupMin(int wakeup_min){
        this.wakeup_min = wakeup_min;
    }

    public int getSleepHour(){
        return sleep_hour;
    }

    public int getSleepMin(){
        return sleep_min;
    }

    public int getWakeupHour(){
        return wakup_hour;
    }

    public int getWakupMin(){
        return wakeup_min;
    }

    public String getSleepTime(){
        return getTwoUnit(this.sleep_hour)+":"+getTwoUnit(this.sleep_min);
    }

    public String getWakeupTime(){
        return getTwoUnit(this.wakup_hour)+":"+getTwoUnit(this.wakeup_min);
    }

    private String getTwoUnit(int time){
        String timeString= Integer.toString(time);
        if(timeString.length() == 1){
            timeString = "0" + timeString;
        }
        return timeString;
    }

    public void setStatus(boolean onOff){
        this.ifTurnedOn = onOff;
    }

    public boolean getStatus(){
        return this.ifTurnedOn;
    }
}
