package ja.burhanrashid52.photoeditor;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

public class TextStyleBuilderTest {

    @Test
    public void testFillBuilderWithAllPossibleStyles() {
        final TextStyleBuilder builder = new TextStyleBuilder();
        TextShadow textShadow=new TextShadow(0,0,0,123);

        TextBorder textBorder=new TextBorder(0,123,0,123);
        builder.withTextColor(123);
        builder.withTextSize(12f);
        builder.withGravity(3);
        builder.withTextFont(Typeface.DEFAULT);
        builder.withBackgroundColor(321);
        builder.withTextAppearance(144);
        builder.withTextStyle(Typeface.NORMAL);
        builder.withTextShadow(textShadow);
        builder.withTextBorder(textBorder);
        builder.withTextFlag(Paint.UNDERLINE_TEXT_FLAG);

        Assert.assertEquals(10, builder.getValues().size());

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

        Assert.assertTrue(builder.getValues().containsKey(TextStyleBuilder.TextStyle.TEXT_STYLE));
        Assert.assertEquals(Typeface.NORMAL , builder.getValues().get(TextStyleBuilder.TextStyle.TEXT_STYLE));

        Assert.assertTrue(builder.getValues().containsKey(TextStyleBuilder.TextStyle.SHADOW));
        Assert.assertEquals(textShadow, builder.getValues().get(TextStyleBuilder.TextStyle.SHADOW));

        Assert.assertTrue(builder.getValues().containsKey(TextStyleBuilder.TextStyle.BORDER));
        Assert.assertEquals( textBorder, builder.getValues().get(TextStyleBuilder.TextStyle.BORDER));

        Assert.assertTrue(builder.getValues().containsKey(TextStyleBuilder.TextStyle.TEXT_FLAG));
        Assert.assertEquals(Paint.UNDERLINE_TEXT_FLAG , builder.getValues().get(TextStyleBuilder.TextStyle.TEXT_FLAG));


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

        Assert.assertFalse(builder.getValues().containsKey(TextStyleBuilder.TextStyle.TEXT_STYLE));
        Assert.assertFalse(builder.getValues().containsKey(TextStyleBuilder.TextStyle.TEXT_FLAG));
        Assert.assertFalse(builder.getValues().containsKey(TextStyleBuilder.TextStyle.SHADOW));
        Assert.assertFalse(builder.getValues().containsKey(TextStyleBuilder.TextStyle.BORDER));
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
        builder.withTextStyle(Typeface.NORMAL);
        builder.withTextFlag(Paint.ANTI_ALIAS_FLAG);
        builder.withTextShadow(new TextShadow(0,0,0,123));
        builder.withTextBorder(new TextBorder(0,123,0,123) );


        final TextView textView = Mockito.mock(TextView.class);
        builder.applyStyle(textView);

        Assert.assertTrue(builder.textColorApplied);
        Assert.assertTrue(builder.textSizeApplied);
        Assert.assertTrue(builder.gravityApplied);
        Assert.assertTrue(builder.textFontApplied);
        Assert.assertTrue(builder.backgroundColorApplied);
        Assert.assertTrue(builder.textAppearanceApplied);
        Assert.assertFalse(builder.backgroundDrawableApplied);
        Assert.assertTrue(builder.textStyle);
        Assert.assertTrue(builder.textFlag);
        Assert.assertTrue(builder.textShadow1);
        Assert.assertTrue(builder.textBorder1);
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
        Assert.assertFalse(builder.textStyle);
        Assert.assertFalse(builder.textFlag);
        Assert.assertFalse(builder.textShadow1);
        Assert.assertFalse(builder.textBorder1);
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
        Assert.assertFalse(builder.textStyle);
        Assert.assertFalse(builder.textFlag);
        Assert.assertFalse(builder.textShadow1);
        Assert.assertFalse(builder.textBorder1);
    }

    private class MockTextStyleBuilder extends TextStyleBuilder {
        boolean textSizeApplied = false;
        boolean textColorApplied = false;
        boolean gravityApplied = false;
        boolean textFontApplied = false;
        boolean backgroundColorApplied = false;
        boolean backgroundDrawableApplied = false;
        boolean textAppearanceApplied = false;
        boolean textStyle=false;
        boolean textFlag=false;
        boolean textShadow1=false;
        boolean textBorder1=false;

        @Override
        protected void applyTextStyle(TextView textView, int typeface) {
            textStyle=true;
        }

        @Override
        protected void applyTextFlag(TextView textView, int flag) {
            textFlag=true;
        }

        @Override
        protected void applyTextShadow(TextView textView, TextShadow textShadow) {
            textShadow1=true;
        }

        @Override
        protected void applyTextBorder(TextView textView, TextBorder textBorder) {
            textBorder1=true;
        }

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
