package ja.burhanrashid52.photoeditor

import android.view.MotionEvent
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import org.robolectric.RuntimeEnvironment

internal open class BaseDrawingViewTest {
    protected var mContext = RuntimeEnvironment.systemContext
    protected fun setupDrawingView(): DrawingView {
        // create view and ShapeBuilder
        val drawingView = DrawingView(mContext)
        drawingView.enableDrawing(true)
        val shapeBuilder = ShapeBuilder()
        drawingView.currentShapeBuilder = shapeBuilder

        // Get the created Shape
        return drawingView
    }

    protected fun setupDrawingViewWithChangeListener(brushViewChangeListener: BrushViewChangeListener?): DrawingView {
        val drawingView = setupDrawingView()
        drawingView.enableDrawing(true)
        drawingView.setBrushViewChangeListener(brushViewChangeListener)
        return drawingView
    }

    protected fun touchView(drawingView: DrawingView, action: Int) {
        val touchEventOne = MotionEvent.obtain(
            200,
            300,
            action,
            150.0f,
            100.0f,
            0
        )
        drawingView.dispatchTouchEvent(touchEventOne)
    }

    protected fun swipeView(drawingView: DrawingView) {
        val actionDown = MotionEvent.obtain(
            200,
            300,
            MotionEvent.ACTION_DOWN,
            10.0f,
            100.0f,
            0
        )
        drawingView.dispatchTouchEvent(actionDown)
        for (i in 0..99) {
            val actionMove = MotionEvent.obtain(
                200,
                300,
                MotionEvent.ACTION_MOVE,
                10.0f + i * 5,
                100.0f,
                0
            )
            drawingView.dispatchTouchEvent(actionMove)
        }
        val actionUp = MotionEvent.obtain(
            200,
            300,
            MotionEvent.ACTION_UP,
            510.0f,
            100.0f,
            0
        )
        drawingView.dispatchTouchEvent(actionUp)
    }
}