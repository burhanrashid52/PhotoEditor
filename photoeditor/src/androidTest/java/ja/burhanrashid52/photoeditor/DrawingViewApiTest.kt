package ja.burhanrashid52.photoeditor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuffXfermode
import android.view.MotionEvent
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
internal class DrawingViewApiTest : BaseDrawingViewTest() {
    @Test
    fun testDefaultPaintAttributes() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        val paint = drawingView.currentShape!!.paint

        assertEquals(paint.color, ShapeBuilder.DEFAULT_SHAPE_COLOR)
        assertEquals(paint.style, Paint.Style.STROKE)
        assertEquals(paint.strokeJoin, Paint.Join.ROUND)
        assertEquals(paint.strokeCap, Paint.Cap.ROUND)
        assertEquals(paint.strokeWidth, ShapeBuilder.DEFAULT_SHAPE_SIZE)
        assertEquals(paint.alpha, ShapeBuilder.DEFAULT_SHAPE_OPACITY ?: DEFAULT_COLOR_ALPHA)
        assertTrue(paint.xfermode is PorterDuffXfermode)

        // Spy is not working properly
        /*Paint spyPaint = Mockito.spy(drawingPaint);
        verify(spyPaint, times(1)).setColor(Color.BLACK);*/
    }

    @Test
    fun testPaintAttributesAfterDrawingModeIsEnabled() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        val paint = drawingView.currentShape!!.paint

        assertEquals(paint.style, Paint.Style.STROKE)
        assertEquals(paint.strokeJoin, Paint.Join.ROUND)
        assertEquals(paint.strokeCap, Paint.Cap.ROUND)
        assertEquals(paint.strokeWidth, ShapeBuilder.DEFAULT_SHAPE_SIZE)
        assertEquals(paint.alpha, ShapeBuilder.DEFAULT_SHAPE_OPACITY ?: DEFAULT_COLOR_ALPHA)
        assertTrue(paint.xfermode is PorterDuffXfermode)

        val spyPaint = Mockito.spy(paint)
        Mockito.verify(spyPaint, Mockito.times(0)).color = ShapeBuilder.DEFAULT_SHAPE_COLOR
    }

    @Test
    fun testByDefaultDrawingModeIsDisabled() {
        val drawingView = DrawingView(context)
        assertFalse(drawingView.isDrawingEnabled)
        assertEquals(drawingView.visibility, View.GONE)
    }

    @Test
    fun testWhenDrawingModeIsSetEnabled() {
        val drawingView = setupDrawingView()
        drawingView.enableDrawing(true)
        assertTrue(drawingView.isDrawingEnabled)
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
        val paint = drawingView.currentShape!!.paint
        assertEquals(drawingView.currentShapeBuilder.shapeSize, ShapeBuilder.DEFAULT_SHAPE_SIZE)
        assertEquals(paint.strokeWidth, ShapeBuilder.DEFAULT_SHAPE_SIZE)
    }

    @Test
    fun testCorrectShapeSizeIsSet() {
        val drawingView = setupDrawingView()
        val shapeSize = 75f
        drawingView.currentShapeBuilder.withShapeSize(shapeSize)
        touchView(drawingView, MotionEvent.ACTION_DOWN)

        assertEquals(drawingView.currentShapeBuilder.shapeSize, shapeSize)
        assertEquals(drawingView.currentShape!!.paint.strokeWidth, shapeSize)
        assertTrue(drawingView.isDrawingEnabled)
    }

    @Test
    fun testDefaultOpacityValue() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        val paint = drawingView.currentShape!!.paint
        val currentShapeBuilder = drawingView.currentShapeBuilder

        assertEquals(currentShapeBuilder.shapeOpacity, ShapeBuilder.DEFAULT_SHAPE_OPACITY)
        assertEquals(paint.alpha, ShapeBuilder.DEFAULT_SHAPE_OPACITY ?: DEFAULT_COLOR_ALPHA)
    }

    @Test
    fun testCorrectOpacityValueIsSet() {
        val drawingView = setupDrawingView()
        val shapeOpacity = 240
        drawingView.currentShapeBuilder.withShapeOpacity(shapeOpacity)
        touchView(drawingView, MotionEvent.ACTION_DOWN)

        assertEquals(drawingView.currentShapeBuilder.shapeOpacity, shapeOpacity)
        assertEquals(drawingView.currentShape!!.paint.alpha, shapeOpacity)
        assertTrue(drawingView.isDrawingEnabled)
    }

    @Test
    fun testCorrectAlphaAndColorValues() {
        val drawingView = setupDrawingView()
        val currentShapeBuilder = drawingView.currentShapeBuilder

        val shapeOpacity = 120
        val colorAlpha1 = 11
        val colorAlpha2 = 22

        currentShapeBuilder.withShapeColor(Color.argb(colorAlpha1, 33, 44, 55))
        touchView(drawingView, MotionEvent.ACTION_DOWN)

        drawingView.currentShape!!.paint.also { paint ->
            assertEquals(colorAlpha1, paint.alpha)
            assertEquals(Color.argb(colorAlpha1, 33, 44, 55), paint.color)
        }

        currentShapeBuilder.withShapeOpacity(shapeOpacity)
        touchView(drawingView, MotionEvent.ACTION_DOWN)

        drawingView.currentShape!!.paint.also { paint ->
            assertEquals(shapeOpacity, paint.alpha)
            assertEquals(Color.argb(shapeOpacity, 33, 44, 55), paint.color)
        }

        currentShapeBuilder.withShapeColor(Color.argb(colorAlpha2, 44, 55, 66))
        touchView(drawingView, MotionEvent.ACTION_DOWN)

        drawingView.currentShape!!.paint.also { paint ->
            assertEquals(shapeOpacity, paint.alpha)
            assertEquals(Color.argb(shapeOpacity, 44, 55, 66), paint.color)
        }

        currentShapeBuilder.withShapeOpacity(null)
        touchView(drawingView, MotionEvent.ACTION_DOWN)

        drawingView.currentShape!!.paint.also { paint ->
            assertEquals(colorAlpha2, paint.alpha)
            assertEquals(Color.argb(colorAlpha2, 44, 55, 66), paint.color)
        }
    }

    @Test
    fun testDefaultBrushColorValue() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        val paint = drawingView.currentShape!!.paint

        assertEquals(drawingView.currentShapeBuilder.shapeColor, ShapeBuilder.DEFAULT_SHAPE_COLOR)
        assertEquals(paint.color, ShapeBuilder.DEFAULT_SHAPE_COLOR)
    }

    @Test
    fun testCorrectBrushColorIsSet() {
        val drawingView = setupDrawingView()
        touchView(drawingView, MotionEvent.ACTION_DOWN)
        val shapeColor = Color.RED
        drawingView.currentShapeBuilder.withShapeColor(shapeColor)
        touchView(drawingView, MotionEvent.ACTION_DOWN)

        assertEquals(drawingView.currentShapeBuilder.shapeColor, shapeColor)
        assertEquals(drawingView.currentShape!!.paint.color, shapeColor)
        assertTrue(drawingView.isDrawingEnabled)
    }

    @Test
    fun testCanvasIsDrawingCorrectlyOnDraw() {
        val brushViewChangeListener = Mockito.mock(BrushViewChangeListener::class.java)
        val drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener)

        val numberOfTouch = 4
        for (i in 0 until numberOfTouch) {
            touchView(drawingView, MotionEvent.ACTION_DOWN)
        }

        val canvas = Mockito.mock(Canvas::class.java)
        drawingView.onDraw(canvas)

        Mockito.verify(canvas, Mockito.times(numberOfTouch))
            .drawPath(any(Path::class.java), any(Paint::class.java))
    }

    private companion object {
        val DEFAULT_COLOR_ALPHA = Color.alpha(ShapeBuilder.DEFAULT_SHAPE_COLOR)
    }

}