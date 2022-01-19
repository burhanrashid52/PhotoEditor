package ja.burhanrashid52.photoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import kotlin.math.max
import kotlin.math.min

/**
 * The handle view at the corner of image/text when adding a new sticker/text
 * When the user resizes the sticker/text, the sticker handle view should adjust margin, size to keep handle size.
 */

@SuppressLint("AppCompatCustomView")
class HandleView(context: Context, attributeSet: AttributeSet) : ImageView(context, attributeSet),
    ZoomListener {

    fun adjustSize(scale: Float) {
        val backgroundScale = 1f / ZoomManager.scale
        val size =
            (resources.getDimensionPixelSize(R.dimen.handle_size) * scale * backgroundScale).toInt()
        val borderMargin = resources.getDimensionPixelSize(R.dimen.border_margin)
        val margin = max(borderMargin - size / 2, 0)
        val params = FrameLayout.LayoutParams(size, size)

        params.bottomMargin = margin
        params.topMargin = margin
        params.leftMargin = margin
        params.rightMargin = margin
        if (layoutParams is FrameLayout.LayoutParams) {
            params.gravity = (layoutParams as FrameLayout.LayoutParams).gravity
        }

        layoutParams = params
    }

    override fun onBackgroundZoomStarted() {
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        adjustScale()
        ZoomManager.addZoomListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ZoomManager.removeZoomListener(this)
    }

    override fun onBackgroundZoomChanged(scale: Float) {
        adjustScale()
    }

    private fun adjustScale() {
        adjustSize(1f)
    }
}