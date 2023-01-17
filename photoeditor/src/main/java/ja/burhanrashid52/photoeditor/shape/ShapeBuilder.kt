package ja.burhanrashid52.photoeditor.shape

import android.graphics.Color
import androidx.annotation.ColorInt

/**
 *
 *
 * Used to hold a Shape parameters: type, size, opacity and color.
 *
 */
class ShapeBuilder {

    var shapeType: ShapeType = ShapeType.Brush
        private set

    var shapeSize: Float = DEFAULT_SHAPE_SIZE
        private set

    @androidx.annotation.IntRange(from = 0, to = 255)
    var shapeOpacity: Int? = DEFAULT_SHAPE_OPACITY
        private set

    @get:ColorInt
    @ColorInt
    var shapeColor: Int = DEFAULT_SHAPE_COLOR
        private set

    fun withShapeType(shapeType: ShapeType): ShapeBuilder {
        this.shapeType = shapeType
        return this
    }

    fun withShapeSize(size: Float): ShapeBuilder {
        shapeSize = size
        return this
    }

    fun withShapeOpacity(
        @androidx.annotation.IntRange(
            from = 0,
            to = 255
        ) opacity: Int?
    ): ShapeBuilder {
        shapeOpacity = opacity
        return this
    }

    fun withShapeColor(@ColorInt color: Int): ShapeBuilder {
        shapeColor = color
        return this
    }

    companion object {
        const val DEFAULT_SHAPE_SIZE = 25.0f
        val DEFAULT_SHAPE_OPACITY = null
        const val DEFAULT_SHAPE_COLOR = Color.BLACK
    }

}