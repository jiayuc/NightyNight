package com.example.huanglisa.nightynight.utils;

import com.example.huanglisa.nightynight.models.ClockItem;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by jiayu on 4/24/2017.
 */
public class ClockMsgPackerTest {
    private String EXPECTED_ID = "SOMErandomID123";

    /**
     * Test clockToString converts correctly
     *
     * @throws Exception
     */
    @Test
    public void clockToString() throws Exception {
        int[] times = {12, 49, 23, 52};
        ClockItem clock = new ClockItem(times[0], times[1], times[2], times[3], EXPECTED_ID, false);
        // convert to string
        ClockMsgPacker packer = new ClockMsgPacker();
        String msg = packer.clockToString(clock);
        // verify
        String msgExpected = "";
        for (int time : times) {
            msgExpected += Integer.toString(time) + ":";
        }
        msgExpected += EXPECTED_ID;
        assertEquals(msgExpected, msg);
    }

    /**
     * Test stringToClock converts correctly
     *
     * @throws Exception
     */
    @Test
    public void stringToClock() throws Exception {
        int[] times = {12, 49, 23, 52};
        // construct input string
        String msg = "";
        for (int time : times) {
            msg += Integer.toString(time) + ":";
        }
        msg += EXPECTED_ID;
        // convert to clockItem
        ClockMsgPacker packer = new ClockMsgPacker();
        ClockItem clock = packer.StringToClock(msg);
        // verify four values
        assertEquals(times[0], clock.getSleepHour());
        assertEquals(times[1], clock.getSleepMin());
        assertEquals(times[2], clock.getWakeupHour());
        assertEquals(times[3], clock.getWakupMin());
        assertEquals(EXPECTED_ID, clock.getId());
    }

}