package ja.burhanrashid52.photoeditor;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface Shape {

    void draw(Canvas canvas, Paint paint);
    void startShape(float x, float y);
    void moveShape(float x, float y);
    void stopShape();

    float TOUCH_TOLERANCE = 4;
}

