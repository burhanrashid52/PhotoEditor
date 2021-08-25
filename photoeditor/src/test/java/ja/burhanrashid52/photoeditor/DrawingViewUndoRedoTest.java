package ja.burhanrashid52.photoeditor;

import android.util.Pair;
import android.view.MotionEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.Stack;

import ja.burhanrashid52.photoeditor.shape.ShapeAndPaint;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class DrawingViewUndoRedoTest extends BaseDrawingViewTest {

    @Test
    public void testUndoReturnFalseWhenThereNothingToUndo() {
        DrawingView drawingView = new DrawingView(mContext);
        assertFalse(drawingView.undo());
    }

    @Test
    public void testRedoReturnFalseWhenThereNothingToRedo() {
        DrawingView drawingView = new DrawingView(mContext);
        assertFalse(drawingView.redo());
    }

    @Test
    public void testUndoAndRedoWithListenerWhenAnythingIsDrawnOnCanvas() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        DrawingView drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener);

        // Add 3 Shapes
        swipeView(drawingView);
        swipeView(drawingView);
        swipeView(drawingView);

        verify(brushViewChangeListener, times(3)).onViewAdd(drawingView);
        verifyUndo(drawingView, brushViewChangeListener);
        verifyRedo(drawingView, brushViewChangeListener);
    }

    private void verifyUndo(DrawingView drawingView, BrushViewChangeListener brushViewChangeListener) {
        Pair<Stack<ShapeAndPaint>, Stack<ShapeAndPaint>> drawingPaths = drawingView.getDrawingPath();
        Stack<ShapeAndPaint> undoPaths = drawingPaths.first;
        Stack<ShapeAndPaint> redoPaths = drawingPaths.second;

        assertEquals(3, undoPaths.size());
        assertEquals(0, redoPaths.size());

        drawingView.undo();
        assertEquals(2, undoPaths.size());
        assertEquals(1, redoPaths.size());
        verify(brushViewChangeListener, times(1)).onViewRemoved(drawingView);

        drawingView.undo();
        assertEquals(1, undoPaths.size());
        assertEquals(2, redoPaths.size());
        verify(brushViewChangeListener, times(2)).onViewRemoved(drawingView);


        drawingView.undo();
        assertEquals(0, undoPaths.size());
        assertEquals(3, redoPaths.size());
        verify(brushViewChangeListener, times(3)).onViewRemoved(drawingView);

    }

    private void verifyRedo(DrawingView drawingView, BrushViewChangeListener brushViewChangeListener) {
        Pair<Stack<ShapeAndPaint>, Stack<ShapeAndPaint>> drawingPaths = drawingView.getDrawingPath();
        Stack<ShapeAndPaint> undoPaths = drawingPaths.first;
        Stack<ShapeAndPaint> redoPaths = drawingPaths.second;

        assertEquals(undoPaths.size(), 0);
        assertEquals(redoPaths.size(), 3);

        drawingView.redo();
        assertEquals(undoPaths.size(), 1);
        assertEquals(redoPaths.size(), 2);
        verify(brushViewChangeListener, times(4)).onViewAdd(drawingView);


        drawingView.redo();
        assertEquals(undoPaths.size(), 2);
        assertEquals(redoPaths.size(), 1);
        verify(brushViewChangeListener, times(5)).onViewAdd(drawingView);


        drawingView.redo();
        assertEquals(undoPaths.size(), 3);
        assertEquals(redoPaths.size(), 0);
        verify(brushViewChangeListener, times(6)).onViewAdd(drawingView);
    }


    @Test
    public void testUndoRedoAndPaintColorWhenEverythingIsCleared() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        DrawingView drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener);
        Pair<Stack<ShapeAndPaint>, Stack<ShapeAndPaint>> paths = drawingView.getDrawingPath();
        Stack<ShapeAndPaint> undoPaths = paths.first;
        Stack<ShapeAndPaint> redoPaths = paths.second;

        ShapeAndPaint linePath = Mockito.mock(ShapeAndPaint.class);

        undoPaths.add(linePath);
        undoPaths.add(linePath);
        redoPaths.add(linePath);

        drawingView.clearAll();
        assertEquals(undoPaths.size(), 0);
        assertEquals(redoPaths.size(), 0);
    }
}
