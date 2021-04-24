package com.burhanrashid52.photoediting;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
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
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;


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

    @Test
    public void checkIfOnlyOneStickerCanBeSelectedAtATime() {

        mActivityRule.launchActivity(null);

        // Add the first emoji to the editor
        onView(withText(R.string.label_emoji)).perform(click());
        onView(withId(R.id.rvEmoji))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Move the first emoji to the left.
        // NOTE(lucianocheng): I tried to do a SwipeAction here, but using the swipe to move
        //                     the element turned out to be very difficult in practice.
        final View firstEmojiStickerFrameBorder = mActivityRule.getActivity().findViewById(R.id.frmBorder);
        ((FrameLayout) firstEmojiStickerFrameBorder.getParent()).setX(0);

        // Add the second emoji to the editor
        onView(withIndex(withText(R.string.label_emoji), 1)).perform(click());
        onView(withId(R.id.rvEmoji))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // Assert that the first emoji is not selected (frame background is null)
        onView(withIndex(withId(R.id.frmBorder), 0)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                assertNull(((FrameLayout) view).getBackground());
            }
        });

        // Assert that the second emoji is selected (frame background is not null)
        onView(withIndex(withId(R.id.frmBorder), 1)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                assertNotNull(null, ((FrameLayout) view).getBackground());
            }
        });
    }

    // Helper class for matching with multiple elements with the same ID
    // NOTE(lucianocheng): All emoji / stickers have the same views, so we need this to retrieve
    //                     them in espresso and avoid AmbiguousViewMatcherException
    //                     Taken from https://stackoverflow.com/a/41967652/1015951
    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }
}