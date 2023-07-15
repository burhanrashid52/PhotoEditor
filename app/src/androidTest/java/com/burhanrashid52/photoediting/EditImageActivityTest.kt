package com.burhanrashid52.photoediting

import android.content.Context
import org.junit.runner.RunWith
import androidx.test.rule.ActivityTestRule
import junit.framework.TestCase.*
import androidx.recyclerview.widget.RecyclerView
import kotlin.Throws
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertNotEquals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
@LargeTest
class EditImageActivityTest {
    @Rule
    @JvmField
    var mActivityRule = ActivityTestRule(
        EditImageActivity::class.java, false, false
    )

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    @Test
    fun checkIfActivityIsLaunched() {
        mActivityRule.launchActivity(null)
        onView(withText(R.string.app_name)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun checkIfBrushIsEnabledWhenClickedOnBrushTool() {
        val editImageActivity = mActivityRule.launchActivity(null)
        assertEquals(editImageActivity.mPhotoEditor.brushDrawableMode, false)
        composeTestRule.onNodeWithText("Shape").performClick()
        assertEquals(editImageActivity.mPhotoEditor.brushDrawableMode, true)
    }

    @Test
    fun checkIfEraserIsEnabledWhenClickedOnEraserTool() {
        mActivityRule.launchActivity(null)
        composeTestRule.onNodeWithText("Eraser").performClick()
        onView(withText(R.string.label_eraser_mode)).check(matches(isDisplayed()))
    }

    @Test
    fun checkIfShapeIsEnabledWhenClickedOnBrushTool() {
        mActivityRule.launchActivity(null)
        composeTestRule.onNodeWithText("Shape").performClick()
        composeTestRule.onNodeWithText("Shape").assertIsDisplayed()
    }

    @Test
    fun checkIfEmojiIsDisplayedWhenEmojiIsSelected() {
        val context: Context = mActivityRule.launchActivity(null)
        val emojis = EmojiBSFragment.getEmojis(context)
        val emojiPosition = 1
        val emojiUnicode = emojis[emojiPosition]
        composeTestRule.onNodeWithText("Emoji").performClick()
        onView(withId(R.id.rvEmoji)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                emojiPosition, click()
            )
        )
        Thread.sleep(500)
        onView(withText(emojiUnicode)).check(matches(isDisplayed()))
    }

    @Ignore("Flacky test. Need to optimize")
    fun checkIfDiscardDialogIsNotDisplayedWhenCacheIsEmpty() {
        val editImageActivity = mActivityRule.launchActivity(null)
        assertTrue(editImageActivity.mPhotoEditor.isCacheEmpty)
        onView(withId(R.id.imgClose)).perform(click())
        assertTrue(editImageActivity.isDestroyed)
    }

    @Test
    fun checkIfDiscardDialogIsDisplayedWhenCacheIsNotEmpty() {
        val editImageActivity = mActivityRule.launchActivity(null)
        assertTrue(editImageActivity.mPhotoEditor.isCacheEmpty)
        composeTestRule.onNodeWithText("Text").performClick()

        onView(withId(R.id.add_text_edit_text)).perform(click())
        onView(withId(R.id.add_text_edit_text))
            .perform(ViewActions.typeText("Test Text"))
        onView(withId(R.id.add_text_done_tv)).perform(click())

        onView(withId(R.id.imgClose)).perform(click())
        onView(withText(R.string.msg_save_image)).check(matches(isDisplayed()))
    }

    @Test
    fun checkIfUndoRedoIsWorkingCorrectWhenClickedOnUndoRedo() {
        val editImageActivity = mActivityRule.launchActivity(null)
        val emojisUnicodes = EmojiBSFragment.getEmojis(editImageActivity)

        composeTestRule.onNodeWithText("Emoji").performClick()
        onView(withId(R.id.rvEmoji)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
        )
        // Thread.sleep(500)
        onView(withText(emojisUnicodes[0])).check(matches(isDisplayed()))

        // Undo the Emoji
        onView(withId(R.id.imgUndo)).perform(click())
        onView(withText(emojisUnicodes[0])).check(doesNotExist())

        // Redo the Emoji
        onView(withId(R.id.imgRedo)).perform(click())
        onView(withText(emojisUnicodes[0])).check(matches(isDisplayed()))
    }

    @Test
    fun testWhenNoImageIsSavedThanToastIsVisibleOnClickedOnShareButton() {
        mActivityRule.launchActivity(null)
        onView(withId(R.id.imgShare)).perform(click())
        onView(withText(R.string.msg_save_image_to_share)).check(matches(isDisplayed()))
    }

    @Ignore("Need to Fix this test")
    @Throws(InterruptedException::class)
    fun testShareIntentWhenImageIsAvailableOnClickedOnShareButton() {
        val editImageActivity = mActivityRule.launchActivity(null)
        editImageActivity.mSaveImageUri = Uri.parse("somethurl")
        Thread.sleep(2000)
        onView(withId(R.id.imgShare)).perform(click())
        //onView(withText(R.string.msg_save_image_to_share)).check(matches(isDisplayed()));
    }

    @Test
    @Throws(InterruptedException::class)
    fun checkIfPinchTextScalableFlagWorks_False() {

        // Use an intent to tell EditImageActivity to set the PhotoEditor "pinchTextScalableFlag" to "false"
        val intent = Intent()
        intent.putExtra(EditImageActivity.PINCH_TEXT_SCALABLE_INTENT_KEY, false)
        mActivityRule.launchActivity(intent)

        // Open the emoji menu (delay to give time to load lower menu)
        Thread.sleep(2000)
        composeTestRule.onNodeWithText("Emoji").performClick()

        // Add an emoji from the menu (delay to give time for the RecyclerView to open)
        Thread.sleep(2000)
        onView(withId(R.id.rvEmoji))
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
                    click()
                )
            )

        // Select the emoji (delay to give time for the RecyclerView to close)
        Thread.sleep(1000)
        onView(withId(ja.burhanrashid52.photoeditor.R.id.frmBorder))
            .perform(click())

        // Capture the scale of the emoji
        var emojiFrameParentView =
            mActivityRule.activity.findViewById<View>(ja.burhanrashid52.photoeditor.R.id.frmBorder).parent as ViewGroup
        val emojiScaleXBeforePinching = emojiFrameParentView.scaleX
        val emojiScaleYBeforePinching = emojiFrameParentView.scaleY

        // Scale the emoji up by pinching
        onView(withId(ja.burhanrashid52.photoeditor.R.id.frmBorder))
            .perform(PinchTestHelper.pinchOut())

        // Check if the emoji scaled up after pinching.
        emojiFrameParentView =
            mActivityRule.activity.findViewById<View>(ja.burhanrashid52.photoeditor.R.id.frmBorder).parent as ViewGroup
        assertNotEquals(emojiScaleXBeforePinching, emojiFrameParentView.scaleX)
        assertNotEquals(emojiScaleYBeforePinching, emojiFrameParentView.scaleY)

        // Remove the emoji from the screen.
        onView(withId(ja.burhanrashid52.photoeditor.R.id.imgPhotoEditorClose))
            .perform(click())

        // Add a text to the image.
        composeTestRule.onNodeWithText("Text").performClick()

        onView(withId(R.id.add_text_edit_text)).perform(click())
        onView(withId(R.id.add_text_edit_text))
            .perform(ViewActions.typeText("Test Text"))

        // Select the text (delay to give time for the text imput screen to close)
        Thread.sleep(2000)
        onView(withId(R.id.add_text_done_tv)).perform(click())

        // Select the text box
        val testTextBox = withId(ja.burhanrashid52.photoeditor.R.id.tvPhotoEditorText)
        onView(testTextBox).perform(click())

        // Capture the current scale of the text box
        var textFrameParentView =
            mActivityRule.activity.findViewById<View>(ja.burhanrashid52.photoeditor.R.id.frmBorder).parent as ViewGroup
        val textScaleXBeforeScaling = textFrameParentView.scaleX
        val textScaleYBeforeScaling = textFrameParentView.scaleY

        // Attempt to scale the text box by pinching
        onView(testTextBox).perform(PinchTestHelper.pinchOut())

        // Validate that the text box did not scale by pinching.
        textFrameParentView =
            mActivityRule.activity.findViewById<View>(ja.burhanrashid52.photoeditor.R.id.frmBorder).parent as ViewGroup
        assertEquals(
            textScaleXBeforeScaling.toDouble(),
            textFrameParentView.scaleX.toDouble(),
            0.01
        )
        assertEquals(
            textScaleYBeforeScaling.toDouble(),
            textFrameParentView.scaleY.toDouble(),
            0.01
        )
    }

    @Test
    @Throws(InterruptedException::class)
    fun checkIfPinchTextScalableFlagWorks_True() {

        // Use an intent to tell EditImageActivity to set the PhotoEditor "pinchTextScalableFlag" to "false"
        val intent = Intent()
        intent.putExtra(EditImageActivity.PINCH_TEXT_SCALABLE_INTENT_KEY, true)
        mActivityRule.launchActivity(intent)

        // Open the emoji menu (delay to give time to load lower menu)
        Thread.sleep(2000)
        composeTestRule.onNodeWithText("Text").performClick()
        onView(withId(R.id.add_text_edit_text)).perform(click())

        // Type the text (delay to allow keyboard to load)
        Thread.sleep(2000)
        onView(withId(R.id.add_text_edit_text))
            .perform(ViewActions.typeText("Test Text"))

        // Select the text (delay to give time for the text imput screen to close)
        Thread.sleep(2000)
        onView(withId(R.id.add_text_done_tv)).perform(click())

        // Select the text box
        val testTextBox = withId(ja.burhanrashid52.photoeditor.R.id.tvPhotoEditorText)
        onView(testTextBox).perform(click())

        // Capture the current scale of the text box
        var textFrameParentView =
            mActivityRule.activity.findViewById<View>(ja.burhanrashid52.photoeditor.R.id.frmBorder).parent as ViewGroup
        val textScaleXBeforeScaling = textFrameParentView.scaleX
        val textScaleYBeforeScaling = textFrameParentView.scaleY

        // Attempt to scale the text box by pinching
        onView(testTextBox).perform(PinchTestHelper.pinchOut())

        // Validate that the text box did not scale by pinching.
        textFrameParentView =
            mActivityRule.activity.findViewById<View>(ja.burhanrashid52.photoeditor.R.id.frmBorder).parent as ViewGroup
        assertNotEquals(textScaleXBeforeScaling, textFrameParentView.scaleX)
        assertNotEquals(textScaleYBeforeScaling, textFrameParentView.scaleY)
    }

    @Test
    fun checkIfOnlyOneStickerCanBeSelectedAtATime() {
        mActivityRule.launchActivity(null)

        // Add the first emoji to the editor
        composeTestRule.onNodeWithText("Emoji").performClick()
        onView(withId(R.id.rvEmoji))
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )

        // Move the first emoji to the left.
        // NOTE(lucianocheng): I tried to do a SwipeAction here, but using the swipe to move
        //                     the element turned out to be very difficult in practice.
        val firstEmojiStickerFrameBorder =
            mActivityRule.activity.findViewById<View>(ja.burhanrashid52.photoeditor.R.id.frmBorder)
        (firstEmojiStickerFrameBorder.parent as FrameLayout).x = 0f

        // Add the second emoji to the editor
        /*Espresso.onView(withIndex(ViewMatchers.withText(R.string.label_emoji), 1)).perform(
            ViewActions.click()
        )*/
        composeTestRule.onNodeWithText("Emoji").performClick()

        onView(withId(R.id.rvEmoji))
            .perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
                    click()
                )
            )

        // Assert that the first emoji is not selected (frame background is null)
        onView(
            withIndex(
                withId(ja.burhanrashid52.photoeditor.R.id.frmBorder),
                0
            )
        )
            .check { view: View, noViewFoundException: NoMatchingViewException? ->
                assertNull(
                    view.background
                )
            }

        // Assert that the second emoji is selected (frame background is not null)
        onView(
            withIndex(
                withId(ja.burhanrashid52.photoeditor.R.id.frmBorder),
                1
            )
        )
            .check { view: View, noViewFoundException: NoMatchingViewException? ->
                assertNotNull(
                    null,
                    view.background
                )
            }
    }

    companion object {
        // Helper class for matching with multiple elements with the same ID
        // NOTE(lucianocheng): All emoji / stickers have the same views, so we need this to retrieve
        //                     them in espresso and avoid AmbiguousViewMatcherException
        //                     Taken from https://stackoverflow.com/a/41967652/1015951
        fun withIndex(matcher: Matcher<View?>, index: Int): TypeSafeMatcher<View?> {
            return object : TypeSafeMatcher<View?>() {
                var currentIndex = 0
                override fun describeTo(description: Description) {
                    description.appendText("with index: ")
                    description.appendValue(index)
                    matcher.describeTo(description)
                }

                public override fun matchesSafely(view: View?): Boolean {
                    return matcher.matches(view) && currentIndex++ == index
                }
            }
        }
    }
}