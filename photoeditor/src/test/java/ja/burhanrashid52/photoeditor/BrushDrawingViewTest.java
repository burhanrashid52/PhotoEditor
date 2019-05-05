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

/*
    @Test
    public void brushEraser() {
    }

    @Test
    public void setOpacity() {
    }

    @Test
    public void getBrushDrawingMode() {
    }

    @Test
    public void setBrushSize() {
    }

    @Test
    public void setBrushColor() {
    }

    @Test
    public void setBrushEraserSize() {
    }

    @Test
    public void setBrushEraserColor() {
    }

    @Test
    public void getEraserSize() {
    }

    @Test
    public void getBrushSize() {
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