package ja.burhanrashid52.photoeditor.shape

import android.graphics.Path
import ja.burhanrashid52.photoeditor.shape.AbstractShape
import android.graphics.RectF
import android.util.Log

class OvalShape : AbstractShape() {
    private var lastX = 0f
    private var lastY = 0f
    override val tag: String
        protected get() = "OvalShape"

    override fun startShape(x: Float, y: Float) {
        Log.d(tag, "startShape@ $x,$y")
        left = x
        top = y
    }

    override fun moveShape(x: Float, y: Float) {
        right = x
        bottom = y
        val dx = Math.abs(x - lastX)
        val dy = Math.abs(y - lastY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path = createOvalPath()
            lastX = x
            lastY = y
        }
    }

    private fun createOvalPath(): Path {
        val rect = RectF(left, top, right, bottom)
        val path = Path()
        path.moveTo(left, top)
        path.addOval(rect, Path.Direction.CW)
        path.close()
        return path
    }

    override fun stopShape() {
        Log.d(tag, "stopShape")
    }
}