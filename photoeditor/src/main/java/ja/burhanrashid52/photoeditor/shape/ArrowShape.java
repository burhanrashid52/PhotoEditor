package ja.burhanrashid52.photoeditor.shape;

import android.graphics.Path;
import android.util.Log;

import androidx.annotation.NonNull;

public class ArrowShape extends AbstractShape {

    private float lastX, lastY;

    @Override
    protected String getTag() {
        return "ArrowShape";
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
            path = createArrowPath();
            lastX = x;
            lastY = y;
        }
    }

    private @NonNull
    Path createArrowPath() {

        Path path = new Path();
        float headLength = 100;

        float degreesInRadians225 = (float) (225 * Math.PI / 180);
        float degreesInRadians135 = (float) (135 * Math.PI / 180);

        // calc the angle of the line
        float dx = right - left;
        float dy = bottom - top;
        float angle = (float) Math.atan2(dy, dx);

        // calc arrowhead points
        float x225 = (float) (right + headLength * Math.cos(angle + degreesInRadians225));
        float y225 = (float) (bottom + headLength * Math.sin(angle + degreesInRadians225));
        float x135 = (float) (right + headLength * Math.cos(angle + degreesInRadians135));
        float y135 = (float) (bottom + headLength * Math.sin(angle + degreesInRadians135));

        // draw line
        path.moveTo(left, top);
        path.lineTo(right, bottom);

        // draw partial arrowhead at 225 degrees
        path.moveTo(right, bottom);
        path.lineTo(x225, y225);

        // draw partial arrowhead at 135 degrees
        path.moveTo(right, bottom);
        path.lineTo(x135, y135);

        path.close();
        return path;
    }

    @Override
    public void stopShape() {
        Log.d(getTag(), "ArrowShape");
    }
}
