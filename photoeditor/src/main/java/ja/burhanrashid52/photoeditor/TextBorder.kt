package ja.burhanrashid52.photoeditor

class TextBorder(private var corner: Float, private var backGroundColor: Int, private var strokeWidth: Int, private var strokeColor: Int) {
    fun getCorner(): Float {
        return corner
    }

    fun setCorner(corner: Float) {
        this.corner = corner
    }

    fun getBackGroundColor(): Int {
        return backGroundColor
    }

    fun setBackGroundColor(backGroundColor: Int) {
        this.backGroundColor = backGroundColor
    }

    fun getStrokeWidth(): Int {
        return strokeWidth
    }

    fun setStrokeWidth(strokeWidth: Int) {
        this.strokeWidth = strokeWidth
    }

    fun getStrokeColor(): Int {
        return strokeColor
    }

    fun setStrokeColor(strokeColor: Int) {
        this.strokeColor = strokeColor
    }
}