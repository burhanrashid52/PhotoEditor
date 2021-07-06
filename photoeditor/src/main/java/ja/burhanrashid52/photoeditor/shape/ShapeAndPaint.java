package ja.burhanrashid52.photoeditor.shape;

import android.graphics.Paint;

/**
 * Simple data class to be put in an ordered Stack
 */
public class ShapeAndPaint {
    private final AbstractShape shape;
    private final Paint paint;

    public ShapeAndPaint(AbstractShape shape, Paint paint) {
        this.shape = shape;
        this.paint = paint;
    }

    public AbstractShape getShape() {
        return shape;
    }

    public Paint getPaint() {
        return paint;
    }
}
