package ja.burhanrashid52.photoeditor.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

abstract public class AbstractShape implements Shape {

    protected float TOUCH_TOLERANCE = 4;

    protected Path path = new Path();
    protected float left, top, right, bottom;
    protected abstract String getTag();

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawPath(path, paint);
    }

    public RectF getBounds() {
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        return bounds;
    }

    public boolean hasBeenTapped() {
        RectF bounds = getBounds();
        return bounds.top < TOUCH_TOLERANCE &&
                bounds.bottom < TOUCH_TOLERANCE &&
                bounds.left < TOUCH_TOLERANCE &&
                bounds.right < TOUCH_TOLERANCE;
    }

    public String toString() {
        return getTag() +
                ": left: " + left +
                " - top: " + top +
                " - right: " + right +
                " - bottom: " + bottom;
    }

}
