package com.burhanrashid52.photoeditor;

import android.content.Context;
import android.net.Uri;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.PhotoEditor;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditImageActivityTest {

    @Rule
    public ActivityTestRule<EditImageActivity> mActivityRule = new ActivityTestRule<>(EditImageActivity.class, false, false);

    @Test
    public void checkIfActivityIsLaunched() {
        mActivityRule.launchActivity(null);
        onView(withText(R.string.app_name)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfBrushIsEnabledWhenClickedOnBrushTool() {
        EditImageActivity editImageActivity = mActivityRule.launchActivity(null);
        assertFalse(editImageActivity.mPhotoEditor.getBrushDrawableMode());
        onView(withText(R.string.label_brush)).perform(click());
        assertTrue(editImageActivity.mPhotoEditor.getBrushDrawableMode());
    }

    @Test
    public void checkIfEraserIsEnabledWhenClickedOnEraserTool() {
        mActivityRule.launchActivity(null);
        onView(withText(R.string.label_eraser)).perform(click());
        onView(withText(R.string.label_eraser_mode)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfEmojiIsDisplayedWhenEmojiIsSelected() {
        Context context = mActivityRule.launchActivity(null);
        ArrayList<String> emojis = PhotoEditor.getEmojis(context);
        int emojiPosition = 1;
        String emojiUnicode = emojis.get(emojiPosition);
        onView(withText(R.string.label_emoji)).perform(click());
        onView(withId(R.id.rvEmoji))
                .perform(RecyclerViewActions.actionOnItemAtPosition(emojiPosition, click()));
        onView(withText(emojiUnicode)).check(matches(isDisplayed()));
    }

    @Ignore("Flacky test. Need to optimize")
    public void checkIfDiscardDialogIsNotDisplayedWhenCacheIsEmpty() {
        EditImageActivity editImageActivity = mActivityRule.launchActivity(null);
        assertTrue(editImageActivity.mPhotoEditor.isCacheEmpty());
        onView(withId(R.id.imgClose)).perform(click());
        assertTrue(editImageActivity.isDestroyed());
    }

    @Test
    public void checkIfDiscardDialogIsDisplayedWhenCacheIsNotEmpty() {
        EditImageActivity editImageActivity = mActivityRule.launchActivity(null);
        assertTrue(editImageActivity.mPhotoEditor.isCacheEmpty());
        onView(withText(R.string.label_emoji)).perform(click());
        onView(withId(R.id.rvEmoji))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.imgClose)).perform(click());
        onView(withText(R.string.msg_save_image)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfUndoRedoIsWorkingCorrectWhenClickedOnUndoRedo() throws InterruptedException {
        EditImageActivity editImageActivity = mActivityRule.launchActivity(null);
        ArrayList<String> emojisUnicodes = PhotoEditor.getEmojis(editImageActivity);

        //Add Emoji
        onView(withText(R.string.label_emoji)).perform(click());
        onView(withId(R.id.rvEmoji)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withText(emojisUnicodes.get(0))).check(matches(isDisplayed()));

        // Undo the Emoji
        onView(withId(R.id.imgUndo)).perform(click());
        onView(withText(emojisUnicodes.get(0))).check(doesNotExist());

        // Redo the Emoji
        onView(withId(R.id.imgRedo)).perform(click());
        onView(withText(emojisUnicodes.get(0))).check(matches(isDisplayed()));
    }

    @Test
    public void testWhenNoImageIsSavedThanToastIsVisibleOnClickedOnShareButton() {
        mActivityRule.launchActivity(null);
        onView(withId(R.id.imgShare)).perform(click());
        onView(withText(R.string.msg_save_image_to_share)).check(matches(isDisplayed()));
    }

    @Ignore("Need to Fix this test")
    public void testShareIntentWhenImageIsAvailableOnClickedOnShareButton() throws InterruptedException {
        EditImageActivity editImageActivity = mActivityRule.launchActivity(null);
        editImageActivity.mSaveImageUri = Uri.parse("somethurl");
        Thread.sleep(2000);
        onView(withId(R.id.imgShare)).perform(click());
        //onView(withText(R.string.msg_save_image_to_share)).check(matches(isDisplayed()));
    }
}