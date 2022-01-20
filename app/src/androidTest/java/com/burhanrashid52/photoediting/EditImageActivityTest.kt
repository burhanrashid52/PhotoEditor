package com.burhanrashid52.photoediting

import android.content.Context
import org.junit.runner.RunWith
import androidx.test.rule.ActivityTestRule
import junit.framework.TestCase
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.recyclerview.widget.RecyclerView
import kotlin.Throws
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import junit.framework.TestCase.*
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
    @Rule @JvmField
    var mActivityRule = ActivityTestRule(
        EditImageActivity::class.java, false, false
    )

    @Test
    fun checkIfActivityIsLaunched() {
        mActivityRule.launchActivity(null)
        Espresso.onView(ViewMatchers.withText(R.string.app_name)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Test
    fun checkIfBrushIsEnabledWhenClickedOnBrushTool() {
        val editImageActivity = mActivityRule.launchActivity(null)
        assertEquals(editImageActivity.mPhotoEditor?.brushDrawableMode, false)
        Espresso.onView(ViewMatchers.withText(R.string.label_shape)).perform(ViewActions.click())
        assertEquals(editImageActivity.mPhotoEditor?.brushDrawableMode ,true)
    }

    @Test
    fun checkIfEraserIsEnabledWhenClickedOnEraserTool() {
        mActivityRule.launchActivity(null)
        Espresso.onView(ViewMatchers.withText(R.string.label_eraser)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.label_eraser_mode)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Test
    fun checkIfShapeIsEnabledWhenClickedOnBrushTool() {
        val editImageActivity = mActivityRule.launchActivity(null)
        Espresso.onView(ViewMatchers.withText(R.string.label_shape)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.label_shape)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Test
    fun checkIfEmojiIsDisplayedWhenEmojiIsSelected() {
        val context: Context = mActivityRule.launchActivity(null)
        val emojis = EmojiBSFragment.getEmojis(context)
        val emojiPosition = 1
        val emojiUnicode = emojis[emojiPosition]
        Espresso.onView(ViewMatchers.withId(R.id.rvConstraintTools))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(R.string.label_emoji)
                    )
                )
            )
        Espresso.onView(ViewMatchers.withText(R.string.label_emoji)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.rvEmoji))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    emojiPosition,
                    ViewActions.click()
                )
            )
        Espresso.onView(ViewMatchers.withText(emojiUnicode)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Ignore("Flacky test. Need to optimize")
    fun checkIfDiscardDialogIsNotDisplayedWhenCacheIsEmpty() {
        val editImageActivity = mActivityRule.launchActivity(null)
        TestCase.assertTrue(editImageActivity.mPhotoEditor!!.isCacheEmpty)
        Espresso.onView(ViewMatchers.withId(R.id.imgClose)).perform(ViewActions.click())
        TestCase.assertTrue(editImageActivity.isDestroyed)
    }

    @Test
    fun checkIfDiscardDialogIsDisplayedWhenCacheIsNotEmpty() {
        val editImageActivity = mActivityRule.launchActivity(null)
        TestCase.assertTrue(editImageActivity.mPhotoEditor!!.isCacheEmpty)
        Espresso.onView(ViewMatchers.withId(R.id.rvConstraintTools))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(R.string.label_emoji)
                    )
                )
            )
        Espresso.onView(ViewMatchers.withText(R.string.label_emoji)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.rvEmoji))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    ViewActions.click()
                )
            )
        Espresso.onView(ViewMatchers.withId(R.id.imgClose)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.msg_save_image)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
    }

    @Test
    fun checkIfUndoRedoIsWorkingCorrectWhenClickedOnUndoRedo() {
        val editImageActivity = mActivityRule.launchActivity(null)
        val emojisUnicodes = EmojiBSFragment.getEmojis(editImageActivity)
        Espresso.onView(ViewMatchers.withId(R.id.rvConstraintTools))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(R.string.label_emoji)
                    )
                )
            )
        //Add Emoji
        Espresso.onView(ViewMatchers.withText(R.string.label_emoji)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.rvEmoji)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                ViewActions.click()
            )
        )
        Espresso.onView(
            ViewMatchers.withText(
                emojisUnicodes[0]
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // Undo the Emoji
        Espresso.onView(ViewMatchers.withId(R.id.imgUndo)).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withText(
                emojisUnicodes[0]
            )
        ).check(ViewAssertions.doesNotExist())

        // Redo the Emoji
        Espresso.onView(ViewMatchers.withId(R.id.imgRedo)).perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withText(
                emojisUnicodes[0]
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testWhenNoImageIsSavedThanToastIsVisibleOnClickedOnShareButton() {
        mActivityRule.launchActivity(null)
        Espresso.onView(ViewMatchers.withId(R.id.imgShare)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.msg_save_image_to_share)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
    }

    @Ignore("Need to Fix this test")
    @Throws(InterruptedException::class)
    fun testShareIntentWhenImageIsAvailableOnClickedOnShareButton() {
        val editImageActivity = mActivityRule.launchActivity(null)
        editImageActivity.mSaveImageUri = Uri.parse("somethurl")
        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withId(R.id.imgShare)).perform(ViewActions.click())
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
        Espresso.onView(ViewMatchers.withId(R.id.rvConstraintTools))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(R.string.label_emoji)
                    )
                )
            )
        Espresso.onView(ViewMatchers.withText(R.string.label_emoji)).perform(ViewActions.click())

        // Add an emoji from the menu (delay to give time for the RecyclerView to open)
        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withId(R.id.rvEmoji))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
                    ViewActions.click()
                )
            )

        // Select the emoji (delay to give time for the RecyclerView to close)
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.frmBorder)).perform(ViewActions.click())

        // Capture the scale of the emoji
        var emojiFrameParentView =
            mActivityRule.activity.findViewById<View>(R.id.frmBorder).parent as ViewGroup
        val emojiScaleXBeforePinching = emojiFrameParentView.scaleX
        val emojiScaleYBeforePinching = emojiFrameParentView.scaleY

        // Scale the emoji up by pinching
        Espresso.onView(ViewMatchers.withId(R.id.frmBorder)).perform(PinchTestHelper.pinchOut())

        // Check if the emoji scaled up after pinching.
        emojiFrameParentView =
            mActivityRule.activity.findViewById<View>(R.id.frmBorder).parent as ViewGroup
        assertNotEquals(emojiScaleXBeforePinching, emojiFrameParentView.scaleX)
        assertNotEquals(emojiScaleYBeforePinching, emojiFrameParentView.scaleY)

        // Remove the emoji from the screen.
        Espresso.onView(ViewMatchers.withId(R.id.imgPhotoEditorClose)).perform(ViewActions.click())

        // Add a text to the image.
        Espresso.onView(ViewMatchers.withText(R.string.label_text)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_text_edit_text)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_text_edit_text))
            .perform(ViewActions.typeText("Test Text"))

        // Select the text (delay to give time for the text imput screen to close)
        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withId(R.id.add_text_done_tv)).perform(ViewActions.click())

        // Select the text box
        val testTextBox = ViewMatchers.withId(R.id.tvPhotoEditorText)
        Espresso.onView(testTextBox).perform(ViewActions.click())

        // Capture the current scale of the text box
        var textFrameParentView =
            mActivityRule.activity.findViewById<View>(R.id.frmBorder).parent as ViewGroup
        val textScaleXBeforeScaling = textFrameParentView.scaleX
        val textScaleYBeforeScaling = textFrameParentView.scaleY

        // Attempt to scale the text box by pinching
        Espresso.onView(testTextBox).perform(PinchTestHelper.pinchOut())

        // Validate that the text box did not scale by pinching.
        textFrameParentView =
            mActivityRule.activity.findViewById<View>(R.id.frmBorder).parent as ViewGroup
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
        Espresso.onView(ViewMatchers.withText(R.string.label_text)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_text_edit_text)).perform(ViewActions.click())

        // Type the text (delay to allow keyboard to load)
        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withId(R.id.add_text_edit_text))
            .perform(ViewActions.typeText("Test Text"))

        // Select the text (delay to give time for the text imput screen to close)
        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withId(R.id.add_text_done_tv)).perform(ViewActions.click())

        // Select the text box
        val testTextBox = ViewMatchers.withId(R.id.tvPhotoEditorText)
        Espresso.onView(testTextBox).perform(ViewActions.click())

        // Capture the current scale of the text box
        var textFrameParentView =
            mActivityRule.activity.findViewById<View>(R.id.frmBorder).parent as ViewGroup
        val textScaleXBeforeScaling = textFrameParentView.scaleX
        val textScaleYBeforeScaling = textFrameParentView.scaleY

        // Attempt to scale the text box by pinching
        Espresso.onView(testTextBox).perform(PinchTestHelper.pinchOut())

        // Validate that the text box did not scale by pinching.
        textFrameParentView =
            mActivityRule.activity.findViewById<View>(R.id.frmBorder).parent as ViewGroup
        assertNotEquals(textScaleXBeforeScaling, textFrameParentView.scaleX)
        assertNotEquals(textScaleYBeforeScaling, textFrameParentView.scaleY)
    }

    @Test
    fun checkIfOnlyOneStickerCanBeSelectedAtATime() {
        mActivityRule.launchActivity(null)

        // Add the first emoji to the editor
        Espresso.onView(ViewMatchers.withId(R.id.rvConstraintTools))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(R.string.label_emoji)
                    )
                )
            )
        Espresso.onView(ViewMatchers.withText(R.string.label_emoji)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.rvEmoji))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    ViewActions.click()
                )
            )

        // Move the first emoji to the left.
        // NOTE(lucianocheng): I tried to do a SwipeAction here, but using the swipe to move
        //                     the element turned out to be very difficult in practice.
        val firstEmojiStickerFrameBorder = mActivityRule.activity.findViewById<View>(R.id.frmBorder)
        (firstEmojiStickerFrameBorder.parent as FrameLayout).x = 0f

        // Add the second emoji to the editor
        Espresso.onView(withIndex(ViewMatchers.withText(R.string.label_emoji), 1)).perform(
            ViewActions.click()
        )
        Espresso.onView(ViewMatchers.withId(R.id.rvEmoji))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    1,
                    ViewActions.click()
                )
            )

        // Assert that the first emoji is not selected (frame background is null)
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.frmBorder), 0))
            .check { view: View, noViewFoundException: NoMatchingViewException? ->
                assertNull(
                    view.background
                )
            }

        // Assert that the second emoji is selected (frame background is not null)
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.frmBorder), 1))
            .check { view: View, noViewFoundException: NoMatchingViewException? ->
                TestCase.assertNotNull(
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