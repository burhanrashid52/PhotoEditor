package ja.burhanrashid52.photoeditor

import android.graphics.Paint
import android.graphics.Path

internal class LinePath(drawPath: Path?, drawPaints: Paint?) {
    val drawPaint: Paint
    val drawPath: Path

    init {
        drawPaint = Paint(drawPaints)
        this.drawPath = Path(drawPath)
    }
}