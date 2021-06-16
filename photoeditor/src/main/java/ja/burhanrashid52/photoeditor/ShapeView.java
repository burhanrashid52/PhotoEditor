package ja.burhanrashid52.photoeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class ShapeView extends View {

    private final Paint paint = new Paint();
    private Shape shape;


    public ShapeView(Context context) {
        super(context);
        setupView();
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupView();
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView();
    }

    private void setupView() {
        setupPaint();
    }

    private void setupPaint() {
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        shape.draw(canvas, paint);
    }

    /**
     * Handle touch event to draw shape on Canvas
     *
     * @param event points having touch info
     * @return true if handling touch events
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                shape.startShape(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                shape.moveShape(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                shape.stopShape();
                break;
        }
        invalidate();
        return true;
    }

    // Setters/Getters
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public void setSize(float size) {
        paint.setStrokeWidth(size);
    }

    public void setOpacity(int opacity) {
        paint.setAlpha(opacity);
        Log.d("ShapeView", "Set opacity to " + opacity);
    }

    public void setColor(int color) {
        paint.setColor(color);
        Log.d("ShapeView", "Set color to " + color);
    }

}
