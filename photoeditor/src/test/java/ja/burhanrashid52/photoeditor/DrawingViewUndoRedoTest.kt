package ja.burhanrashid52.photoeditor

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.mockito.Mockito
import ja.burhanrashid52.photoeditor.shape.ShapeAndPaint
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import org.junit.Test

@RunWith(RobolectricTestRunner::class)
internal class DrawingViewUndoRedoTest : BaseDrawingViewTest() {
    @Test
    fun testUndoReturnFalseWhenThereNothingToUndo() {
        val drawingView = DrawingView(mContext)
        assertFalse(drawingView.undo())
    }

    @Test
    fun testRedoReturnFalseWhenThereNothingToRedo() {
        val drawingView = DrawingView(mContext)
        assertFalse(drawingView.redo())
    }

    @Test
    fun testUndoAndRedoWithListenerWhenAnythingIsDrawnOnCanvas() {
        val brushViewChangeListener = Mockito.mock(
            BrushViewChangeListener::class.java
        )
        val drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener)

        // Add 3 Shapes
        swipeView(drawingView)
        swipeView(drawingView)
        swipeView(drawingView)
        Mockito.verify(brushViewChangeListener, Mockito.times(3)).onViewAdd(drawingView)
        verifyUndo(drawingView, brushViewChangeListener)
        verifyRedo(drawingView, brushViewChangeListener)
    }

    private fun verifyUndo(
        drawingView: DrawingView,
        brushViewChangeListener: BrushViewChangeListener
    ) {
        val drawingPaths = drawingView.drawingPath
        val undoPaths = drawingPaths.first
        val redoPaths = drawingPaths.second
        assertEquals(3, undoPaths.size)
        assertEquals(0, redoPaths.size)
        drawingView.undo()
        assertEquals(2, undoPaths.size)
        assertEquals(1, redoPaths.size)
        Mockito.verify(brushViewChangeListener, Mockito.times(1)).onViewRemoved(drawingView)
        drawingView.undo()
        assertEquals(1, undoPaths.size)
        assertEquals(2, redoPaths.size)
        Mockito.verify(brushViewChangeListener, Mockito.times(2)).onViewRemoved(drawingView)
        drawingView.undo()
        assertEquals(0, undoPaths.size)
        assertEquals(3, redoPaths.size)
        Mockito.verify(brushViewChangeListener, Mockito.times(3)).onViewRemoved(drawingView)
    }

    private fun verifyRedo(
        drawingView: DrawingView,
        brushViewChangeListener: BrushViewChangeListener
    ) {
        val drawingPaths = drawingView.drawingPath
        val undoPaths = drawingPaths.first
        val redoPaths = drawingPaths.second
        assertEquals(undoPaths.size, 0)
        assertEquals(redoPaths.size, 3)
        drawingView.redo()
        assertEquals(undoPaths.size, 1)
        assertEquals(redoPaths.size, 2)
        Mockito.verify(brushViewChangeListener, Mockito.times(4)).onViewAdd(drawingView)
        drawingView.redo()
        assertEquals(undoPaths.size, 2)
        assertEquals(redoPaths.size, 1)
        Mockito.verify(brushViewChangeListener, Mockito.times(5)).onViewAdd(drawingView)
        drawingView.redo()
        assertEquals(undoPaths.size, 3)
        assertEquals(redoPaths.size, 0)
        Mockito.verify(brushViewChangeListener, Mockito.times(6)).onViewAdd(drawingView)
    }

    @Test
    fun testUndoRedoAndPaintColorWhenEverythingIsCleared() {
        val brushViewChangeListener = Mockito.mock(
            BrushViewChangeListener::class.java
        )
        val drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener)
        val paths = drawingView.drawingPath
        val undoPaths = paths.first
        val redoPaths = paths.second
        val linePath = Mockito.mock(ShapeAndPaint::class.java)
        undoPaths.add(linePath)
        undoPaths.add(linePath)
        redoPaths.add(linePath)
        drawingView.clearAll()
        assertEquals(undoPaths.size, 0)
        assertEquals(redoPaths.size, 0)
    }
}