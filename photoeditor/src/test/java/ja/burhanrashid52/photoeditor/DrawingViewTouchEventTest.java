package ja.burhanrashid52.photoeditor;

import android.util.Pair;
import android.view.MotionEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.Stack;

import ja.burhanrashid52.photoeditor.shape.ShapeAndPaint;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class DrawingViewTouchEventTest extends BaseDrawingViewTest {

    @Test
    public void testDrawingShouldNotWorkWhenDisabled() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        DrawingView drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener);
        drawingView.enableDrawing(false);

        touchView(drawingView, MotionEvent.ACTION_DOWN);

        verify(brushViewChangeListener, times(0)).onStartDrawing();
    }

    @Test
    public void testDrawingChangeListenerAndPathWhenShapeisCreated() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        DrawingView drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener);

        swipeView(drawingView);
        verify(brushViewChangeListener, times(1)).onStartDrawing();

        Pair<Stack<ShapeAndPaint>, Stack<ShapeAndPaint>> drawingPath = drawingView.getDrawingPath();
        Stack<ShapeAndPaint> drawnPath = drawingPath.first;
        Stack<ShapeAndPaint> redoPaths = drawingPath.second;
        assertFalse(drawnPath.empty());
        assertTrue(redoPaths.empty());
    }

    @Test
    public void testDrawingChangeListenerAndPathWhenTouchIsMove() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        DrawingView drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener);

        touchView(drawingView, MotionEvent.ACTION_MOVE);

        verify(brushViewChangeListener, times(0)).onStartDrawing();
        verify(brushViewChangeListener, times(0)).onStopDrawing();
        verify(brushViewChangeListener, times(0)).onViewAdd(drawingView);
        verify(brushViewChangeListener, times(0)).onViewRemoved(drawingView);

        Pair<Stack<ShapeAndPaint>, Stack<ShapeAndPaint>> drawingPath = drawingView.getDrawingPath();
        Stack<ShapeAndPaint> drawnPath = drawingPath.first;
        Stack<ShapeAndPaint> redoPaths = drawingPath.second;
        assertTrue(drawnPath.empty());
        assertTrue(redoPaths.empty());
    }

    @Test
    public void testBrushDrawingChangeListenerAndPathWhenTouchIsUp() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        DrawingView drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener);

        MotionEvent touchEventUp = MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, 150.0f, 100.0f, 0);
        drawingView.dispatchTouchEvent(touchEventUp);
        verify(brushViewChangeListener, times(0)).onStartDrawing();
        verify(brushViewChangeListener, times(0)).onStopDrawing();
        verify(brushViewChangeListener, times(0)).onViewAdd(drawingView);
        verify(brushViewChangeListener, times(0)).onViewRemoved(drawingView);

        Pair<Stack<ShapeAndPaint>, Stack<ShapeAndPaint>> drawingPath = drawingView.getDrawingPath();
        Stack<ShapeAndPaint> drawnPath = drawingPath.first;
        Stack<ShapeAndPaint> redoPaths = drawingPath.second;
        assertTrue(drawnPath.empty());
        assertTrue(redoPaths.empty());
    }

    @Test
    public void testPathDrawnOnTouchEvents() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        DrawingView drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener);

        float touchDownX = 150.0f;
        float touchDownY = 100.0f;
        MotionEvent touchEventDown = MotionEvent.obtain(200, 300, MotionEvent.ACTION_DOWN, touchDownX, touchDownY, 0);
        drawingView.dispatchTouchEvent(touchEventDown);

        float touchMoveX = 160.0f;
        float touchMoveY = 110.0f;
        MotionEvent touchEventMove = MotionEvent.obtain(200, 300, MotionEvent.ACTION_MOVE, touchMoveX, touchMoveY, 0);
        drawingView.dispatchTouchEvent(touchEventMove);

        MotionEvent touchEventUp = MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, 150.0f, 100.0f, 0);
        drawingView.dispatchTouchEvent(touchEventUp);

        Pair<Stack<ShapeAndPaint>, Stack<ShapeAndPaint>> drawingPath = drawingView.getDrawingPath();
        Stack<ShapeAndPaint> drawnPath = drawingPath.first;

        /*LinePath linePath = drawnPath.pop();
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
