package ja.burhanrashid52.photoeditor.shape

import android.graphics.Path
import android.util.Log
import kotlin.math.abs

class RectangleShape : AbstractShape() {
    private var lastX = 0f
    private var lastY = 0f
    override val tag: String
        get() = "RectangleShape"

    override fun startShape(x: Float, y: Float) {
        Log.d(tag, "startShape@ $x,$y")
        left = x
        top = y
    }

    override fun moveShape(x: Float, y: Float) {
        right = x
        bottom = y
        val dx = abs(x - lastX)
        val dy = abs(y - lastY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            path = createRectanglePath()
            lastX = x
            lastY = y
        }
    }

    private fun createRectanglePath(): Path {
        val path = Path()
        path.moveTo(left, top)
        path.lineTo(left, bottom)
        path.lineTo(right, bottom)
        path.lineTo(right, top)
        path.close()
        return path
    }

    override fun stopShape() {
        Log.d(tag, "stopShape")
    }
}