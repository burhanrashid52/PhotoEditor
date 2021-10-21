package ja.burhanrashid52.photoeditor

import android.graphics.Paint
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import ja.burhanrashid52.photoeditor.TextShadow
import ja.burhanrashid52.photoeditor.TextBorder
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import ja.burhanrashid52.photoeditor.TextStyleBuilderTest.MockTextStyleBuilder
import android.widget.TextView
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class TextStyleBuilderTest {
    @Test
    fun testFillBuilderWithAllPossibleStyles() {
        val builder = TextStyleBuilder()
        val textShadow = TextShadow(0, 0, 0, 123)
        val textBorder = TextBorder(0, 123, 0, 123)
        builder.withTextColor(123)
        builder.withTextSize(12f)
        builder.withGravity(3)
        builder.withTextFont(Typeface.DEFAULT)
        builder.withBackgroundColor(321)
        builder.withTextAppearance(144)
        builder.withTextStyle(Typeface.NORMAL)
        builder.withTextShadow(textShadow)
        builder.withTextBorder(textBorder)
        builder.withTextFlag(Paint.UNDERLINE_TEXT_FLAG)
        Assert.assertEquals(10, builder.values.size.toLong())
        Assert.assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.COLOR))
        Assert.assertEquals(123, builder.values[TextStyleBuilder.TextStyle.COLOR])
        Assert.assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.SIZE))
        Assert.assertEquals(12f, builder.values[TextStyleBuilder.TextStyle.SIZE])
        Assert.assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.GRAVITY))
        Assert.assertEquals(3, builder.values[TextStyleBuilder.TextStyle.GRAVITY])
        Assert.assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.FONT_FAMILY))
        Assert.assertEquals(
            Typeface.DEFAULT,
            builder.values[TextStyleBuilder.TextStyle.FONT_FAMILY]
        )
        Assert.assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.BACKGROUND))
        Assert.assertEquals(321, builder.values[TextStyleBuilder.TextStyle.BACKGROUND])
        Assert.assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.TEXT_APPEARANCE))
        Assert.assertEquals(144, builder.values[TextStyleBuilder.TextStyle.TEXT_APPEARANCE])
        Assert.assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.TEXT_STYLE))
        Assert.assertEquals(Typeface.NORMAL, builder.values[TextStyleBuilder.TextStyle.TEXT_STYLE])
        Assert.assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.SHADOW))
        Assert.assertEquals(textShadow, builder.values[TextStyleBuilder.TextStyle.SHADOW])
        Assert.assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.BORDER))
        Assert.assertEquals(textBorder, builder.values[TextStyleBuilder.TextStyle.BORDER])
        Assert.assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.TEXT_FLAG))
        Assert.assertEquals(
            Paint.UNDERLINE_TEXT_FLAG,
            builder.values[TextStyleBuilder.TextStyle.TEXT_FLAG]
        )
    }

    @Test
    fun testFillBackgroundDrawableOverridingBackgroundColor() {
        val builder = TextStyleBuilder()
        val dummyDrawable: Drawable = BitmapDrawable()
        builder.withBackgroundColor(123)
        builder.withBackgroundDrawable(dummyDrawable)
        Assert.assertEquals(1, builder.values.size.toLong())
        Assert.assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.BACKGROUND))
        Assert.assertEquals(dummyDrawable, builder.values[TextStyleBuilder.TextStyle.BACKGROUND])
    }

    @Test
    fun testFillSomeRandomItemsAndLetOthersUnset() {
        val builder = TextStyleBuilder()
        builder.withTextColor(123)
        builder.withGravity(3)
        Assert.assertEquals(2, builder.values.size.toLong())
        Assert.assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.COLOR))
        Assert.assertEquals(123, builder.values[TextStyleBuilder.TextStyle.COLOR])
        Assert.assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.GRAVITY))
        Assert.assertEquals(3, builder.values[TextStyleBuilder.TextStyle.GRAVITY])
        Assert.assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.SIZE))
        Assert.assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.FONT_FAMILY))
        Assert.assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.BACKGROUND))
        Assert.assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.TEXT_APPEARANCE))
        Assert.assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.TEXT_STYLE))
        Assert.assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.TEXT_FLAG))
        Assert.assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.SHADOW))
        Assert.assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.BORDER))
    }

    @Test
    fun testApplyStyleShouldCallTheCorrectApplyMethod() {
        val builder = MockTextStyleBuilder()
        builder.withTextColor(123)
        builder.withTextSize(12f)
        builder.withGravity(3)
        builder.withTextFont(Typeface.DEFAULT)
        builder.withBackgroundColor(321)
        builder.withTextAppearance(144)
        builder.withTextStyle(Typeface.NORMAL)
        builder.withTextFlag(Paint.ANTI_ALIAS_FLAG)
        builder.withTextShadow(TextShadow(0, 0, 0, 123))
        builder.withTextBorder(TextBorder(0, 123, 0, 123))
        val textView = Mockito.mock(TextView::class.java)
        builder.applyStyle(textView)
        Assert.assertTrue(builder.textColorApplied)
        Assert.assertTrue(builder.textSizeApplied)
        Assert.assertTrue(builder.gravityApplied)
        Assert.assertTrue(builder.textFontApplied)
        Assert.assertTrue(builder.backgroundColorApplied)
        Assert.assertTrue(builder.textAppearanceApplied)
        Assert.assertFalse(builder.backgroundDrawableApplied)
        Assert.assertTrue(builder.textStyle)
        Assert.assertTrue(builder.textFlag)
        Assert.assertTrue(builder.textShadow1)
        Assert.assertTrue(builder.textBorder1)
    }

    @Test
    fun testApplyBackgroundDrawableStyleShouldCallBackgroundDrawableApplyMethod() {
        val builder = MockTextStyleBuilder()
        val textView = Mockito.mock(TextView::class.java)
        builder.withBackgroundDrawable(BitmapDrawable())
        builder.applyStyle(textView)
        Assert.assertFalse(builder.textColorApplied)
        Assert.assertFalse(builder.textSizeApplied)
        Assert.assertFalse(builder.gravityApplied)
        Assert.assertFalse(builder.textFontApplied)
        Assert.assertFalse(builder.backgroundColorApplied)
        Assert.assertFalse(builder.textAppearanceApplied)
        Assert.assertTrue(builder.backgroundDrawableApplied)
        Assert.assertFalse(builder.textStyle)
        Assert.assertFalse(builder.textFlag)
        Assert.assertFalse(builder.textShadow1)
        Assert.assertFalse(builder.textBorder1)
    }

    @Test
    fun testApplyStyleShouldCallNoOneApplyMethod() {
        val builder = MockTextStyleBuilder()
        val textView = Mockito.mock(TextView::class.java)
        builder.applyStyle(textView)
        Assert.assertFalse(builder.textColorApplied)
        Assert.assertFalse(builder.textSizeApplied)
        Assert.assertFalse(builder.gravityApplied)
        Assert.assertFalse(builder.textFontApplied)
        Assert.assertFalse(builder.backgroundColorApplied)
        Assert.assertFalse(builder.textAppearanceApplied)
        Assert.assertFalse(builder.backgroundDrawableApplied)
        Assert.assertFalse(builder.textStyle)
        Assert.assertFalse(builder.textFlag)
        Assert.assertFalse(builder.textShadow1)
        Assert.assertFalse(builder.textBorder1)
    }

    private inner class MockTextStyleBuilder : TextStyleBuilder() {
        var textSizeApplied = false
        var textColorApplied = false
        var gravityApplied = false
        var textFontApplied = false
        var backgroundColorApplied = false
        var backgroundDrawableApplied = false
        var textAppearanceApplied = false
        var textStyle = false
        var textFlag = false
        var textShadow1 = false
        var textBorder1 = false
        override fun applyTextStyle(textView: TextView, typeface: Int) {
            textStyle = true
        }

        override fun applyTextFlag(textView: TextView, flag: Int) {
            textFlag = true
        }

        override fun applyTextShadow(textView: TextView, textShadow: TextShadow) {
            textShadow1 = true
        }

        override fun applyTextBorder(textView: TextView, textBorder: TextBorder) {
            textBorder1 = true
        }

        override fun applyTextSize(textView: TextView, size: Float) {
            textSizeApplied = true
        }

        override fun applyTextColor(textView: TextView, color: Int) {
            textColorApplied = true
        }

        override fun applyGravity(textView: TextView, gravity: Int) {
            gravityApplied = true
        }

        override fun applyFontFamily(textView: TextView, typeface: Typeface) {
            textFontApplied = true
        }

        override fun applyTextAppearance(textView: TextView, styleAppearance: Int) {
            textAppearanceApplied = true
        }

        override fun applyBackgroundColor(textView: TextView, color: Int) {
            backgroundColorApplied = true
        }

        override fun applyBackgroundDrawable(textView: TextView, bg: Drawable) {
            backgroundDrawableApplied = true
        }
    }
}