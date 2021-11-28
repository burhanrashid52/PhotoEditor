package ja.burhanrashid52.photoeditor

class TextShadow(private var radius: Float, private var dx: Float, private var dy: Float, private var color: Int) {
    fun getRadius(): Float {
        return radius
    }

    fun setRadius(radius: Float) {
        this.radius = radius
    }

    fun getDx(): Float {
        return dx
    }

    fun setDx(dx: Float) {
        this.dx = dx
    }

    fun getDy(): Float {
        return dy
    }

    fun setDy(dy: Float) {
        this.dy = dy
    }

    fun getColor(): Int {
        return color
    }

    fun setColor(color: Int) {
        this.color = color
    }
}