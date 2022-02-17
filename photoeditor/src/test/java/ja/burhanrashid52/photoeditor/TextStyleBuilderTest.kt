package ja.burhanrashid52.photoeditor

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.TextView
import junit.framework.TestCase.*
import org.junit.Assert
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

class TextStyleBuilderTest {
    @Test
    fun testFillBuilderWithAllPossibleStyles() {
        val builder = TextStyleBuilder()
        val textShadow = TextShadow(0f, 0f, 0f, 123)
        val textBorder = TextBorder(0f, 123, 0, 123)
        builder.withTextColor(123)
        builder.withTextSize(12f)
        builder.withGravity(3)
        val default = Mockito.mock(Typeface::class.java)
        builder.withTextFont(default)
        builder.withBackgroundColor(321)
        builder.withTextAppearance(144)
        builder.withTextStyle(Typeface.NORMAL)
        builder.withTextShadow(textShadow)
        builder.withTextBorder(textBorder)
        builder.withTextFlag(Paint.UNDERLINE_TEXT_FLAG)
        assertEquals(10, builder.values.size.toLong())
        assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.COLOR))
        assertEquals(123, builder.values[TextStyleBuilder.TextStyle.COLOR])
        assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.SIZE))
        assertEquals(12f, builder.values[TextStyleBuilder.TextStyle.SIZE])
        assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.GRAVITY))
        assertEquals(3, builder.values[TextStyleBuilder.TextStyle.GRAVITY])
        assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.FONT_FAMILY))
        assertEquals(
            default,
            builder.values[TextStyleBuilder.TextStyle.FONT_FAMILY]
        )
        assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.BACKGROUND))
        assertEquals(321, builder.values[TextStyleBuilder.TextStyle.BACKGROUND])
        assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.TEXT_APPEARANCE))
        assertEquals(144, builder.values[TextStyleBuilder.TextStyle.TEXT_APPEARANCE])
        assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.TEXT_STYLE))
        assertEquals(Typeface.NORMAL, builder.values[TextStyleBuilder.TextStyle.TEXT_STYLE])
        assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.SHADOW))
        assertEquals(textShadow, builder.values[TextStyleBuilder.TextStyle.SHADOW])
        assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.BORDER))
        assertEquals(textBorder, builder.values[TextStyleBuilder.TextStyle.BORDER])
        assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.TEXT_FLAG))
        assertEquals(
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
        assertEquals(1, builder.values.size.toLong())
        assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.BACKGROUND))
        assertEquals(dummyDrawable, builder.values[TextStyleBuilder.TextStyle.BACKGROUND])
    }

    @Test
    fun testFillSomeRandomItemsAndLetOthersUnset() {
        val builder = TextStyleBuilder()
        builder.withTextColor(123)
        builder.withGravity(3)
        assertEquals(2, builder.values.size.toLong())
        assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.COLOR))
        assertEquals(123, builder.values[TextStyleBuilder.TextStyle.COLOR])
        assertTrue(builder.values.containsKey(TextStyleBuilder.TextStyle.GRAVITY))
        assertEquals(3, builder.values[TextStyleBuilder.TextStyle.GRAVITY])
        assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.SIZE))
        assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.FONT_FAMILY))
        assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.BACKGROUND))
        assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.TEXT_APPEARANCE))
        assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.TEXT_STYLE))
        assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.TEXT_FLAG))
        assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.SHADOW))
        assertFalse(builder.values.containsKey(TextStyleBuilder.TextStyle.BORDER))
    }

    @Test
    fun testApplyStyleShouldCallTheCorrectApplyMethod() {

        val builder = MockTextStyleBuilder()
        builder.withTextColor(123)
        builder.withTextSize(12f)
        builder.withGravity(3)
        builder.withTextFont(Mockito.mock(Typeface::class.java))
        builder.withBackgroundColor(321)
        builder.withTextAppearance(144)
        builder.withTextStyle(Typeface.NORMAL)
        builder.withTextFlag(Paint.ANTI_ALIAS_FLAG)
        builder.withTextShadow(TextShadow(0f, 0f, 0f, 123))
        builder.withTextBorder(TextBorder(0f, 123, 0, 123))
        val textView = Mockito.mock(TextView::class.java)
        builder.applyStyle(textView)
        assertTrue(builder.textColorApplied)
        assertTrue(builder.textSizeApplied)
        assertTrue(builder.gravityApplied)
        assertTrue(builder.textFontApplied)
        assertTrue(builder.backgroundColorApplied)
        assertTrue(builder.textAppearanceApplied)
        assertFalse(builder.backgroundDrawableApplied)
        assertTrue(builder.textStyle)
        assertTrue(builder.textFlag)
        assertTrue(builder.textShadow1)
        assertTrue(builder.textBorder1)
    }

    @Test
    fun testApplyBackgroundDrawableStyleShouldCallBackgroundDrawableApplyMethod() {
        val builder = MockTextStyleBuilder()
        val textView = Mockito.mock(TextView::class.java)
        builder.withBackgroundDrawable(BitmapDrawable())
        builder.applyStyle(textView)
        assertFalse(builder.textColorApplied)
        assertFalse(builder.textSizeApplied)
        assertFalse(builder.gravityApplied)
        assertFalse(builder.textFontApplied)
        assertFalse(builder.backgroundColorApplied)
        assertFalse(builder.textAppearanceApplied)
        assertTrue(builder.backgroundDrawableApplied)
        assertFalse(builder.textStyle)
        assertFalse(builder.textFlag)
        assertFalse(builder.textShadow1)
        assertFalse(builder.textBorder1)
    }

    @Test
    fun testApplyStyleShouldCallNoOneApplyMethod() {
        val builder = MockTextStyleBuilder()
        val textView = Mockito.mock(TextView::class.java)
        builder.applyStyle(textView)
        assertFalse(builder.textColorApplied)
        assertFalse(builder.textSizeApplied)
        assertFalse(builder.gravityApplied)
        assertFalse(builder.textFontApplied)
        assertFalse(builder.backgroundColorApplied)
        assertFalse(builder.textAppearanceApplied)
        assertFalse(builder.backgroundDrawableApplied)
        assertFalse(builder.textStyle)
        assertFalse(builder.textFlag)
        assertFalse(builder.textShadow1)
        assertFalse(builder.textBorder1)
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

        override fun applyFontFamily(textView: TextView, typeface: Typeface?) {
            textFontApplied = true
        }

        override fun applyTextAppearance(textView: TextView, styleAppearance: Int) {
            textAppearanceApplied = true
        }

        override fun applyBackgroundColor(textView: TextView, color: Int) {
            backgroundColorApplied = true
        }

        override fun applyBackgroundDrawable(textView: TextView, bg: Drawable?) {
            backgroundDrawableApplied = true
        }
    }
}