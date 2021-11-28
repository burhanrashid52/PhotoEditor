package ja.burhanrashid52.photoeditor.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

abstract class AbstractShape : Shape {
    protected var touchTolerance = 4f
    protected var path = Path()
    protected var left = 0f
    protected var top = 0f
    protected var right = 0f
    protected var bottom = 0f
    protected abstract val tag: String
    override fun draw(canvas: Canvas, paint: Paint?) {
        canvas.drawPath(path, paint!!)
    }

    private val bounds: RectF
        get() {
            val bounds = RectF()
            path.computeBounds(bounds, true)
            return bounds
        }

    fun hasBeenTapped(): Boolean {
        val bounds = bounds
        return bounds.top < touchTolerance && bounds.bottom < touchTolerance && bounds.left < touchTolerance && bounds.right < touchTolerance
    }

    override fun toString(): String {
        return tag +
                ": left: " + left +
                " - top: " + top +
                " - right: " + right +
                " - bottom: " + bottom
    }
}