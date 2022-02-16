package ja.burhanrashid52.photoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView

class BorderView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet), ZoomListener {

    fun adjustSize(scale: Float) {
        val margin = (resources.getDimensionPixelSize(R.dimen.border_margin) * scale).toInt()
        val params = layoutParams as LayoutParams
        params.bottomMargin = margin
        params.topMargin = margin
        params.leftMargin = margin
        params.rightMargin = margin
        layoutParams = params
    }

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