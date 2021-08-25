package ja.burhanrashid52.photoeditor.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class BrushShape extends AbstractShape {

    @Override
    protected String getTag() { return "BrushShape"; }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawPath(path, paint);
    }

    @Override
    public void startShape(float x, float y) {
        Log.d(getTag(), "startShape@ " + x + "," + y);
        path.moveTo(x, y);
        left = x;
        top = y;
    }

    @Override
    public void moveShape(float x, float y) {
        float dx = Math.abs(x - left);
        float dy = Math.abs(y - top);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(left, top, (x + left) / 2, (y + top) / 2);
            left = x;
            top = y;
        }
    }

    @Override
    public void stopShape() {
        Log.d(getTag(), "stopShape");
    }

}
