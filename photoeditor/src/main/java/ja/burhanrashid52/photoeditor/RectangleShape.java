package ja.burhanrashid52.photoeditor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import androidx.annotation.NonNull;

public class RectangleShape extends AbstractShape {

    private float lastX, lastY;

    @Override
    protected String getTag() { return "RectangleShape"; }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawPath(path, paint);
    }

    @Override
    public void startShape(float x, float y) {
        Log.d(getTag(), "startShape@ " + x + "," + y);
        left = x;
        top = y;
    }

    @Override
    public void moveShape(float x, float y) {
        right = x;
        bottom = y;

        float dx = Math.abs(x - lastX);
        float dy = Math.abs(y - lastY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path = createRectanglePath();
            lastX = x;
            lastY = y;
        }
    }

    private @NonNull Path createRectanglePath() {
        Path path = new Path();
        path.moveTo(left, top);
        path.lineTo(left, bottom);
        path.lineTo(right, bottom);
        path.lineTo(right, top);
        path.close();
        return path;
    }

    @Override
    public void stopShape() {
        Log.d(getTag(), "stopShape");
    }

}
