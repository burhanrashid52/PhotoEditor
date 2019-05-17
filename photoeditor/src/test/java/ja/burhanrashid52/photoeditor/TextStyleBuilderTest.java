package ja.burhanrashid52.photoeditor;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class TextStyleBuilderTest {

    @Test
    public void testFillBuilderWithAllPossibleStyles() {
        final TextStyleBuilder builder = new TextStyleBuilder();
        builder.withTextColor(123);
        builder.withTextSize(12f);
        builder.withGravity(3);
        builder.withTextFont(Typeface.DEFAULT);
        builder.withBackgroundColor(321);
        builder.withTextAppearance(144);

        Assert.assertEquals(6, builder.getValues().size());

        Assert.assertTrue(builder.getValues().containsKey(TextStyleBuilder.TextStyle.COLOR));
        Assert.assertEquals(123, builder.getValues().get(TextStyleBuilder.TextStyle.COLOR));

        Assert.assertTrue(builder.getValues().containsKey(TextStyleBuilder.TextStyle.SIZE));
        Assert.assertEquals(12f, builder.getValues().get(TextStyleBuilder.TextStyle.SIZE));

        Assert.assertTrue(builder.getValues().containsKey(TextStyleBuilder.TextStyle.GRAVITY));
        Assert.assertEquals(3, builder.getValues().get(TextStyleBuilder.TextStyle.GRAVITY));

        Assert.assertTrue(builder.getValues().containsKey(TextStyleBuilder.TextStyle.FONT_FAMILY));
        Assert.assertEquals(Typeface.DEFAULT, builder.getValues().get(TextStyleBuilder.TextStyle.FONT_FAMILY));

        Assert.assertTrue(builder.getValues().containsKey(TextStyleBuilder.TextStyle.BACKGROUND));
        Assert.assertEquals(321 , builder.getValues().get(TextStyleBuilder.TextStyle.BACKGROUND));

        Assert.assertTrue(builder.getValues().containsKey(TextStyleBuilder.TextStyle.TEXT_APPEARANCE));
        Assert.assertEquals(144 , builder.getValues().get(TextStyleBuilder.TextStyle.TEXT_APPEARANCE));
    }

    @Test
    public void testFillBackgroundDrawableOverridingBackgroundColor() {
        final TextStyleBuilder builder = new TextStyleBuilder();
        final Drawable dummyDrawable = new BitmapDrawable();
        builder.withBackgroundColor(123);
        builder.withBackgroundDrawable(dummyDrawable);

        Assert.assertEquals(1, builder.getValues().size());

        Assert.assertTrue(builder.getValues().containsKey(TextStyleBuilder.TextStyle.BACKGROUND));
        Assert.assertEquals(dummyDrawable , builder.getValues().get(TextStyleBuilder.TextStyle.BACKGROUND));
    }

    @Test
    public void testFillSomeRandomItemsAndLetOthersUnset() {
        final TextStyleBuilder builder = new TextStyleBuilder();
        builder.withTextColor(123);
        builder.withGravity(3);

        Assert.assertEquals(2, builder.getValues().size());

        Assert.assertTrue(builder.getValues().containsKey(TextStyleBuilder.TextStyle.COLOR));
        Assert.assertEquals(123, builder.getValues().get(TextStyleBuilder.TextStyle.COLOR));

        Assert.assertTrue(builder.getValues().containsKey(TextStyleBuilder.TextStyle.GRAVITY));
        Assert.assertEquals(3, builder.getValues().get(TextStyleBuilder.TextStyle.GRAVITY));

        Assert.assertFalse(builder.getValues().containsKey(TextStyleBuilder.TextStyle.SIZE));

        Assert.assertFalse(builder.getValues().containsKey(TextStyleBuilder.TextStyle.FONT_FAMILY));

        Assert.assertFalse(builder.getValues().containsKey(TextStyleBuilder.TextStyle.BACKGROUND));

        Assert.assertFalse(builder.getValues().containsKey(TextStyleBuilder.TextStyle.TEXT_APPEARANCE));
    }

    @Test
    public void testApplyStyleShouldCallTheCorrectApplyMethod() {
        final MockTextStyleBuilder builder = new MockTextStyleBuilder();
        builder.withTextColor(123);
        builder.withTextSize(12f);
        builder.withGravity(3);
        builder.withTextFont(Typeface.DEFAULT);
        builder.withBackgroundColor(321);
        builder.withTextAppearance(144);

        final TextView textView = Mockito.mock(TextView.class);
        builder.applyStyle(textView);

        Assert.assertTrue(builder.textColorApplied);
        Assert.assertTrue(builder.textSizeApplied);
        Assert.assertTrue(builder.gravityApplied);
        Assert.assertTrue(builder.textFontApplied);
        Assert.assertTrue(builder.backgroundColorApplied);
        Assert.assertTrue(builder.textAppearanceApplied);
        Assert.assertFalse(builder.backgroundDrawableApplied);
    }

    @Test
    public void testApplyBackgroundDrawableStyleShouldCallBackgroundDrawableApplyMethod() {
        final MockTextStyleBuilder builder = new MockTextStyleBuilder();

        final TextView textView = Mockito.mock(TextView.class);
        builder.withBackgroundDrawable(new BitmapDrawable());
        builder.applyStyle(textView);

        Assert.assertFalse(builder.textColorApplied);
        Assert.assertFalse(builder.textSizeApplied);
        Assert.assertFalse(builder.gravityApplied);
        Assert.assertFalse(builder.textFontApplied);
        Assert.assertFalse(builder.backgroundColorApplied);
        Assert.assertFalse(builder.textAppearanceApplied);
        Assert.assertTrue(builder.backgroundDrawableApplied);
    }

    @Test
    public void testApplyStyleShouldCallNoOneApplyMethod() {
        final MockTextStyleBuilder builder = new MockTextStyleBuilder();

        final TextView textView = Mockito.mock(TextView.class);
        builder.applyStyle(textView);

        Assert.assertFalse(builder.textColorApplied);
        Assert.assertFalse(builder.textSizeApplied);
        Assert.assertFalse(builder.gravityApplied);
        Assert.assertFalse(builder.textFontApplied);
        Assert.assertFalse(builder.backgroundColorApplied);
        Assert.assertFalse(builder.textAppearanceApplied);
        Assert.assertFalse(builder.backgroundDrawableApplied);
    }

    private class MockTextStyleBuilder extends TextStyleBuilder {
        boolean textSizeApplied = false;
        boolean textColorApplied = false;
        boolean gravityApplied = false;
        boolean textFontApplied = false;
        boolean backgroundColorApplied = false;
        boolean backgroundDrawableApplied = false;
        boolean textAppearanceApplied = false;

        @Override
        protected void applyTextSize(TextView textView, float size) {
            textSizeApplied = true;
        }

        @Override
        protected void applyTextColor(TextView textView, int color) {
            textColorApplied = true;
        }

        @Override
        protected void applyGravity(TextView textView, int gravity) {
            gravityApplied = true;
        }

        @Override
        protected void applyFontFamily(TextView textView, Typeface typeface) {
            textFontApplied = true;
        }

        @Override
        protected void applyTextAppearance(TextView textView, int styleAppearance) {
            textAppearanceApplied = true;
        }

        @Override
        protected void applyBackgroundColor(TextView textView, int color) {
            backgroundColorApplied = true;
        }

        @Override
        protected void applyBackgroundDrawable(TextView textView, Drawable bg) {
            backgroundDrawableApplied = true;
        }
    }
}