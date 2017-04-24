package com.example.huanglisa.nightynight.utils;

import com.example.huanglisa.nightynight.models.ClockItem;

/**
 * Created by jiayu on 4/24/2017.
 */

public class ClockMsgPacker {
    /**
     * Convert clockitem to string format of
     * sleep_hour: sleep_min: wakup_hour: wakeup_min : id
     *
     * @param clock
     * @return string representing clockItem info
     */
    public String clockToString(ClockItem clock) {
        String ret = "";
        ret += Integer.toString(clock.getSleepHour()) + ":";
        ret += Integer.toString(clock.getSleepMin()) + ":";
        ret += Integer.toString(clock.getWakeupHour()) + ":";
        ret += Integer.toString(clock.getWakupMin()) + ":";
        ret += clock.getId();
        return ret;
    }

    /**
     * Convert encoded clock info to ClockItem
     *
     * @param clockStr encoded clock info
     * @return ClockItem
     */
    public ClockItem StringToClock(String clockStr) {
        String[] times = clockStr.split(":");
        System.out.print("split to get times: ");

        ClockItem clock = new ClockItem();
        clock.setSleepHour(Integer.valueOf(times[0]));
        clock.setSleepMin(Integer.valueOf(times[1]));
        clock.setWakeupHour(Integer.valueOf(times[2]));
        clock.setWakeupMin(Integer.valueOf(times[3]));
        clock.setId(times[4]);

        return clock;
    }

}
