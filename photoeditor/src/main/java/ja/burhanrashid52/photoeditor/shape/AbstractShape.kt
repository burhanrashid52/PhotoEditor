package ja.burhanrashid52.photoeditor.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

abstract class AbstractShape(protected val tag: String) : Shape {
    protected var path = Path()
    protected var left = 0f
    protected var top = 0f
    protected var right = 0f
    protected var bottom = 0f

    override fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawPath(path, paint)
    }

    private val bounds: RectF
        get() {
            val bounds = RectF()
            path.computeBounds(bounds, true)
            return bounds
        }

    fun hasBeenTapped(): Boolean {
        val bounds = bounds
        return bounds.top < TOUCH_TOLERANCE && bounds.bottom < TOUCH_TOLERANCE && bounds.left < TOUCH_TOLERANCE && bounds.right < TOUCH_TOLERANCE
    }

    override fun toString(): String {
        return tag +
                ": left: " + left +
                " - top: " + top +
                " - right: " + right +
                " - bottom: " + bottom
    }

    companion object {
        const val TOUCH_TOLERANCE = 4f
    }
}