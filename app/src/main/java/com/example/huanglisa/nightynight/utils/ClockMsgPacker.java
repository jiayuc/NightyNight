package com.example.huanglisa.nightynight.utils;

import com.example.huanglisa.nightynight.models.ClockItem;

/**
 * Created by jiayu on 4/24/2017.
 */

public class ClockMsgPacker {
    /**
     * Convert clockitem to string format of
     * sleep_hour: sleep_min: wakup_hour: wakeup_min
     *
     * @param clock
     * @return string representing clockItem info
     */
    public String clockToString(ClockItem clock) {
        String ret = "";

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
        System.out.print(times);

        ClockItem clock = new ClockItem();
        return clock;
    }

}
