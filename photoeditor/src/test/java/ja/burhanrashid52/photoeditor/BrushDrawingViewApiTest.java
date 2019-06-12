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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class BrushDrawingViewApiTest extends BaseBrushDrawingViewTest {

    @Test
    public void testDefaultPaintAttributes() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        Paint drawingPaint = brushDrawingView.getDrawingPaint();

        assertEquals(drawingPaint.getColor(), Color.BLACK);
        assertEquals(drawingPaint.getStyle(), Paint.Style.STROKE);
        assertEquals(drawingPaint.getStrokeJoin(), Paint.Join.ROUND);
        assertEquals(drawingPaint.getStrokeCap(), Paint.Cap.ROUND);
        assertEquals(drawingPaint.getStrokeWidth(), BrushDrawingView.DEFAULT_BRUSH_SIZE);
        assertEquals(drawingPaint.getAlpha(), BrushDrawingView.DEFAULT_OPACITY);
        assertTrue(drawingPaint.getXfermode() instanceof PorterDuffXfermode);

        // Spy is not working properly
        /*Paint spyPaint = Mockito.spy(drawingPaint);
        verify(spyPaint, times(1)).setColor(Color.BLACK);*/
    }

    @Test
    public void testPaintAttributesAfterBrushModeIsEnabled() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        brushDrawingView.setBrushDrawingMode(true);
        Paint drawingPaint = brushDrawingView.getDrawingPaint();

        assertEquals(drawingPaint.getStyle(), Paint.Style.STROKE);
        assertEquals(drawingPaint.getStrokeJoin(), Paint.Join.ROUND);
        assertEquals(drawingPaint.getStrokeCap(), Paint.Cap.ROUND);
        assertEquals(drawingPaint.getStrokeWidth(), BrushDrawingView.DEFAULT_BRUSH_SIZE);
        assertEquals(drawingPaint.getAlpha(), BrushDrawingView.DEFAULT_OPACITY);
        assertTrue(drawingPaint.getXfermode() instanceof PorterDuffXfermode);

        Paint spyPaint = Mockito.spy(drawingPaint);
        verify(spyPaint, times(0)).setColor(Color.BLACK);
    }

    @Test
    public void testByDefaultBrushDrawingModeIsDisabled() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        assertFalse(brushDrawingView.getBrushDrawingMode());
        assertEquals(brushDrawingView.getVisibility(), View.GONE);
    }

    @Test
    public void testWhenBrushDrawingModeIsSetEnabled() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        brushDrawingView.setBrushDrawingMode(true);
        assertTrue(brushDrawingView.getBrushDrawingMode());
        assertEquals(brushDrawingView.getVisibility(), View.VISIBLE);
    }


    @Test
    public void testWhenBrushEraserIsEnabled() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        Paint drawingPaint = brushDrawingView.getDrawingPaint();
        brushDrawingView.brushEraser();


        assertTrue(brushDrawingView.getBrushDrawingMode());
        assertEquals(brushDrawingView.getEraserSize(), BrushDrawingView.DEFAULT_ERASER_SIZE);
        assertEquals(drawingPaint.getStrokeWidth(), BrushDrawingView.DEFAULT_ERASER_SIZE);
        assertTrue(drawingPaint.getXfermode() instanceof PorterDuffXfermode);
    }


    @Test
    public void testDefaultBrushSize() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        Paint drawingPaint = brushDrawingView.getDrawingPaint();
        assertEquals(brushDrawingView.getBrushSize(), BrushDrawingView.DEFAULT_BRUSH_SIZE);
        assertEquals(drawingPaint.getStrokeWidth(), BrushDrawingView.DEFAULT_BRUSH_SIZE);
    }

    @Test
    public void testCorrectBrushSizeIsSetPaintAndDrawingModeIsEnabled() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        float brushSize = 75f;
        brushDrawingView.setBrushSize(brushSize);
        Paint drawingPaint = brushDrawingView.getDrawingPaint();

        assertEquals(brushDrawingView.getBrushSize(), brushSize);
        assertEquals(drawingPaint.getStrokeWidth(), brushSize);
        assertTrue(brushDrawingView.getBrushDrawingMode());
    }

    @Test
    public void testCorrectEraserSizeIsSetPaintAndDrawingModeIsEnabled() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        float eraserSize = 105f;
        brushDrawingView.setBrushEraserSize(eraserSize);
        brushDrawingView.brushEraser();
        Paint drawingPaint = brushDrawingView.getDrawingPaint();

        assertEquals(brushDrawingView.getEraserSize(), eraserSize);
        assertEquals(drawingPaint.getStrokeWidth(), eraserSize);
        assertTrue(brushDrawingView.getBrushDrawingMode());
    }

    @Test
    public void testDefaultOpacityValue() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        Paint drawingPaint = brushDrawingView.getDrawingPaint();
        assertEquals(brushDrawingView.getOpacity(), BrushDrawingView.DEFAULT_OPACITY);
        assertEquals(drawingPaint.getAlpha(), BrushDrawingView.DEFAULT_OPACITY);
    }

    @Test
    public void testCorrectOpacityValueIsSet() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        int opacityValue = 240;
        brushDrawingView.setOpacity(opacityValue);
        Paint drawingPaint = brushDrawingView.getDrawingPaint();

        assertEquals(brushDrawingView.getOpacity(), opacityValue);
        assertEquals(drawingPaint.getAlpha(), opacityValue);
        assertTrue(brushDrawingView.getBrushDrawingMode());
    }

    @Test
    public void testDefaultBrushColorValue() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        Paint drawingPaint = brushDrawingView.getDrawingPaint();
        assertEquals(drawingPaint.getColor(), Color.BLACK);
    }

    @Test
    public void testCorrectBrushColorIsSet() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        brushDrawingView.setBrushColor(Color.RED);
        Paint drawingPaint = brushDrawingView.getDrawingPaint();

        assertEquals(drawingPaint.getColor(), Color.RED);
        assertEquals(brushDrawingView.getBrushColor(), Color.RED);
    }


    @Test
    public void testCorrectBrushEraserColorIsSet() {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        brushDrawingView.setBrushEraserColor(Color.RED);
        Paint drawingPaint = brushDrawingView.getDrawingPaint();

        assertEquals(drawingPaint.getColor(), Color.RED);
        assertEquals(brushDrawingView.getBrushColor(), Color.RED);
    }


    @Test
    public void testCanvasIsDrawingCorrectlyOnDraw() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        BrushDrawingView brushDrawingView = setupBrushForTouchEvents(brushViewChangeListener);
        MotionEvent touchEventOne = MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, 150.0f, 100.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventOne);
        MotionEvent touchEventTwo = MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, 150.0f, 100.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventTwo);
        MotionEvent touchEventThree = MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, 150.0f, 100.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventThree);
        Canvas canvas = Mockito.mock(Canvas.class);
        brushDrawingView.onDraw(canvas);
        verify(canvas, times(4)).drawPath(any(Path.class), any(Paint.class));
    }
}