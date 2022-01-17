package ja.burhanrashid52.photoeditor

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.widget.TextView
import java.util.*

/**
 *
 *
 * This class is used to wrap the styles to apply on the TextView on [PhotoEditor.addText] and [PhotoEditor.editText]
 *
 *
 * @author [Christian Caballero](https://github.com/Sulfkain)
 * @since 14/05/2019
 */
open class TextStyleBuilder {
    val values = mutableMapOf<TextStyle, Any>()

    /**
     * Set this textSize style
     *
     * @param size Size to apply on text
     */
    fun withTextSize(size: Float) {
        values[TextStyle.SIZE] = size
    }

    /**
     * Set this textShadow style
     *
     * @param radius Radius of the shadow to apply on text
     * @param dx Horizontal distance of the shadow
     * @param dy Vertical distance of the shadow
     * @param color Color of the shadow
     */
    fun withTextShadow(radius: Float, dx: Float, dy: Float, color: Int) {
        val shadow = TextShadow(radius, dx, dy, color)
        withTextShadow(shadow)
    }

    /**
     * Set this color style
     *
     * @param color Color to apply on text
     */
    fun withTextColor(color: Int) {
        values[TextStyle.COLOR] = color
    }

    /**
     * Set this [Typeface] style
     *
     * @param textTypeface TypeFace to apply on text
     */
    fun withTextFont(textTypeface: Typeface) {
        values[TextStyle.FONT_FAMILY] = textTypeface
    }

    /**
     * Set this gravity style
     *
     * @param gravity Gravity style to apply on text
     */
    fun withGravity(gravity: Int) {
        values[TextStyle.GRAVITY] = gravity
    }

    /**
     * Set this background color
     *
     * @param background Background color to apply on text, this method overrides the preview set on [TextStyleBuilder.withBackgroundDrawable]
     */
    fun withBackgroundColor(background: Int) {
        values[TextStyle.BACKGROUND] = background
    }

    /**
     * Set this background [Drawable], this method overrides the preview set on [TextStyleBuilder.withBackgroundColor]
     *
     * @param bgDrawable Background drawable to apply on text
     */
    fun withBackgroundDrawable(bgDrawable: Drawable) {
        values[TextStyle.BACKGROUND] = bgDrawable
    }

    /**
     * Set this textAppearance style
     *
     * @param textAppearance Text style to apply on text
     */
    fun withTextAppearance(textAppearance: Int) {
        values[TextStyle.TEXT_APPEARANCE] =
            textAppearance
    }

    fun withTextStyle(typeface: Int) {
        values[TextStyle.TEXT_STYLE] = typeface
    }

    fun withTextFlag(paintFlag: Int) {
        values[TextStyle.TEXT_FLAG] = paintFlag
    }

    fun withTextShadow(textShadow: TextShadow) {
        values[TextStyle.SHADOW] = textShadow
    }

    fun withTextBorder(textBorder: TextBorder) {
        values[TextStyle.BORDER] = textBorder
    }

    /**
     * Method to apply all the style setup on this Builder}
     *
     * @param textView TextView to apply the style
     */
    fun applyStyle(textView: TextView) {
        for ((key, value) in values) {
            when (key) {
                TextStyle.SIZE -> {
                    val size = value as Float
                    applyTextSize(textView, size)
                }
                TextStyle.COLOR -> {
                    val color = value as Int
                    applyTextColor(textView, color)
                }
                TextStyle.FONT_FAMILY -> {
                    val typeface = value as Typeface
                    applyFontFamily(textView, typeface)
                }
                TextStyle.GRAVITY -> {
                    val gravity = value as Int
                    applyGravity(textView, gravity)
                }
                TextStyle.BACKGROUND -> {
                    if (value is Drawable) {
                        applyBackgroundDrawable(textView, value)
                    } else if (value is Int) {
                        applyBackgroundColor(textView, value)
                    }
                }
                TextStyle.TEXT_APPEARANCE -> {
                    if (value is Int) {
                        applyTextAppearance(textView, value)
                    }
                }
                TextStyle.TEXT_STYLE -> {
                    val typeface = value as Int
                    applyTextStyle(textView, typeface)
                }
                TextStyle.TEXT_FLAG -> {
                    val flag = value as Int
                    applyTextFlag(textView, flag)
                }
                TextStyle.SHADOW -> {
                    run {
                        if (value is TextShadow) {
                            applyTextShadow(textView, value)
                        }
                    }
                    run {
                        if (value is TextBorder) {
                            applyTextBorder(textView, value)
                        }
                    }
                }
                TextStyle.BORDER -> {
                    if (value is TextBorder) {
                        applyTextBorder(textView, value)
                    }
                }
            }
        }
    }

    protected open fun applyTextSize(textView: TextView, size: Float) {
        textView.textSize = size
    }

    protected fun applyTextShadow(
        textView: TextView,
        radius: Float,
        dx: Float,
        dy: Float,
        color: Int
    ) {
        textView.setShadowLayer(radius, dx, dy, color)
    }

    protected open fun applyTextColor(textView: TextView, color: Int) {
        textView.setTextColor(color)
    }

    protected open fun applyFontFamily(textView: TextView, typeface: Typeface?) {
        textView.typeface = typeface
    }

    protected open fun applyGravity(textView: TextView, gravity: Int) {
        textView.gravity = gravity
    }

    protected open fun applyBackgroundColor(textView: TextView, color: Int) {
        textView.setBackgroundColor(color)
    }

    protected open fun applyBackgroundDrawable(textView: TextView, bg: Drawable?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.background = bg
        } else {
            textView.setBackgroundDrawable(bg)
        }
    }

    // border
    protected open fun applyTextBorder(textView: TextView, textBorder: TextBorder) {
        val gd = GradientDrawable()
        gd.cornerRadius = textBorder.corner
        gd.setStroke(textBorder.strokeWidth, textBorder.strokeColor)
        gd.setColor(textBorder.backGroundColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.background = gd
        }
    }

    // shadow
    protected open fun applyTextShadow(textView: TextView, textShadow: TextShadow) {
        textView.setShadowLayer(textShadow.radius, textShadow.dx, textShadow.dy, textShadow.color)
    }

    // bold or italic
    protected open fun applyTextStyle(textView: TextView, typeface: Int) {
        textView.setTypeface(textView.typeface, typeface)
    }

    // underline or strike
    protected open fun applyTextFlag(textView: TextView, flag: Int) {
//        textView.setPaintFlags(textView.getPaintFlags()|flag);
        textView.paint.flags = flag
    }

    protected open fun applyTextAppearance(textView: TextView, styleAppearance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(styleAppearance)
        } else {
            textView.setTextAppearance(textView.context, styleAppearance)
        }
    }

    /**
     * Enum to maintain current supported style properties used on on [PhotoEditor.addText] and [PhotoEditor.editText]
     */
    enum class TextStyle(val property: String) {
        SIZE("TextSize"),
        COLOR("TextColor"),
        GRAVITY("Gravity"),
        FONT_FAMILY("FontFamily"),
        BACKGROUND("Background"),
        TEXT_APPEARANCE("TextAppearance"),
        TEXT_STYLE("TextStyle"),
        TEXT_FLAG("TextFlag"),
        SHADOW("Shadow"),
        BORDER("Border");

    }
}