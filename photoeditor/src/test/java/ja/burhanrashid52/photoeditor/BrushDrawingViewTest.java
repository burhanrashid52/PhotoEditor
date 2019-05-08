package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class BrushDrawingViewTest {

    private Context mContext = RuntimeEnvironment.systemContext;

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

   /* @Test

    @Test
    public void setBrushColor() {
    }

    @Test
    public void setBrushEraserColor() {
    }

    @Test
    public void getBrushColor() {
    }

    @Test
    public void clearAll() {
    }

    @Test
    public void setBrushViewChangeListener() {
    }

    @Test
    public void onSizeChanged() {
    }

    @Test
    public void onDraw() {
    }

    @Test
    public void onTouchEvent() {
    }

    @Test
    public void undo() {
    }

    @Test
    public void redo() {
    }*/
}