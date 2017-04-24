package com.example.huanglisa.nightynight.activities;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by jiayu on 4/24/2017.
 */
@RunWith(AndroidJUnit4.class)
public class ClockSetterActivityTest {
    @Rule
    public ActivityTestRule mActivityRule = new ActivityTestRule<>(
            ClockSetterActivity.class);

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void onCreate() throws Exception {
        assertEquals("com", "com");
    }

    @Test
    public void onOptionsItemSelected() throws Exception {

    }

    @Test
    public void getClockItem() throws Exception {

    }

}