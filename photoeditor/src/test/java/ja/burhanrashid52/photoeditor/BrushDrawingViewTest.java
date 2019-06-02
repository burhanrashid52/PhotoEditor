package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowPath;

import java.util.Stack;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

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

    @Test
    public void testBrushDrawingChangeListenerAndPathWhenTouchIsDown() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        BrushDrawingView brushDrawingView = setupBrushForTouchEvents(brushViewChangeListener);

        MotionEvent touchEventDown = MotionEvent.obtain(200, 300, MotionEvent.ACTION_DOWN, 15.0f, 10.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventDown);
        verify(brushViewChangeListener, times(1)).onStartDrawing();

        Pair<Stack<LinePath>, Stack<LinePath>> drawingPath = brushDrawingView.getDrawingPath();
        Stack<LinePath> drawnPath = drawingPath.first;
        Stack<LinePath> redoPaths = drawingPath.second;
        assertTrue(drawnPath.empty());
        assertTrue(redoPaths.empty());
    }

    @Test
    public void testBrushDrawingChangeListenerAndPathWhenTouchIsMove() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        BrushDrawingView brushDrawingView = setupBrushForTouchEvents(brushViewChangeListener);

        MotionEvent touchEventMove = MotionEvent.obtain(200, 300, MotionEvent.ACTION_MOVE, 15.0f, 10.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventMove);
        verify(brushViewChangeListener, times(0)).onStartDrawing();
        verify(brushViewChangeListener, times(0)).onStopDrawing();
        verify(brushViewChangeListener, times(0)).onViewAdd(brushDrawingView);
        verify(brushViewChangeListener, times(0)).onViewRemoved(brushDrawingView);

        Pair<Stack<LinePath>, Stack<LinePath>> drawingPath = brushDrawingView.getDrawingPath();
        Stack<LinePath> drawnPath = drawingPath.first;
        Stack<LinePath> redoPaths = drawingPath.second;
        assertTrue(drawnPath.empty());
        assertTrue(redoPaths.empty());
    }

    @Test
    public void testBrushDrawingChangeListenerAndPathWhenTouchIsUp() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        BrushDrawingView brushDrawingView = setupBrushForTouchEvents(brushViewChangeListener);

        MotionEvent touchEventUp = MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, 150.0f, 100.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventUp);
        verify(brushViewChangeListener, times(0)).onStartDrawing();
        verify(brushViewChangeListener, times(1)).onStopDrawing();
        verify(brushViewChangeListener, times(1)).onViewAdd(brushDrawingView);
        verify(brushViewChangeListener, times(0)).onViewRemoved(brushDrawingView);

        Pair<Stack<LinePath>, Stack<LinePath>> drawingPath = brushDrawingView.getDrawingPath();
        Stack<LinePath> drawnPath = drawingPath.first;
        Stack<LinePath> redoPaths = drawingPath.second;
        assertFalse(drawnPath.empty());
        assertTrue(redoPaths.empty());

        LinePath linePath = drawnPath.pop();
        assertEquals(linePath.getDrawPaint().getColor(), brushDrawingView.getDrawingPaint().getColor());
    }

    @Test
    public void testPathDrawnOnTouchEvents() {
        BrushViewChangeListener brushViewChangeListener = Mockito.mock(BrushViewChangeListener.class);
        BrushDrawingView brushDrawingView = setupBrushForTouchEvents(brushViewChangeListener);

        float touchDownX = 150.0f;
        float touchDownY = 100.0f;
        MotionEvent touchEventDown = MotionEvent.obtain(200, 300, MotionEvent.ACTION_DOWN, touchDownX, touchDownY, 0);
        brushDrawingView.dispatchTouchEvent(touchEventDown);

        float touchMoveX = 160.0f;
        float touchMoveY = 110.0f;
        MotionEvent touchEventMove = MotionEvent.obtain(200, 300, MotionEvent.ACTION_MOVE, touchMoveX, touchMoveY, 0);
        brushDrawingView.dispatchTouchEvent(touchEventMove);

        MotionEvent touchEventUp = MotionEvent.obtain(200, 300, MotionEvent.ACTION_UP, 150.0f, 100.0f, 0);
        brushDrawingView.dispatchTouchEvent(touchEventUp);

        Pair<Stack<LinePath>, Stack<LinePath>> drawingPath = brushDrawingView.getDrawingPath();
        Stack<LinePath> drawnPath = drawingPath.first;

        LinePath linePath = drawnPath.pop();
        ShadowPath shadowPath = shadowOf(linePath.getDrawPath());

        ShadowPath.Point pointOne = shadowPath.getPoints().get(0);
        assertEquals(pointOne.getType(), ShadowPath.Point.Type.MOVE_TO);
        assertEquals(pointOne.getX(), touchDownX);
        assertEquals(pointOne.getY(), touchDownY);

        ShadowPath.Point pointTwo = shadowPath.getPoints().get(1);
        assertEquals(pointTwo.getType(), ShadowPath.Point.Type.LINE_TO);
        assertEquals(pointTwo.getX(), touchMoveX);
        assertEquals(pointTwo.getY(), touchMoveY);

    }


    @NonNull
    private BrushDrawingView setupBrushForTouchEvents(BrushViewChangeListener brushViewChangeListener) {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        brushDrawingView.setBrushDrawingMode(true);
        brushDrawingView.setBrushViewChangeListener(brushViewChangeListener);
        brushDrawingView.onSizeChanged(500, 500, 500, 500);
        return brushDrawingView;
    }

   /*
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
    public void undo() {
    }

    @Test
    public void redo() {
    }*/
}