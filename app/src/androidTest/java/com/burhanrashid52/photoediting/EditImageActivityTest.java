package com.burhanrashid52.photoediting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.burhanrashid52.photoediting.EmojiBSFragment.getEmojis;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
        onView(withText(R.string.label_shape)).perform(click());
        assertTrue(editImageActivity.mPhotoEditor.getBrushDrawableMode());
    }

    @Test
    public void checkIfEraserIsEnabledWhenClickedOnEraserTool() {
        mActivityRule.launchActivity(null);
        onView(withText(R.string.label_eraser)).perform(click());
        onView(withText(R.string.label_eraser_mode)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfShapeIsEnabledWhenClickedOnBrushTool() {
        EditImageActivity editImageActivity = mActivityRule.launchActivity(null);
        onView(withText(R.string.label_shape)).perform(click());
        onView(withText(R.string.label_shape)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfEmojiIsDisplayedWhenEmojiIsSelected() {
        Context context = mActivityRule.launchActivity(null);
        ArrayList<String> emojis = getEmojis(context);
        int emojiPosition = 1;
        String emojiUnicode = emojis.get(emojiPosition);
        onView(ViewMatchers.withId(R.id.rvConstraintTools))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(R.string.label_emoji))));
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
        onView(ViewMatchers.withId(R.id.rvConstraintTools))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(R.string.label_emoji))));
        onView(withText(R.string.label_emoji)).perform(click());
        onView(withId(R.id.rvEmoji))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.imgClose)).perform(click());
        onView(withText(R.string.msg_save_image)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfUndoRedoIsWorkingCorrectWhenClickedOnUndoRedo() {
        EditImageActivity editImageActivity = mActivityRule.launchActivity(null);
        ArrayList<String> emojisUnicodes = getEmojis(editImageActivity);

        onView(ViewMatchers.withId(R.id.rvConstraintTools))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(R.string.label_emoji))));
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
    public void checkIfPinchTextScalableFlagWorks_False() throws InterruptedException {

        // Use an intent to tell EditImageActivity to set the PhotoEditor "pinchTextScalableFlag" to "false"
        final Intent intent = new Intent();
        intent.putExtra(EditImageActivity.PINCH_TEXT_SCALABLE_INTENT_KEY, false);
        mActivityRule.launchActivity(intent);

        // Open the emoji menu (delay to give time to load lower menu)
        Thread.sleep(2000);
        onView(ViewMatchers.withId(R.id.rvConstraintTools))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(R.string.label_emoji))));
        onView(withText(R.string.label_emoji)).perform(click());

        // Add an emoji from the menu (delay to give time for the RecyclerView to open)
        Thread.sleep(2000);
        onView(withId(R.id.rvEmoji))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // Select the emoji (delay to give time for the RecyclerView to close)
        Thread.sleep(1000);
        onView(withId((R.id.frmBorder))).perform(click());

        // Capture the scale of the emoji
        ViewGroup emojiFrameParentView = (ViewGroup) mActivityRule.getActivity().findViewById(R.id.frmBorder).getParent();
        final float emojiScaleXBeforePinching = emojiFrameParentView.getScaleX();
        final float emojiScaleYBeforePinching = emojiFrameParentView.getScaleY();

        // Scale the emoji up by pinching
        onView(withId((R.id.frmBorder))).perform(PinchTestHelper.pinchOut());

        // Check if the emoji scaled up after pinching.
        emojiFrameParentView = (ViewGroup) mActivityRule.getActivity().findViewById(R.id.frmBorder).getParent();
        assertNotEquals(emojiScaleXBeforePinching, emojiFrameParentView.getScaleX());
        assertNotEquals(emojiScaleYBeforePinching, emojiFrameParentView.getScaleY());

        // Remove the emoji from the screen.
        onView(withId(R.id.imgPhotoEditorClose)).perform(click());

        // Add a text to the image.
        onView(withText(R.string.label_text)).perform(click());
        onView(withId(R.id.add_text_edit_text)).perform(click());
        onView(withId(R.id.add_text_edit_text)).perform(typeText("Test Text"));

        // Select the text (delay to give time for the text imput screen to close)
        Thread.sleep(2000);
        onView(withId(R.id.add_text_done_tv)).perform(click());

        // Select the text box
        Matcher<View> testTextBox = withId(R.id.tvPhotoEditorText);
        onView(testTextBox).perform(click());

        // Capture the current scale of the text box
        ViewGroup textFrameParentView = (ViewGroup) mActivityRule.getActivity().findViewById(R.id.frmBorder).getParent();
        final float textScaleXBeforeScaling = textFrameParentView.getScaleX();
        final float textScaleYBeforeScaling = textFrameParentView.getScaleY();

        // Attempt to scale the text box by pinching
        onView(testTextBox).perform(PinchTestHelper.pinchOut());

        // Validate that the text box did not scale by pinching.
        textFrameParentView = (ViewGroup) mActivityRule.getActivity().findViewById(R.id.frmBorder).getParent();
        assertEquals(textScaleXBeforeScaling, textFrameParentView.getScaleX(), 0.01);
        assertEquals(textScaleYBeforeScaling, textFrameParentView.getScaleY(), 0.01);
    }

    @Test
    public void checkIfPinchTextScalableFlagWorks_True() throws InterruptedException {

        // Use an intent to tell EditImageActivity to set the PhotoEditor "pinchTextScalableFlag" to "false"
        final Intent intent = new Intent();
        intent.putExtra(EditImageActivity.PINCH_TEXT_SCALABLE_INTENT_KEY, true);
        mActivityRule.launchActivity(intent);

        // Open the emoji menu (delay to give time to load lower menu)
        Thread.sleep(2000);
        onView(withText(R.string.label_text)).perform(click());
        onView(withId(R.id.add_text_edit_text)).perform(click());

        // Type the text (delay to allow keyboard to load)
        Thread.sleep(2000);
        onView(withId(R.id.add_text_edit_text)).perform(typeText("Test Text"));

        // Select the text (delay to give time for the text imput screen to close)
        Thread.sleep(2000);
        onView(withId(R.id.add_text_done_tv)).perform(click());

        // Select the text box
        Matcher<View> testTextBox = withId(R.id.tvPhotoEditorText);
        onView(testTextBox).perform(click());

        // Capture the current scale of the text box
        ViewGroup textFrameParentView = (ViewGroup) mActivityRule.getActivity().findViewById(R.id.frmBorder).getParent();
        final float textScaleXBeforeScaling = textFrameParentView.getScaleX();
        final float textScaleYBeforeScaling = textFrameParentView.getScaleY();

        // Attempt to scale the text box by pinching
        onView(testTextBox).perform(PinchTestHelper.pinchOut());

        // Validate that the text box did not scale by pinching.
        textFrameParentView = (ViewGroup) mActivityRule.getActivity().findViewById(R.id.frmBorder).getParent();
        assertNotEquals(textScaleXBeforeScaling, textFrameParentView.getScaleX());
        assertNotEquals(textScaleYBeforeScaling, textFrameParentView.getScaleY());
    }

    @Test
    public void checkIfOnlyOneStickerCanBeSelectedAtATime() {

        mActivityRule.launchActivity(null);

        // Add the first emoji to the editor
        onView(ViewMatchers.withId(R.id.rvConstraintTools))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(R.string.label_emoji))));
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
        onView(withIndex(withId(R.id.frmBorder), 0)).check((view, noViewFoundException) -> assertNull((view).getBackground()));

        // Assert that the second emoji is selected (frame background is not null)
        onView(withIndex(withId(R.id.frmBorder), 1)).check((view, noViewFoundException) -> assertNotNull(null, view.getBackground()));
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
