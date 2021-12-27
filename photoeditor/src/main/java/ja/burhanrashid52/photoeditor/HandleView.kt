package ja.burhanrashid52.photoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView

/**
 * The handle view at the corner of image/text when adding a new sticker/text
 * When the user resizes the sticker/text, the sticker handle view should adjust margin, size to keep handle size.
 */

@SuppressLint("AppCompatCustomView")
class HandleView(context: Context, attributeSet: AttributeSet) : ImageView(context, attributeSet),
    ZoomListener {

    fun adjustSize(scale: Float, gravity: Int = Gravity.TOP or Gravity.START) {
        val size = (resources.getDimensionPixelSize(R.dimen.handle_size) * scale).toInt()
        val margin = (resources.getDimensionPixelSize(R.dimen.handle_margin) * scale).toInt()
        val params = FrameLayout.LayoutParams(size, size)
        params.bottomMargin = margin
        params.topMargin = margin
        params.leftMargin = margin
        params.rightMargin = margin
        params.gravity = gravity
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
        scaleX = 1f / ZoomManager.scale
        scaleY = 1f / ZoomManager.scale
    }
}