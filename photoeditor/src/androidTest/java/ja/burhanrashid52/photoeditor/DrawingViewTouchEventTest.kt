package ja.burhanrashid52.photoeditor

import android.graphics.Paint
import android.view.MotionEvent
import androidx.test.ext.junit.runners.AndroidJUnit4
import ja.burhanrashid52.photoeditor.shape.OvalShape
import ja.burhanrashid52.photoeditor.shape.RectangleShape
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import junit.framework.TestCase.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
internal class DrawingViewTouchEventTest : BaseDrawingViewTest() {

    @Test
    fun testDrawingShouldNotWorkWhenDisabled() {
        val brushViewChangeListener = Mockito.mock(BrushViewChangeListener::class.java)
        val drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener)
        drawingView.enableDrawing(false)
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        Mockito.verify(brushViewChangeListener, Mockito.times(0)).onStartDrawing()
    }

    @Test
    fun testDrawingChangeListenerAndPathWhenShapeIsCreated() {
        val brushViewChangeListener = Mockito.mock(BrushViewChangeListener::class.java)
        val drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener)

        swipeView(drawingView)
        Mockito.verify(brushViewChangeListener, Mockito.times(1)).onStartDrawing()

        val drawingPath = drawingView.drawingPath
        val drawnPath = drawingPath.first
        val redoPaths = drawingPath.second
        assertFalse(drawnPath.empty())
        assertTrue(redoPaths.empty())
    }

    @Test
    fun testDrawingChangeListenerAndPathWhenTouchIsMove() {
        val brushViewChangeListener = Mockito.mock(BrushViewChangeListener::class.java)
        val drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener)
        touchView(drawingView, MotionEvent.ACTION_MOVE)

        Mockito.verify(brushViewChangeListener, Mockito.times(0)).onStartDrawing()
        Mockito.verify(brushViewChangeListener, Mockito.times(0)).onStopDrawing()
        Mockito.verify(brushViewChangeListener, Mockito.times(0)).onViewAdd(drawingView)
        Mockito.verify(brushViewChangeListener, Mockito.times(0)).onViewRemoved(drawingView)

        val drawingPath = drawingView.drawingPath
        val drawnPath = drawingPath.first
        val redoPaths = drawingPath.second
        assertTrue(drawnPath.empty())
        assertTrue(redoPaths.empty())
    }

    @Test
    fun testBrushDrawingChangeListenerAndPathWhenTouchIsUp() {
        val brushViewChangeListener = Mockito.mock(BrushViewChangeListener::class.java)
        val drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener)

        val touchEventUp = MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, 150.0f, 100.0f, 0)
        drawingView.dispatchTouchEvent(touchEventUp)

        Mockito.verify(brushViewChangeListener, Mockito.times(0)).onStartDrawing()
        Mockito.verify(brushViewChangeListener, Mockito.times(0)).onStopDrawing()
        Mockito.verify(brushViewChangeListener, Mockito.times(0)).onViewAdd(drawingView)
        Mockito.verify(brushViewChangeListener, Mockito.times(0)).onViewRemoved(drawingView)

        val drawingPath = drawingView.drawingPath
        val drawnPath = drawingPath.first
        val redoPaths = drawingPath.second
        assertTrue(drawnPath.empty())
        assertTrue(redoPaths.empty())
    }

    @Test
    fun testTapWithOvalDrawsUniformFilledCircle() {
        val drawingView = setupDrawingView()
        drawingView.currentShapeBuilder = ShapeBuilder().withShapeType(ShapeType.Oval)

        tapView(drawingView, 150.0f, 100.0f)

        val top = drawingView.drawingPath.first
        assertFalse(top.empty())
        assertTrue(top.peek()?.shape is OvalShape)
        assertEquals(Paint.Style.FILL, top.peek()?.paint?.style)
    }

    @Test
    fun testTapWithRectangleDrawsUniformFilledSquare() {
        val drawingView = setupDrawingView()
        drawingView.currentShapeBuilder = ShapeBuilder().withShapeType(ShapeType.Rectangle)

        tapView(drawingView, 150.0f, 100.0f)

        val top = drawingView.drawingPath.first
        assertFalse(top.empty())
        assertTrue(top.peek()?.shape is RectangleShape)
        assertEquals(Paint.Style.FILL, top.peek()?.paint?.style)
    }

    @Test
    fun testTapWithPointlessShapeDrawsNothing() {
        for (shapeType in listOf(ShapeType.Line, ShapeType.Brush, ShapeType.Arrow())) {
            val drawingView = setupDrawingView()
            drawingView.currentShapeBuilder = ShapeBuilder().withShapeType(shapeType)

            tapView(drawingView, 150.0f, 100.0f)

            assertTrue("tap with $shapeType should draw nothing", drawingView.drawingPath.first.empty())
        }
    }

    @Test
    fun testTapWhileErasingDrawsNothing() {
        val drawingView = setupDrawingView()
        drawingView.brushEraser()

        tapView(drawingView, 150.0f, 100.0f)

        val drawnShapes = drawingView.drawingPath.first
        assertTrue(drawnShapes.empty())
    }

    private fun tapView(drawingView: DrawingView, x: Float, y: Float) {
        drawingView.dispatchTouchEvent(
            MotionEvent.obtain(200, 300, MotionEvent.ACTION_DOWN, x, y, 0)
        )
        drawingView.dispatchTouchEvent(
            MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, x, y, 0)
        )
    }

    @Test
    fun testPathDrawnOnTouchEvents() {
        val brushViewChangeListener = Mockito.mock(BrushViewChangeListener::class.java)
        val drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener)

        val touchDownX = 150.0f
        val touchDownY = 100.0f
        val touchEventDown =
            MotionEvent.obtain(200, 300, MotionEvent.ACTION_DOWN, touchDownX, touchDownY, 0)
        drawingView.dispatchTouchEvent(touchEventDown)

        val touchMoveX = 160.0f
        val touchMoveY = 110.0f

        val touchEventMove =
            MotionEvent.obtain(200, 300, MotionEvent.ACTION_MOVE, touchMoveX, touchMoveY, 0)
        drawingView.dispatchTouchEvent(touchEventMove)

        val touchEventUp = MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, 150.0f, 100.0f, 0)
        drawingView.dispatchTouchEvent(touchEventUp)

        /* val drawingPath = drawingView.drawingPath
        val drawnPath = drawingPath.first

        LinePath linePath = drawnPath.pop();
        ShadowPath shadowPath = shadowOf(linePath.getDrawPath());

        ShadowPath.Point pointOne = shadowPath.getPoints().get(0);
        assertEquals(pointOne.getType(), ShadowPath.Point.Type.MOVE_TO);
        assertEquals(pointOne.getX(), touchDownX);
        assertEquals(pointOne.getY(), touchDownY);

        ShadowPath.Point pointTwo = shadowPath.getPoints().get(1);
        assertEquals(pointTwo.getType(), ShadowPath.Point.Type.LINE_TO);
        assertEquals(pointTwo.getX(), touchMoveX);
        assertEquals(pointTwo.getY(), touchMoveY);
        */
    }
}