package ja.burhanrashid52.photoeditor.filters;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static ja.burhanrashid52.photoeditor.filters.PhotoFilter.*;


@IntDef({
        NONE, AUTO_FIX, BLACK_WHITE, BRIGHTNESS, CONTRAST, CROSS_PROCESS, DOCUMENTARY,
        DUE_TONE, FILL_LIGHT, FISH_EYE, FLIPVERT, FLIPHOR, GRAIN, GRAY_SCALE, LOMISH, NEGATIVE,
        POSTERIZE, ROTATE, SATURATE, SEPIA, SHARPEN, TEMPERATURE, TINT, VIGNETTE
})
@Retention(RetentionPolicy.SOURCE)
public @interface FilterType {
}