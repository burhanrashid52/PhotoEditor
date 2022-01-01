package ja.burhanrashid52.photoeditor

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * The border view that wraps the image/text inside.
 * When the user resizes the sticker/text, the border view should adjust margin to keep ratio with sticker handle views.
 */
class BorderView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet),
    ZoomListener {

    override fun onBackgroundZoomStarted() {
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ZoomManager.addZoomListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ZoomManager.removeZoomListener(this)
    }

    override fun onBackgroundZoomChanged(scale: Float) {

    }

}