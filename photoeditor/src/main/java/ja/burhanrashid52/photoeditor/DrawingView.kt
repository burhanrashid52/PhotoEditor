package ja.burhanrashid52.photoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.util.Pair
import android.view.MotionEvent
import android.view.View
import ja.burhanrashid52.photoeditor.shape.*
import java.util.*

/**
 *
 *
 * This is custom drawing view used to do painting on user touch events it it will paint on canvas
 * as per attributes provided to the paint
 *
 *
 * @author [Burhanuddin Rashid](https://github.com/burhanrashid52)
 * @version 0.1.1
 * @since 12/1/18
 */
class DrawingView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    private val drawShapes = Stack<ShapeAndPaint?>()
    private val redoShapes = Stack<ShapeAndPaint?>()
    internal var currentShape: ShapeAndPaint? = null
    var isDrawingEnabled = false
        private set
    private var viewChangeListener: BrushViewChangeListener? = null
    var currentShapeBuilder: ShapeBuilder? = null

    // eraser parameters
    private var isErasing = false
    var eraserSize = DEFAULT_ERASER_SIZE

    // endregion
    private fun createPaint(): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.isDither = true
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)

        // apply shape builder parameters
        currentShapeBuilder?.apply {
            paint.strokeWidth = this.shapeSize
            paint.alpha = this.shapeOpacity
            paint.color = this.shapeColor
        }

        return paint
    }

    private fun createEraserPaint(): Paint {
        val paint = createPaint()
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        return paint
    }

    private fun setupBrushDrawing() {
        //Caution: This line is to disable hardware acceleration to make eraser feature work properly
        setLayerType(LAYER_TYPE_HARDWARE, null)
        visibility = GONE
        currentShapeBuilder = ShapeBuilder()
    }

    fun clearAll() {
        drawShapes.clear()
        redoShapes.clear()
        invalidate()
    }

    fun setBrushViewChangeListener(brushViewChangeListener: BrushViewChangeListener?) {
        viewChangeListener = brushViewChangeListener
    }

    public override fun onDraw(canvas: Canvas) {
        for (shape in drawShapes) {
            shape?.shape?.draw(canvas, shape.paint)
        }
    }

    /**
     * Handle touch event to draw paint on canvas i.e brush drawing
     *
     * @param event points having touch info
     * @return true if handling touch events
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isDrawingEnabled) {
            val touchX = event.x
            val touchY = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> onTouchEventDown(touchX, touchY)
                MotionEvent.ACTION_MOVE -> onTouchEventMove(touchX, touchY)
                MotionEvent.ACTION_UP -> onTouchEventUp(touchX, touchY)
            }
            invalidate()
            true
        } else {
            false
        }
    }

    private fun onTouchEventDown(touchX: Float, touchY: Float) {
        createShape()
        currentShape?.shape?.startShape(touchX, touchY)
    }

    private fun onTouchEventMove(touchX: Float, touchY: Float) {
        currentShape?.shape?.moveShape(touchX, touchY)
    }

    private fun onTouchEventUp(touchX: Float, touchY: Float) {
        currentShape?.apply {
            shape.stopShape()
            endShape(touchX, touchY)
        }
    }

    private fun createShape() {
        var paint = createPaint()
        var shape: AbstractShape = BrushShape()

        if (isErasing) {
            paint = createEraserPaint()
        } else {
            when(currentShapeBuilder?.shapeType){
                ShapeType.OVAL -> {
                    shape = OvalShape()
                }
                ShapeType.BRUSH -> {
                    shape = BrushShape()
                }
                ShapeType.RECTANGLE -> {
                    shape = RectangleShape()
                }
                ShapeType.LINE -> {
                    shape = LineShape()
                }
            }
        }

        currentShape = ShapeAndPaint(shape, paint)
        drawShapes.push(currentShape)
        viewChangeListener?.onStartDrawing()
    }

    private fun endShape(touchX: Float, touchY: Float) {
        if (currentShape?.shape?.hasBeenTapped() == true) {
            // just a tap, this is not a shape, so remove it
            drawShapes.remove(currentShape)
            //handleTap(touchX, touchY);
        }
        viewChangeListener?.apply {
            onStopDrawing()
            onViewAdd(this@DrawingView)
        }
    }

    fun undo(): Boolean {
        if (!drawShapes.empty()) {
            redoShapes.push(drawShapes.pop())
            invalidate()
        }
        viewChangeListener?.onViewRemoved(this)
        return !drawShapes.empty()
    }

    fun redo(): Boolean {
        if (!redoShapes.empty()) {
            drawShapes.push(redoShapes.pop())
            invalidate()
        }
        viewChangeListener?.onViewAdd(this)
        return !redoShapes.empty()
    }

    // region eraser
    fun brushEraser() {
        isDrawingEnabled = true
        isErasing = true
    }

    // endregion
    // region Setters/Getters

    fun enableDrawing(brushDrawMode: Boolean) {
        isDrawingEnabled = brushDrawMode
        isErasing = !brushDrawMode
        if (brushDrawMode) {
            visibility = VISIBLE
        }
    }

    // endregion
    val drawingPath: Pair<Stack<ShapeAndPaint?>, Stack<ShapeAndPaint?>>
        get() = Pair(drawShapes, redoShapes)

    companion object {
        const val DEFAULT_ERASER_SIZE = 50.0f
    }

    // region constructors
    init {
        setupBrushDrawing()
    }
}