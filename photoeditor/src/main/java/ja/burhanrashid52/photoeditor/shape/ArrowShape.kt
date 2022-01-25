package ja.burhanrashid52.photoeditor.shape

import android.graphics.Path
import android.util.Log
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class ArrowShape : AbstractShape("ArrowShape") {

    private var lastX = 0f
    private var lastY = 0f

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
            path = createArrowPath()
            lastX = x
            lastY = y
        }
    }

    private fun createArrowPath(): Path {
        val path = Path()
        val headLength = 100f
        val degreesInRadians225 = (225 * Math.PI / 180).toFloat()
        val degreesInRadians135 = (135 * Math.PI / 180).toFloat()

        // calc the angle of the line
        val dx = right - left
        val dy = bottom - top
        val angle = atan2(dy.toDouble(), dx.toDouble()).toFloat()

        // calc arrowhead points
        val x225 = (right + headLength * cos((angle + degreesInRadians225).toDouble())).toFloat()
        val y225 = (bottom + headLength * sin((angle + degreesInRadians225).toDouble())).toFloat()
        val x135 = (right + headLength * cos((angle + degreesInRadians135).toDouble())).toFloat()
        val y135 = (bottom + headLength * sin((angle + degreesInRadians135).toDouble())).toFloat()

        // draw line
        path.moveTo(left, top)
        path.lineTo(right, bottom)

        // draw partial arrowhead at 225 degrees
        path.moveTo(right, bottom)
        path.lineTo(x225, y225)

        // draw partial arrowhead at 135 degrees
        path.moveTo(right, bottom)
        path.lineTo(x135, y135)
        path.close()
        return path
    }

    override fun stopShape() {
        Log.d(tag, "ArrowShape")
    }
}