package ja.burhanrashid52.photoeditor;

import android.util.Pair;
import android.view.MotionEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.Stack;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class BrushDrawingViewUndoRedoTest extends BaseBrushDrawingViewTest {

    @Test
    public void testUndoReturnFalseWhenThereNothingToUndo() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        assertFalse(brushDrawingView.undo());
    }

    @Test
    public void testRedoReturnFalseWhenThereNothingToRedo() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        assertFalse(brushDrawingView.redo());
    }

    @Test
    public void testUndoAndRedoWithListenerWhenAnythingIsDrawnOnCanvas() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        BrushDrawingView brushDrawingView = setupBrushForTouchEvents(brushViewChangeListener);

        MotionEvent touchEventOne = MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, 150.0f, 100.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventOne);
        MotionEvent touchEventTwo = MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, 150.0f, 100.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventTwo);
        MotionEvent touchEventThree = MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, 150.0f, 100.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventThree);
        verify(brushViewChangeListener, times(3)).onViewAdd(brushDrawingView);

        verifyUndo(brushDrawingView, brushViewChangeListener);

        verifyRedo(brushDrawingView, brushViewChangeListener);


    }

    private void verifyUndo(BrushDrawingView brushDrawingView, BrushViewChangeListener brushViewChangeListener) {
        Pair<Stack<LinePath>, Stack<LinePath>> drawingPaths = brushDrawingView.getDrawingPath();
        Stack<LinePath> undoPaths = drawingPaths.first;
        Stack<LinePath> redoPaths = drawingPaths.second;

        assertEquals(undoPaths.size(), 3);
        assertEquals(redoPaths.size(), 0);

        brushDrawingView.undo();
        assertEquals(undoPaths.size(), 2);
        assertEquals(redoPaths.size(), 1);
        verify(brushViewChangeListener, times(1)).onViewRemoved(brushDrawingView);

        brushDrawingView.undo();
        assertEquals(undoPaths.size(), 1);
        assertEquals(redoPaths.size(), 2);
        verify(brushViewChangeListener, times(2)).onViewRemoved(brushDrawingView);


        brushDrawingView.undo();
        assertEquals(undoPaths.size(), 0);
        assertEquals(redoPaths.size(), 3);
        verify(brushViewChangeListener, times(3)).onViewRemoved(brushDrawingView);

    }

    private void verifyRedo(BrushDrawingView brushDrawingView, BrushViewChangeListener brushViewChangeListener) {
        Pair<Stack<LinePath>, Stack<LinePath>> drawingPaths = brushDrawingView.getDrawingPath();
        Stack<LinePath> undoPaths = drawingPaths.first;
        Stack<LinePath> redoPaths = drawingPaths.second;

        assertEquals(undoPaths.size(), 0);
        assertEquals(redoPaths.size(), 3);

        brushDrawingView.redo();
        assertEquals(undoPaths.size(), 1);
        assertEquals(redoPaths.size(), 2);
        verify(brushViewChangeListener, times(4)).onViewAdd(brushDrawingView);


        brushDrawingView.redo();
        assertEquals(undoPaths.size(), 2);
        assertEquals(redoPaths.size(), 1);
        verify(brushViewChangeListener, times(5)).onViewAdd(brushDrawingView);


        brushDrawingView.redo();
        assertEquals(undoPaths.size(), 3);
        assertEquals(redoPaths.size(), 0);
        verify(brushViewChangeListener, times(6)).onViewAdd(brushDrawingView);
    }


    @Test
    public void testUndoRedoAndPaintColorWhenEverythingIsCleared() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        BrushDrawingView brushDrawingView = setupBrushForTouchEvents(brushViewChangeListener);
        Pair<Stack<LinePath>, Stack<LinePath>> paths = brushDrawingView.getDrawingPath();
        Stack<LinePath> undoPaths = paths.first;
        Stack<LinePath> redoPaths = paths.second;

        LinePath linePath = Mockito.mock(LinePath.class);

        undoPaths.add(linePath);
        undoPaths.add(linePath);

        redoPaths.add(linePath);

        brushDrawingView.clearAll();
        assertEquals(undoPaths.size(), 0);
        assertEquals(redoPaths.size(), 0);
    }
}
