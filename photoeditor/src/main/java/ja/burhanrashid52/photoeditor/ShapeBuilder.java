package ja.burhanrashid52.photoeditor;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;

import static ja.burhanrashid52.photoeditor.BrushDrawingView.DEFAULT_BRUSH_SIZE;
import static ja.burhanrashid52.photoeditor.BrushDrawingView.DEFAULT_OPACITY;

/**
 * <p>
 * Used to hold a Shape parameters: type, size, opacity and color.
 * </p>
 */
public class ShapeBuilder {

    private ShapeType currentShapeType;
    private float currentShapeSize;
    @IntRange(from = 0, to = 255) private int currentShapeOpacity;
    @ColorInt private int currentShapeColor;


    public ShapeBuilder() {
        // default values
        withShapeType(ShapeType.LINE);
        withShapeSize(DEFAULT_BRUSH_SIZE);
        withShapeOpacity(DEFAULT_OPACITY);
        withShapeColor(Color.BLACK);
    }

    public ShapeBuilder withShapeType(ShapeType shapeType) {
        currentShapeType = shapeType;
        return this;
    }

    public ShapeType getShapeType() {
        return currentShapeType;
    }

    public ShapeBuilder withShapeSize(float size) {
        currentShapeSize = size;
        return this;
    }

    public float getShapeSize() {
        return currentShapeSize;
    }

    public ShapeBuilder withShapeOpacity(@IntRange(from = 0, to = 255) int opacity) {
        currentShapeOpacity = opacity;
        return this;
    }

    public @IntRange(from = 0, to = 255) int getShapeOpacity() {
        return currentShapeOpacity;
    }

    public ShapeBuilder withShapeColor(@ColorInt int color) {
        currentShapeColor = color;
        return this;
    }

    public @ColorInt int getShapeColor() {
        return currentShapeColor;
    }
}
