package ja.burhanrashid52.photoeditor

import android.view.MotionEvent
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.mockito.Mockito
import junit.framework.TestCase
import junit.framework.TestCase.assertFalse
import org.junit.Test

@RunWith(RobolectricTestRunner::class)
internal class DrawingViewTouchEventTest : BaseDrawingViewTest() {
    @Test
    fun testDrawingShouldNotWorkWhenDisabled() {
        val brushViewChangeListener = Mockito.mock(
            BrushViewChangeListener::class.java
        )
        val drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener)
        drawingView.enableDrawing(false)
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        Mockito.verify(brushViewChangeListener, Mockito.times(0)).onStartDrawing()
    }

    @Test
    fun testDrawingChangeListenerAndPathWhenShapeisCreated() {
        val brushViewChangeListener = Mockito.mock(
            BrushViewChangeListener::class.java
        )
        val drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener)
        swipeView(drawingView)
        Mockito.verify(brushViewChangeListener, Mockito.times(1)).onStartDrawing()
        val drawingPath = drawingView.drawingPath
        val drawnPath = drawingPath.first
        val redoPaths = drawingPath.second
        assertFalse(drawnPath.empty())
        TestCase.assertTrue(redoPaths.empty())
    }

    @Test
    fun testDrawingChangeListenerAndPathWhenTouchIsMove() {
        val brushViewChangeListener = Mockito.mock(
            BrushViewChangeListener::class.java
        )
        val drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener)
        touchView(drawingView, MotionEvent.ACTION_MOVE)
        Mockito.verify(brushViewChangeListener, Mockito.times(0)).onStartDrawing()
        Mockito.verify(brushViewChangeListener, Mockito.times(0)).onStopDrawing()
        Mockito.verify(brushViewChangeListener, Mockito.times(0)).onViewAdd(drawingView)
        Mockito.verify(brushViewChangeListener, Mockito.times(0)).onViewRemoved(drawingView)
        val drawingPath = drawingView.drawingPath
        val drawnPath = drawingPath.first
        val redoPaths = drawingPath.second
        TestCase.assertTrue(drawnPath.empty())
        TestCase.assertTrue(redoPaths.empty())
    }

    @Test
    fun testBrushDrawingChangeListenerAndPathWhenTouchIsUp() {
        val brushViewChangeListener = Mockito.mock(
            BrushViewChangeListener::class.java
        )
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
        TestCase.assertTrue(drawnPath.empty())
        TestCase.assertTrue(redoPaths.empty())
    }

    @Test
    fun testPathDrawnOnTouchEvents() {
        val brushViewChangeListener = Mockito.mock(
            BrushViewChangeListener::class.java
        )
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