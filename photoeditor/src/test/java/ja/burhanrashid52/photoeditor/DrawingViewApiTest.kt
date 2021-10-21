package ja.burhanrashid52.photoeditor

import android.graphics.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import junit.framework.TestCase
import android.view.MotionEvent
import android.view.View
import org.mockito.Mockito
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import junit.framework.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers

@RunWith(RobolectricTestRunner::class)
class DrawingViewApiTest : BaseDrawingViewTest() {
    @Test
    fun testDefaultPaintAttributes() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        val drawingPaint = drawingView.currentShape.paint
        Assert.assertEquals(drawingPaint.color, ShapeBuilder.DEFAULT_SHAPE_COLOR)
        Assert.assertEquals(drawingPaint.style, Paint.Style.STROKE)
        Assert.assertEquals(drawingPaint.strokeJoin, Paint.Join.ROUND)
        Assert.assertEquals(drawingPaint.strokeCap, Paint.Cap.ROUND)
        Assert.assertEquals(drawingPaint.strokeWidth, ShapeBuilder.DEFAULT_SHAPE_SIZE)
        Assert.assertEquals(drawingPaint.alpha, ShapeBuilder.DEFAULT_SHAPE_OPACITY)
        TestCase.assertTrue(drawingPaint.xfermode is PorterDuffXfermode)

        // Spy is not working properly
        /*Paint spyPaint = Mockito.spy(drawingPaint);
        verify(spyPaint, times(1)).setColor(Color.BLACK);*/
    }

    @Test
    fun testPaintAttributesAfterDrawingModeIsEnabled() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        val drawingPaint = drawingView.currentShape.paint
        Assert.assertEquals(drawingPaint.style, Paint.Style.STROKE)
        Assert.assertEquals(drawingPaint.strokeJoin, Paint.Join.ROUND)
        Assert.assertEquals(drawingPaint.strokeCap, Paint.Cap.ROUND)
        Assert.assertEquals(drawingPaint.strokeWidth, ShapeBuilder.DEFAULT_SHAPE_SIZE)
        Assert.assertEquals(drawingPaint.alpha, ShapeBuilder.DEFAULT_SHAPE_OPACITY)
        TestCase.assertTrue(drawingPaint.xfermode is PorterDuffXfermode)
        val spyPaint = Mockito.spy(drawingPaint)
        Mockito.verify(spyPaint, Mockito.times(0)).color =
            ShapeBuilder.DEFAULT_SHAPE_COLOR
    }

    @Test
    fun testByDefaultDrawingModeIsDisabled() {
        val drawingView = DrawingView(mContext)
        Assert.assertFalse(drawingView.isDrawingEnabled)
        Assert.assertEquals(drawingView.visibility, View.GONE)
    }

    @Test
    fun testWhenDrawingModeIsSetEnabled() {
        val drawingView = setupDrawingView()
        drawingView.enableDrawing(true)
        TestCase.assertTrue(drawingView.isDrawingEnabled)
        Assert.assertEquals(drawingView.visibility, View.VISIBLE)
    }

    @Test
    fun testWhenDrawingModeIsSetDisabled() {
        val drawingView = setupDrawingView()
        drawingView.enableDrawing(false)
        Assert.assertFalse(drawingView.isDrawingEnabled)
    }

    @Test
    fun testDefaultShapeSize() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        val drawingPaint = drawingView.currentShape.paint
        Assert.assertEquals(
            drawingView.currentShapeBuilder.shapeSize,
            ShapeBuilder.DEFAULT_SHAPE_SIZE
        )
        Assert.assertEquals(drawingPaint.strokeWidth, ShapeBuilder.DEFAULT_SHAPE_SIZE)
    }

    @Test
    fun testCorrectShapeSizeIsSet() {
        val drawingView = setupDrawingView()
        val shapeSize = 75f
        drawingView.currentShapeBuilder.withShapeSize(shapeSize)
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        Assert.assertEquals(drawingView.currentShapeBuilder.shapeSize, shapeSize)
        Assert.assertEquals(drawingView.currentShape.paint.strokeWidth, shapeSize)
        TestCase.assertTrue(drawingView.isDrawingEnabled)
    }

    @Test
    fun testDefaultOpacityValue() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        val drawingPaint = drawingView.currentShape.paint
        Assert.assertEquals(
            drawingView.currentShapeBuilder.shapeOpacity,
            ShapeBuilder.DEFAULT_SHAPE_OPACITY
        )
        Assert.assertEquals(drawingPaint.alpha, ShapeBuilder.DEFAULT_SHAPE_OPACITY)
    }

    @Test
    fun testCorrectOpacityValueIsSet() {
        val drawingView = setupDrawingView()
        val shapeOpacity = 240
        drawingView.currentShapeBuilder.withShapeOpacity(shapeOpacity)
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        Assert.assertEquals(drawingView.currentShapeBuilder.shapeOpacity, shapeOpacity)
        Assert.assertEquals(drawingView.currentShape.paint.alpha, shapeOpacity)
        TestCase.assertTrue(drawingView.isDrawingEnabled)
    }

    @Test
    fun testDefaultBrushColorValue() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        val drawingPaint = drawingView.currentShape.paint
        Assert.assertEquals(
            drawingView.currentShapeBuilder.shapeColor,
            ShapeBuilder.DEFAULT_SHAPE_COLOR
        )
        Assert.assertEquals(drawingPaint.color, ShapeBuilder.DEFAULT_SHAPE_COLOR)
    }

    @Test
    fun testCorrectBrushColorIsSet() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        val shapeColor = Color.RED
        drawingView.currentShapeBuilder.withShapeColor(shapeColor)
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        Assert.assertEquals(drawingView.currentShapeBuilder.shapeColor, shapeColor)
        Assert.assertEquals(drawingView.currentShape.paint.color, shapeColor)
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