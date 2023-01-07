package ja.burhanrashid52.photoeditor.shape

import android.graphics.Path
import android.util.Log
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin


class LineShape(
    private val pointerLocation: ArrowPointerLocation? = null
) : AbstractShape("LineShape") {

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
        val dx = abs(x - lastX)
        val dy = abs(y - lastY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path = createLinePath()
            lastX = x
            lastY = y
        }
    }

    private fun createLinePath(): Path {
        val path = Path()

        if (pointerLocation == ArrowPointerLocation.START || pointerLocation == ArrowPointerLocation.BOTH) {
            drawArrow(path, right, bottom, left, top)
        }

        if (pointerLocation == ArrowPointerLocation.END || pointerLocation == ArrowPointerLocation.BOTH) {
            drawArrow(path, left, top, right, bottom)
        }

        path.moveTo(left, top)
        path.lineTo(right, bottom)
        path.close()

        return path
    }

    private fun drawArrow(path: Path, fromX: Float, fromY: Float, toX: Float, toY: Float) {
        // Based on: https://stackoverflow.com/a/41734848/1219654

        val lineAngle = atan2(toY - fromY, toX - fromX)
        val arrowRadius =
            (max(abs(toX - fromX), abs(toY - fromY)) / 2.0f).coerceAtMost(ARROW_MAX_RADIUS)

        path.moveTo(toX, toY)
        path.lineTo(
            (toX - arrowRadius * cos(lineAngle - ANGLE_RAD / 2.0f)),
            (toY - arrowRadius * sin(lineAngle - ANGLE_RAD / 2.0f))
        )

        path.moveTo(toX, toY)
        path.lineTo(
            (toX - arrowRadius * cos(lineAngle + ANGLE_RAD / 2.0f)),
            (toY - arrowRadius * sin(lineAngle + ANGLE_RAD / 2.0f))
        )
    }

    override fun stopShape() {
        Log.d(tag, "stopShape")
    }

    private companion object {

        const val ARROW_ANGLE = 60.0
        const val ANGLE_RAD = (PI * ARROW_ANGLE / 180.0).toFloat()
        const val ARROW_MAX_RADIUS = 80.0f

    }

}