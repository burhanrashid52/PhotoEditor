package ja.burhanrashid52.photoeditor

import ja.burhanrashid52.photoeditor.shape.ShapeBuilder.shapeSize
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder.shapeOpacity
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder.shapeColor
import ja.burhanrashid52.photoeditor.shape.ShapeAndPaint.shape
import ja.burhanrashid52.photoeditor.shape.AbstractShape.draw
import ja.burhanrashid52.photoeditor.shape.ShapeAndPaint.paint
import ja.burhanrashid52.photoeditor.shape.Shape.startShape
import ja.burhanrashid52.photoeditor.shape.Shape.moveShape
import ja.burhanrashid52.photoeditor.shape.Shape.stopShape
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder.shapeType
import ja.burhanrashid52.photoeditor.BrushViewChangeListener.onStartDrawing
import ja.burhanrashid52.photoeditor.shape.AbstractShape.hasBeenTapped
import ja.burhanrashid52.photoeditor.BrushViewChangeListener.onStopDrawing
import ja.burhanrashid52.photoeditor.BrushViewChangeListener.onViewAdd
import ja.burhanrashid52.photoeditor.BrushViewChangeListener.onViewRemoved
import kotlin.jvm.JvmOverloads
import ja.burhanrashid52.photoeditor.BrushViewChangeListener
import ja.burhanrashid52.photoeditor.DrawingView
import android.graphics.PorterDuffXfermode
import android.graphics.PorterDuff
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
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

    @get:VisibleForTesting
    var currentShape: ShapeAndPaint? = null
        private set

    @get:VisibleForTesting
    var currentShapeBuilder: ShapeBuilder? = null
        private set
    var isDrawingEnabled = false
        private set
    private var viewChangeListener: BrushViewChangeListener? = null

    // eraser parameters
    private var isErasing = false
    var eraserSize = DEFAULT_ERASER_SIZE
        private set

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
        paint.strokeWidth = currentShapeBuilder!!.shapeSize
        paint.alpha = currentShapeBuilder!!.shapeOpacity
        paint.color = currentShapeBuilder!!.shapeColor
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

    override fun onDraw(canvas: Canvas) {
        for (shape in drawShapes) {
            shape!!.shape.draw(canvas, shape.paint)
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
        if (currentShape != null && currentShape!!.shape != null) {
            currentShape!!.shape.startShape(touchX, touchY)
        }
    }

    private fun onTouchEventMove(touchX: Float, touchY: Float) {
        if (currentShape != null && currentShape!!.shape != null) {
            currentShape!!.shape.moveShape(touchX, touchY)
        }
    }

    private fun onTouchEventUp(touchX: Float, touchY: Float) {
        if (currentShape != null && currentShape!!.shape != null) {
            currentShape!!.shape.stopShape()
            endShape(touchX, touchY)
        }
    }

    private fun createShape() {
        val shape: AbstractShape
        var paint = createPaint()
        if (isErasing) {
            shape = BrushShape()
            paint = createEraserPaint()
        } else if (currentShapeBuilder!!.shapeType === ShapeType.OVAL) {
            shape = OvalShape()
        } else if (currentShapeBuilder!!.shapeType === ShapeType.RECTANGLE) {
            shape = RectangleShape()
        } else if (currentShapeBuilder!!.shapeType === ShapeType.LINE) {
            shape = LineShape()
        } else {
            shape = BrushShape()
        }
        currentShape = ShapeAndPaint(shape, paint)
        drawShapes.push(currentShape)
        if (viewChangeListener != null) {
            viewChangeListener!!.onStartDrawing()
        }
    }

    private fun endShape(touchX: Float, touchY: Float) {
        if (currentShape!!.shape.hasBeenTapped()) {
            // just a tap, this is not a shape, so remove it
            drawShapes.remove(currentShape)
            //handleTap(touchX, touchY);
        }
        if (viewChangeListener != null) {
            viewChangeListener!!.onStopDrawing()
            viewChangeListener!!.onViewAdd(this)
        }
    }

    fun undo(): Boolean {
        if (!drawShapes.empty()) {
            redoShapes.push(drawShapes.pop())
            invalidate()
        }
        if (viewChangeListener != null) {
            viewChangeListener!!.onViewRemoved(this)
        }
        return !drawShapes.empty()
    }

    fun redo(): Boolean {
        if (!redoShapes.empty()) {
            drawShapes.push(redoShapes.pop())
            invalidate()
        }
        if (viewChangeListener != null) {
            viewChangeListener!!.onViewAdd(this)
        }
        return !redoShapes.empty()
    }

    // region eraser
    fun brushEraser() {
        isDrawingEnabled = true
        isErasing = true
    }

    fun setBrushEraserSize(brushEraserSize: Float) {
        eraserSize = brushEraserSize
    }

    // endregion
    // region Setters/Getters
    fun setShapeBuilder(shapeBuilder: ShapeBuilder?) {
        currentShapeBuilder = shapeBuilder
    }

    fun enableDrawing(brushDrawMode: Boolean) {
        isDrawingEnabled = brushDrawMode
        isErasing = !brushDrawMode
        if (brushDrawMode) {
            visibility = VISIBLE
        }
    }

    // endregion
    @get:VisibleForTesting
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