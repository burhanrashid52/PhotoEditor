package ja.burhanrashid52.photoeditor.shape;

import android.graphics.Path;
import android.util.Log;

import androidx.annotation.NonNull;

public class LineShape extends AbstractShape {

    private float lastX, lastY;

    @Override
    protected String getTag() { return "LineShape"; }

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
            path = createLinePath();
            lastX = x;
            lastY = y;
        }
    }

    private @NonNull Path createLinePath() {
        Path path = new Path();
        path.moveTo(left, top);
        path.lineTo(right, bottom);
        path.close();
        return path;
    }

    @Override
    public void stopShape() {
        Log.d(getTag(), "stopShape");
    }

}
