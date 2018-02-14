package ja.burhanrashid52.photoeditor.filters;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static ja.burhanrashid52.photoeditor.filters.FilterType.*;


@IntDef({
        NONE, AUTO_FIX, BLACK_WHITE, BRIGHTNESS, CONTRAST, CROSS_PROCESS, DOCUMENTARY,
        DUE_TONE, FILL_LIGHT, FISH_EYE, FLIPVERT, FLIPHOR, GRAIN, GRAY_SCALE, LOMISH, NEGATIVE,
        POSTERIZE, ROTATE, SATURATE, SEPIA, SHARPEN, TEMPERATURE, TINT, VIGNETTE
})
@Retention(RetentionPolicy.SOURCE)
public @interface FilterType {
    int NONE = 0;
    int AUTO_FIX = 1;
    int BLACK_WHITE = 2;
    int BRIGHTNESS = 3;
    int CONTRAST = 4;
    int CROSS_PROCESS = 5;
    int DOCUMENTARY = 6;
    int DUE_TONE = 7;
    int FILL_LIGHT = 8;
    int FISH_EYE = 9;
    int FLIPVERT = 10;
    int FLIPHOR = 11;
    int GRAIN = 12;
    int GRAY_SCALE = 13;
    int LOMISH = 14;
    int NEGATIVE = 15;
    int POSTERIZE = 16;
    int ROTATE = 17;
    int SATURATE = 18;
    int SEPIA = 19;
    int SHARPEN = 20;
    int TEMPERATURE = 21;
    int TINT = 22;
    int VIGNETTE = 23;
}