package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import org.robolectric.RuntimeEnvironment;

import ja.burhanrashid52.photoeditor.shape.ShapeBuilder;

public class BaseDrawingViewTest {
    protected Context mContext = RuntimeEnvironment.systemContext;

    @NonNull
    protected DrawingView setupDrawingView() {
        // create view and ShapeBuilder
        DrawingView drawingView = new DrawingView(mContext);
        drawingView.enableDrawing(true);
        ShapeBuilder shapeBuilder = new ShapeBuilder();
        drawingView.setShapeBuilder(shapeBuilder);

        // Get the created Shape
        return drawingView;
    }

    @NonNull
    protected DrawingView setupDrawingViewWithChangeListener(BrushViewChangeListener brushViewChangeListener) {
        DrawingView drawingView = setupDrawingView();
        drawingView.enableDrawing(true);
        drawingView.setBrushViewChangeListener(brushViewChangeListener);
        return drawingView;
    }

    protected void touchView(DrawingView drawingView, int action) {
        MotionEvent touchEventOne = MotionEvent.obtain(
                200,
                300,
                action,
                150.0f,
                100.0f,
                0);
        drawingView.dispatchTouchEvent(touchEventOne);
    }

    protected void swipeView(DrawingView drawingView) {
        MotionEvent actionDown = MotionEvent.obtain(
                200,
                300,
                MotionEvent.ACTION_DOWN,
                10.0f,
                100.0f,
                0);
        drawingView.dispatchTouchEvent(actionDown);

        for (int i = 0; i < 100; i++) {
            MotionEvent actionMove = MotionEvent.obtain(
                    200,
                    300,
                    MotionEvent.ACTION_MOVE,
                    10.0f + i * 5,
                    100.0f,
                    0);
            drawingView.dispatchTouchEvent(actionMove);
        }

        MotionEvent actionUp = MotionEvent.obtain(
                200,
                300,
                MotionEvent.ACTION_UP,
                510.0f,
                100.0f,
                0);
        drawingView.dispatchTouchEvent(actionUp);

    }
}
