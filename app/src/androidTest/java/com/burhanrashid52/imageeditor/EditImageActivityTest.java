package com.burhanrashid52.imageeditor;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditImageActivityTest {

    @Rule
    public ActivityTestRule<EditImageActivity> mActivityRule = new ActivityTestRule<>(EditImageActivity.class);

    @Test
    public void checkIfActivityIsLaunched() {
        onView(withText(R.string.app_name)).check(matches(isDisplayed()));
    }
}