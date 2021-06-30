package ja.burhanrashid52.photoeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

class ShapeView extends View {

    private final String TAG = "ShapeView";
    private final Map<Shape, Paint> shapesAndPaints = new LinkedHashMap<>();
    private AbstractShape currentShape;
    private ShapeBuilder currentShapeBuilder;


    // region constructors
    public ShapeView(Context context) {
        super(context);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    // endregion

    // region View and events
    private Paint createPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        // apply shape builder parameters
        paint.setStrokeWidth(currentShapeBuilder.getShapeSize());
        paint.setAlpha(currentShapeBuilder.getShapeOpacity());
        paint.setColor(currentShapeBuilder.getShapeColor());

        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Shape shape : shapesAndPaints.keySet()) {
            Log.d(TAG, shape.toString());
            shape.draw(canvas, shapesAndPaints.get(shape));
        }
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
                createShape();
                currentShape.startShape(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                currentShape.moveShape(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                currentShape.stopShape();
                endShape(touchX, touchY);
                break;
        }
        invalidate();
        return true;
    }
    // endregion

    private void createShape() {
        final AbstractShape shape;
        if (currentShapeBuilder.getShapeType() == ShapeType.OVAL) {
            shape = new OvalShape();
        } else if (currentShapeBuilder.getShapeType() == ShapeType.RECTANGLE) {
            shape = new RectangleShape();
        } else {
            shape = new LineShape();
        }
        currentShape = shape;

        shapesAndPaints.put(currentShape, createPaint());
        Log.d(TAG, "Created shape: " + shape.toString());
    }

    private void endShape(float touchX, float touchY) {
        if (currentShape.hasBeenTapped()) {
            // just a touch, this is not a shape, so remove it
            shapesAndPaints.remove(currentShape);
            handleTap(touchX, touchY);
        }
    }

    private void handleTap(float touchX, float touchY) {
        // TODO find the tapped path to select it
    }

    // Setters/Getters
    public void setShapeBuilder(ShapeBuilder shapeBuilder) {
        currentShapeBuilder = shapeBuilder;
    }

}
