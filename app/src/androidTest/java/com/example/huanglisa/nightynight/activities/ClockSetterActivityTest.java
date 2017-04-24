package com.example.huanglisa.nightynight.activities;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.huanglisa.nightynight.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

/**
 * Created by jiayu on 4/24/2017.
 */
@RunWith(AndroidJUnit4.class)
public class ClockSetterActivityTest {

    @Rule
    public ActivityTestRule<ClockSetterActivity> mActivityRule = new ActivityTestRule<>(
            ClockSetterActivity.class);

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void onCreate() throws Exception {
        try {
            onView(withId(R.id.my_toolbar)).perform(click());
        } catch (NoMatchingViewException e) {
            assertEquals("R.id.my_toolbar created", "R.id.my_toolbar not created");
        }
    }

    @Test
    public void getClockItem() throws Exception {

    }


}