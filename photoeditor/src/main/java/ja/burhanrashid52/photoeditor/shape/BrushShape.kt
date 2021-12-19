package ja.burhanrashid52.photoeditor.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import ja.burhanrashid52.photoeditor.shape.AbstractShape

class BrushShape : AbstractShape() {
    override val tag: String
        protected get() = "BrushShape"

    override fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawPath(path, paint)
    }

    override fun startShape(x: Float, y: Float) {
        Log.d(tag, "startShape@ $x,$y")
        path.moveTo(x, y)
        left = x
        top = y
    }

    override fun moveShape(x: Float, y: Float) {
        val dx = Math.abs(x - left)
        val dy = Math.abs(y - top)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(left, top, (x + left) / 2, (y + top) / 2)
            left = x
            top = y
        }
    }

    override fun stopShape() {
        Log.d(tag, "stopShape")
    }
}