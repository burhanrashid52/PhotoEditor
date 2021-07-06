package ja.burhanrashid52.photoeditor.shape;

import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.NonNull;

public class OvalShape extends AbstractShape {

    private float lastX, lastY;

    @Override
    protected String getTag() { return "OvalShape"; }

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
            path = createOvalPath();
            lastX = x;
            lastY = y;
        }
    }

    private @NonNull Path createOvalPath() {
        RectF rect = new RectF(left, top, right, bottom);
        Path path = new Path();
        path.moveTo(left, top);
        path.addOval(rect, Path.Direction.CW);
        path.close();
        return path;
    }

    @Override
    public void stopShape() {
        Log.d(getTag(), "stopShape");
    }
}
