package ja.burhanrashid52.photoeditor.shape

import android.content.Context
import android.graphics.Path
import android.util.Log
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class LineShape(
    context: Context,
    private val pointerLocation: ArrowPointerLocation? = null
) : AbstractShape("LineShape") {

    private val maxArrowRadius = convertDpsToPixels(context, MAX_ARROW_RADIUS_DP).toFloat()

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

        if (pointerLocation == ArrowPointerLocation.BOTH || pointerLocation == ArrowPointerLocation.START) {
            drawArrow(path, right, bottom, left, top)
        }

        if (pointerLocation == ArrowPointerLocation.BOTH || pointerLocation == ArrowPointerLocation.END) {
            drawArrow(path, left, top, right, bottom)
        }

        path.moveTo(left, top)
        path.lineTo(right, bottom)
        path.close()

        return path
    }

    private fun drawArrow(path: Path, fromX: Float, fromY: Float, toX: Float, toY: Float) {
        // Based on: https://stackoverflow.com/a/41734848/1219654

        val xDistance = toX - fromX
        val yDistance = toY - fromY

        val lineAngle = atan2(yDistance, xDistance)
        val arrowRadius = (hypot(xDistance, yDistance) / 2.5f).coerceAtMost(maxArrowRadius)

        val anglePointerA = lineAngle - ANGLE_RAD
        val anglePointerB = lineAngle + ANGLE_RAD

        path.moveTo(toX, toY)
        path.lineTo(
            (toX - arrowRadius * cos(anglePointerA)),
            (toY - arrowRadius * sin(anglePointerA))
        )

        path.moveTo(toX, toY)
        path.lineTo(
            (toX - arrowRadius * cos(anglePointerB)),
            (toY - arrowRadius * sin(anglePointerB))
        )
    }

    override fun stopShape() {
        Log.d(tag, "stopShape")
    }

    private companion object {

        const val ARROW_ANGLE = 30.0
        const val ANGLE_RAD = (PI * ARROW_ANGLE / 180.0).toFloat()
        const val MAX_ARROW_RADIUS_DP = 32.0f

        fun convertDpsToPixels(context: Context, sizeDp: Float): Int {
            // Convert the dps to pixels
            val scale = context.resources.displayMetrics.density
            // Use sizePx as a size in pixels
            return (sizeDp * scale + 0.5f).toInt()
        }

    }

}