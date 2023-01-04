package ja.burhanrashid52.photoeditor.shape

import android.graphics.Path
import android.util.Log
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class LineShape(
    private val pointerPosition: ArrowPointerPosition? = null
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

        if (pointerPosition == ArrowPointerPosition.START || pointerPosition == ArrowPointerPosition.BOTH) {
            drawArrow(path, left.toDouble(), top.toDouble(), right.toDouble(), bottom.toDouble())
        }

        if (pointerPosition == ArrowPointerPosition.END || pointerPosition == ArrowPointerPosition.BOTH) {
            drawArrow(path, right.toDouble(), bottom.toDouble(), left.toDouble(), top.toDouble())
        }

        path.moveTo(left, top)
        path.lineTo(right, bottom)
        path.close()

        return path
    }

    private fun drawArrow(
        path: Path,
        fromX: Double,
        fromY: Double,
        toX: Double,
        toY: Double
    ) {
        // Based on: https://stackoverflow.com/a/41734848/1219654

        val angleRad = (PI * ARROW_ANGLE / 180.0)
        val lineAngle = atan2(toY - fromY, toX - fromX)

        path.moveTo(toX.toFloat(), toY.toFloat())
        path.lineTo(
            (toX - ARROW_RADIUS * cos(lineAngle - angleRad / 2.0)).toFloat(),
            (toY - ARROW_RADIUS * sin(lineAngle - angleRad / 2.0)).toFloat()
        )

        path.moveTo(toX.toFloat(), toY.toFloat())
        path.lineTo(
            (toX - ARROW_RADIUS * cos(lineAngle + angleRad / 2.0)).toFloat(),
            (toY - ARROW_RADIUS * sin(lineAngle + angleRad / 2.0)).toFloat()
        )
    }

    override fun stopShape() {
        Log.d(tag, "stopShape")
    }

    private companion object {

        const val ARROW_ANGLE = 60.0
        const val ARROW_RADIUS = 80.0

    }

}