package ja.burhanrashid52.photoeditor;

import android.util.Pair;
import android.view.MotionEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowPath;

import java.util.Stack;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class BrushDrawingViewTouchEventTest extends BaseBrushDrawingViewTest {

    @Test
    public void testDrawingShouldNotWorkWhenBrushIsDisabled() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        BrushDrawingView brushDrawingView = setupBrushForTouchEvents(brushViewChangeListener);
        brushDrawingView.setBrushDrawingMode(false);

        MotionEvent touchEventDown = MotionEvent.obtain(200, 300, MotionEvent.ACTION_DOWN, 15.0f, 10.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventDown);
        verify(brushViewChangeListener, times(0)).onStartDrawing();
    }

    @Test
    public void testBrushDrawingChangeListenerAndPathWhenTouchIsDown() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        BrushDrawingView brushDrawingView = setupBrushForTouchEvents(brushViewChangeListener);

        MotionEvent touchEventDown = MotionEvent.obtain(200, 300, MotionEvent.ACTION_DOWN, 15.0f, 10.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventDown);
        verify(brushViewChangeListener, times(1)).onStartDrawing();

        Pair<Stack<LinePath>, Stack<LinePath>> drawingPath = brushDrawingView.getDrawingPath();
        Stack<LinePath> drawnPath = drawingPath.first;
        Stack<LinePath> redoPaths = drawingPath.second;
        assertTrue(drawnPath.empty());
        assertTrue(redoPaths.empty());
    }

    @Test
    public void testBrushDrawingChangeListenerAndPathWhenTouchIsMove() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        BrushDrawingView brushDrawingView = setupBrushForTouchEvents(brushViewChangeListener);

        MotionEvent touchEventMove = MotionEvent.obtain(200, 300, MotionEvent.ACTION_MOVE, 15.0f, 10.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventMove);
        verify(brushViewChangeListener, times(0)).onStartDrawing();
        verify(brushViewChangeListener, times(0)).onStopDrawing();
        verify(brushViewChangeListener, times(0)).onViewAdd(brushDrawingView);
        verify(brushViewChangeListener, times(0)).onViewRemoved(brushDrawingView);

        Pair<Stack<LinePath>, Stack<LinePath>> drawingPath = brushDrawingView.getDrawingPath();
        Stack<LinePath> drawnPath = drawingPath.first;
        Stack<LinePath> redoPaths = drawingPath.second;
        assertTrue(drawnPath.empty());
        assertTrue(redoPaths.empty());
    }

    @Test
    public void testBrushDrawingChangeListenerAndPathWhenTouchIsUp() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        BrushDrawingView brushDrawingView = setupBrushForTouchEvents(brushViewChangeListener);

        MotionEvent touchEventUp = MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, 150.0f, 100.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventUp);
        verify(brushViewChangeListener, times(0)).onStartDrawing();
        verify(brushViewChangeListener, times(1)).onStopDrawing();
        verify(brushViewChangeListener, times(1)).onViewAdd(brushDrawingView);
        verify(brushViewChangeListener, times(0)).onViewRemoved(brushDrawingView);

        Pair<Stack<LinePath>, Stack<LinePath>> drawingPath = brushDrawingView.getDrawingPath();
        Stack<LinePath> drawnPath = drawingPath.first;
        Stack<LinePath> redoPaths = drawingPath.second;
        assertFalse(drawnPath.empty());
        assertTrue(redoPaths.empty());

        LinePath linePath = drawnPath.pop();
        assertEquals(linePath.getDrawPaint().getColor(), brushDrawingView.getDrawingPaint().getColor());
    }

    @Test
    public void testPathDrawnOnTouchEvents() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        BrushDrawingView brushDrawingView = setupBrushForTouchEvents(brushViewChangeListener);

        float touchDownX = 150.0f;
        float touchDownY = 100.0f;
        MotionEvent touchEventDown = MotionEvent.obtain(200, 300, MotionEvent.ACTION_DOWN, touchDownX, touchDownY, 0);
        brushDrawingView.dispatchTouchEvent(touchEventDown);

        float touchMoveX = 160.0f;
        float touchMoveY = 110.0f;
        MotionEvent touchEventMove = MotionEvent.obtain(200, 300, MotionEvent.ACTION_MOVE, touchMoveX, touchMoveY, 0);
        brushDrawingView.dispatchTouchEvent(touchEventMove);

        MotionEvent touchEventUp = MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, 150.0f, 100.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventUp);

        Pair<Stack<LinePath>, Stack<LinePath>> drawingPath = brushDrawingView.getDrawingPath();
        Stack<LinePath> drawnPath = drawingPath.first;

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

    }
}
