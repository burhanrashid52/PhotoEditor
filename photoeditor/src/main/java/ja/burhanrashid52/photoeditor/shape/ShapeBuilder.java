package ja.burhanrashid52.photoeditor.shape;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;


/**
 * <p>
 * Used to hold a Shape parameters: type, size, opacity and color.
 * </p>
 */
public class ShapeBuilder {

    public static final float DEFAULT_SHAPE_SIZE = 25.0f;
    public static final int DEFAULT_SHAPE_OPACITY = 255;
    public static final int DEFAULT_SHAPE_COLOR = Color.BLACK;


    private ShapeType currentShapeType;
    private float currentShapeSize;
    @IntRange(from = 0, to = 255) private int currentShapeOpacity;
    @ColorInt private int currentShapeColor;


    public ShapeBuilder() {
        // default values
        withShapeType(ShapeType.BRUSH);
        withShapeSize(DEFAULT_SHAPE_SIZE);
        withShapeOpacity(DEFAULT_SHAPE_OPACITY);
        withShapeColor(DEFAULT_SHAPE_COLOR);
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
