package ja.burhanrashid52.photoeditor

import android.graphics.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import junit.framework.TestCase
import android.view.MotionEvent
import android.view.View
import org.mockito.Mockito
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import org.junit.Test
import org.mockito.ArgumentMatchers
import java.lang.AssertionError

@RunWith(RobolectricTestRunner::class)
internal class DrawingViewApiTest : BaseDrawingViewTest() {
    @Test
    fun testDefaultPaintAttributes() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        drawingView.currentShape?.paint?.apply {
            assertEquals(color, ShapeBuilder.DEFAULT_SHAPE_COLOR)
            assertEquals(style, Paint.Style.STROKE)
            assertEquals(strokeJoin, Paint.Join.ROUND)
            assertEquals(strokeCap, Paint.Cap.ROUND)
            assertEquals(strokeWidth, ShapeBuilder.DEFAULT_SHAPE_SIZE)
            assertEquals(alpha, ShapeBuilder.DEFAULT_SHAPE_OPACITY)
            TestCase.assertTrue(xfermode is PorterDuffXfermode)
        } ?: AssertionError("The paint is null")


        // Spy is not working properly
        /*Paint spyPaint = Mockito.spy(drawingPaint);
        verify(spyPaint, times(1)).setColor(Color.BLACK);*/
    }

    @Test
    fun testPaintAttributesAfterDrawingModeIsEnabled() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        drawingView.currentShape?.paint?.apply {
            assertEquals(style, Paint.Style.STROKE)
            assertEquals(strokeJoin, Paint.Join.ROUND)
            assertEquals(strokeCap, Paint.Cap.ROUND)
            assertEquals(strokeWidth, ShapeBuilder.DEFAULT_SHAPE_SIZE)
            assertEquals(alpha, ShapeBuilder.DEFAULT_SHAPE_OPACITY)
            TestCase.assertTrue(xfermode is PorterDuffXfermode)
            val spyPaint = Mockito.spy(this)
            Mockito.verify(spyPaint, Mockito.times(0)).color =
                ShapeBuilder.DEFAULT_SHAPE_COLOR
        } ?: AssertionError("The paint is null")
    }

    @Test
    fun testByDefaultDrawingModeIsDisabled() {
        val drawingView = DrawingView(mContext)
        assertFalse(drawingView.isDrawingEnabled)
        assertEquals(drawingView.visibility, View.GONE)
    }

    @Test
    fun testWhenDrawingModeIsSetEnabled() {
        val drawingView = setupDrawingView()
        drawingView.enableDrawing(true)
        TestCase.assertTrue(drawingView.isDrawingEnabled)
        assertEquals(drawingView.visibility, View.VISIBLE)
    }

    @Test
    fun testWhenDrawingModeIsSetDisabled() {
        val drawingView = setupDrawingView()
        drawingView.enableDrawing(false)
        assertFalse(drawingView.isDrawingEnabled)
    }

    @Test
    fun testDefaultShapeSize() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)


        drawingView.currentShape?.paint?.apply {
            assertEquals(
                drawingView.currentShapeBuilder?.shapeSize,
                ShapeBuilder.DEFAULT_SHAPE_SIZE
            )
            assertEquals(strokeWidth, ShapeBuilder.DEFAULT_SHAPE_SIZE)
        } ?: AssertionError("The paint is null")
    }

    @Test
    fun testCorrectShapeSizeIsSet() {
        val drawingView = setupDrawingView()
        val shapeSize = 75f
        drawingView.currentShapeBuilder?.withShapeSize(shapeSize)
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        assertEquals(drawingView.currentShapeBuilder?.shapeSize, shapeSize)
        assertEquals(drawingView.currentShape?.paint?.strokeWidth, shapeSize)
        TestCase.assertTrue(drawingView.isDrawingEnabled)
    }

    @Test
    fun testDefaultOpacityValue() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)

        drawingView.currentShape?.paint?.apply {
            assertEquals(
                drawingView.currentShapeBuilder?.shapeOpacity,
                ShapeBuilder.DEFAULT_SHAPE_OPACITY
            )
            assertEquals(alpha, ShapeBuilder.DEFAULT_SHAPE_OPACITY)
        } ?: AssertionError("The paint is null")
    }

    @Test
    fun testCorrectOpacityValueIsSet() {
        val drawingView = setupDrawingView()
        val shapeOpacity = 240
        drawingView.currentShapeBuilder?.withShapeOpacity(shapeOpacity)
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        assertEquals(drawingView.currentShapeBuilder?.shapeOpacity, shapeOpacity)
        assertEquals(drawingView.currentShape?.paint?.alpha, shapeOpacity)
        TestCase.assertTrue(drawingView.isDrawingEnabled)
    }

    @Test
    fun testDefaultBrushColorValue() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        drawingView.currentShape?.paint?.apply {
            assertEquals(
                drawingView.currentShapeBuilder?.shapeColor,
                ShapeBuilder.DEFAULT_SHAPE_COLOR
            )
            assertEquals(color, ShapeBuilder.DEFAULT_SHAPE_COLOR)
        } ?: AssertionError("The paint is null")

    }

    @Test
    fun testCorrectBrushColorIsSet() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        val shapeColor = Color.RED
        drawingView.currentShapeBuilder?.withShapeColor(shapeColor)
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        assertEquals(drawingView.currentShapeBuilder?.shapeColor, shapeColor)
        assertEquals(drawingView.currentShape?.paint?.color, shapeColor)
        TestCase.assertTrue(drawingView.isDrawingEnabled)
    }

    @Test
    fun testCanvasIsDrawingCorrectlyOnDraw() {
        val brushViewChangeListener = Mockito.mock(
            BrushViewChangeListener::class.java
        )
        val drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener)
        val numberOfTouch = 4
        for (i in 0 until numberOfTouch) {
            touchView(drawingView, MotionEvent.ACTION_DOWN)
        }
        val canvas = Mockito.mock(Canvas::class.java)
        drawingView.onDraw(canvas)
        Mockito.verify(canvas, Mockito.times(numberOfTouch))
            .drawPath(
                ArgumentMatchers.any(Path::class.java), ArgumentMatchers.any(
                    Paint::class.java
                )
            )
    }
}