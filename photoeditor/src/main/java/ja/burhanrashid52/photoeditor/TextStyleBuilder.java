package ja.burhanrashid52.photoeditor;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class TextStyleBuilder {

    private TextStyleBuilder() { }
    private Map<TextStyle, Object> values = new HashMap<>();

    public void withTextSize(@NonNull float size) {
        values.put(TextStyle.Size, size);
    }

    public void withTextColor(@NonNull int color) {
        values.put(TextStyle.Color, color);
    }

    public void withTextFont(@NonNull Typeface textTypeface) {
        values.put(TextStyle.FontFamily, textTypeface);
    }

    public void withGravity(@NonNull int gravity) {
        values.put(TextStyle.Gravity, gravity);
    }

    public void withBackgroundColor(@NonNull int background) {
        values.put(TextStyle.background, background);
    }

    public void withBackgroundDrawable(@NonNull Drawable bgDrawable) {
        values.put(TextStyle.background, bgDrawable);
    }

    public void withTextAppearance(@NonNull int textAppearance) {
        values.put(TextStyle.textAppearance, textAppearance);
    }

    public static TextStyleBuilder createBuilder() {
        return new TextStyleBuilder();
    }

    void applyStyle(@NonNull TextView textView) {
        for (Map.Entry<TextStyle, Object> entry : values.entrySet()) {
            switch (entry.getKey()) {
                case Size: {
                    final float size = (float) entry.getValue();
                    textView.setTextSize(size);
                }
                break;

                case Color: {
                    final int color = (int) entry.getValue();
                    textView.setTextColor(color);
                }
                break;

                case FontFamily: {
                    final Typeface typeface = (Typeface) entry.getValue();
                    textView.setTypeface(typeface);
                }
                break;

                case Gravity: {
                    final int gravity = (int) entry.getValue();
                    textView.setGravity(gravity);
                }
                break;

                case background: {
                    if (entry.getValue() instanceof Drawable) {
                        final Drawable bg = (Drawable) entry.getValue();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            textView.setBackground(bg);
                        } else {
                            textView.setBackgroundDrawable(bg);
                        }

                    } else if (entry.getValue() instanceof Integer) {
                        final int color = (Integer) entry.getValue();
                        textView.setBackgroundColor(color);
                    }
                }
                break;

                case textAppearance: {
                    if (entry.getValue() instanceof Integer) {
                        final int styleAppearance = (Integer)entry.getValue();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            textView.setTextAppearance(styleAppearance);
                        } else {
                            textView.setTextAppearance(textView.getContext(), styleAppearance);
                        }
                    }
                }
                break;
            }
        }
    }

    private enum TextStyle {
        Size("TextSize"),
        Color("TextColor"),
        Gravity("Gravity"),
        FontFamily("FontFamily"),
        background("Background"),
        textAppearance("textAppearance");

        TextStyle(String property) {
            this.property = property;
        }

        private String property;
        public String getProperty() {return property;}
    }
}