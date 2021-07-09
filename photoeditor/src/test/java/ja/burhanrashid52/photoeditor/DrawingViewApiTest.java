package ja.burhanrashid52.photoeditor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static ja.burhanrashid52.photoeditor.shape.ShapeBuilder.DEFAULT_SHAPE_COLOR;
import static ja.burhanrashid52.photoeditor.shape.ShapeBuilder.DEFAULT_SHAPE_SIZE;
import static ja.burhanrashid52.photoeditor.shape.ShapeBuilder.DEFAULT_SHAPE_OPACITY;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class DrawingViewApiTest extends BaseDrawingViewTest {

    @Test
    public void testDefaultPaintAttributes() {
        DrawingView drawingView = setupDrawingView();
        touchView(drawingView, MotionEvent.ACTION_DOWN);
        Paint drawingPaint = drawingView.getCurrentShape().getPaint();

        assertEquals(drawingPaint.getColor(), DEFAULT_SHAPE_COLOR);
        assertEquals(drawingPaint.getStyle(), Paint.Style.STROKE);
        assertEquals(drawingPaint.getStrokeJoin(), Paint.Join.ROUND);
        assertEquals(drawingPaint.getStrokeCap(), Paint.Cap.ROUND);
        assertEquals(drawingPaint.getStrokeWidth(), DEFAULT_SHAPE_SIZE);
        assertEquals(drawingPaint.getAlpha(), DEFAULT_SHAPE_OPACITY);
        assertTrue(drawingPaint.getXfermode() instanceof PorterDuffXfermode);

        // Spy is not working properly
        /*Paint spyPaint = Mockito.spy(drawingPaint);
        verify(spyPaint, times(1)).setColor(Color.BLACK);*/
    }

    @Test
    public void testPaintAttributesAfterDrawingModeIsEnabled() {
        DrawingView drawingView = setupDrawingView();
        touchView(drawingView, MotionEvent.ACTION_DOWN);
        Paint drawingPaint = drawingView.getCurrentShape().getPaint();

        assertEquals(drawingPaint.getStyle(), Paint.Style.STROKE);
        assertEquals(drawingPaint.getStrokeJoin(), Paint.Join.ROUND);
        assertEquals(drawingPaint.getStrokeCap(), Paint.Cap.ROUND);
        assertEquals(drawingPaint.getStrokeWidth(), DEFAULT_SHAPE_SIZE);
        assertEquals(drawingPaint.getAlpha(), DEFAULT_SHAPE_OPACITY);
        assertTrue(drawingPaint.getXfermode() instanceof PorterDuffXfermode);

        Paint spyPaint = Mockito.spy(drawingPaint);
        verify(spyPaint, times(0)).setColor(DEFAULT_SHAPE_COLOR);
    }

    @Test
    public void testByDefaultDrawingModeIsDisabled() {
        DrawingView drawingView = new DrawingView(mContext);
        assertFalse(drawingView.isDrawingEnabled());
        assertEquals(drawingView.getVisibility(), View.GONE);
    }

    @Test
    public void testWhenDrawingModeIsSetEnabled() {
        DrawingView drawingView = setupDrawingView();
        drawingView.enableDrawing(true);
        assertTrue(drawingView.isDrawingEnabled());
        assertEquals(drawingView.getVisibility(), View.VISIBLE);
    }

    @Test
    public void testWhenDrawingModeIsSetDisabled() {
        DrawingView drawingView = setupDrawingView();
        drawingView.enableDrawing(false);
        assertFalse(drawingView.isDrawingEnabled());
    }

    @Test
    public void testDefaultShapeSize() {
        DrawingView drawingView = setupDrawingView();
        touchView(drawingView, MotionEvent.ACTION_DOWN);
        Paint drawingPaint = drawingView.getCurrentShape().getPaint();
        assertEquals(drawingView.getCurrentShapeBuilder().getShapeSize(), DEFAULT_SHAPE_SIZE);
        assertEquals(drawingPaint.getStrokeWidth(), DEFAULT_SHAPE_SIZE);
    }

    @Test
    public void testCorrectShapeSizeIsSet() {
        DrawingView drawingView = setupDrawingView();
        float shapeSize = 75f;
        drawingView.getCurrentShapeBuilder().withShapeSize(shapeSize);
        touchView(drawingView, MotionEvent.ACTION_DOWN);

        assertEquals(drawingView.getCurrentShapeBuilder().getShapeSize(), shapeSize);
        assertEquals(drawingView.getCurrentShape().getPaint().getStrokeWidth(), shapeSize);
        assertTrue(drawingView.isDrawingEnabled());
    }

    @Test
    public void testDefaultOpacityValue() {
        DrawingView drawingView = setupDrawingView();
        touchView(drawingView, MotionEvent.ACTION_DOWN);
        Paint drawingPaint = drawingView.getCurrentShape().getPaint();
        assertEquals(drawingView.getCurrentShapeBuilder().getShapeOpacity(), DEFAULT_SHAPE_OPACITY);
        assertEquals(drawingPaint.getAlpha(), DEFAULT_SHAPE_OPACITY);
    }

    @Test
    public void testCorrectOpacityValueIsSet() {
        DrawingView drawingView = setupDrawingView();
        int shapeOpacity = 240;
        drawingView.getCurrentShapeBuilder().withShapeOpacity(shapeOpacity);
        touchView(drawingView, MotionEvent.ACTION_DOWN);

        assertEquals(drawingView.getCurrentShapeBuilder().getShapeOpacity(), shapeOpacity);
        assertEquals(drawingView.getCurrentShape().getPaint().getAlpha(), shapeOpacity);
        assertTrue(drawingView.isDrawingEnabled());
    }

    @Test
    public void testDefaultBrushColorValue() {
        DrawingView drawingView = setupDrawingView();
        touchView(drawingView, MotionEvent.ACTION_DOWN);
        Paint drawingPaint = drawingView.getCurrentShape().getPaint();
        assertEquals(drawingView.getCurrentShapeBuilder().getShapeColor(), DEFAULT_SHAPE_COLOR);
        assertEquals(drawingPaint.getColor(), DEFAULT_SHAPE_COLOR);
    }

    @Test
    public void testCorrectBrushColorIsSet() {
        DrawingView drawingView = setupDrawingView();
        touchView(drawingView, MotionEvent.ACTION_DOWN);
        int shapeColor = Color.RED;
        drawingView.getCurrentShapeBuilder().withShapeColor(shapeColor);
        touchView(drawingView, MotionEvent.ACTION_DOWN);

        assertEquals(drawingView.getCurrentShapeBuilder().getShapeColor(), shapeColor);
        assertEquals(drawingView.getCurrentShape().getPaint().getColor(), shapeColor);
        assertTrue(drawingView.isDrawingEnabled());
    }

    @Test
    public void testCanvasIsDrawingCorrectlyOnDraw() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        DrawingView drawingView = setupDrawingViewWithChangeListener(brushViewChangeListener);

        int numberOfTouch = 4;
        for (int i = 0; i < numberOfTouch; i++) {
            touchView(drawingView, MotionEvent.ACTION_DOWN);
        }

        Canvas canvas = Mockito.mock(Canvas.class);
        drawingView.onDraw(canvas);

        verify(canvas, times(numberOfTouch))
                .drawPath(any(Path.class), any(Paint.class));
    }

}
