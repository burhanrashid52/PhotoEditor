package ja.burhanrashid52.photoeditor

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class GraphicView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val borderView: BorderView by lazy { findViewById(R.id.frmBorder) }
    private val handleTopLeft: HandleView by lazy { findViewById(R.id.imgHandleTopLeft) }
    private val handleTopRight: HandleView by lazy { findViewById(R.id.imgHandleTopRight) }
    private val handleBottomLeft: HandleView by lazy { findViewById(R.id.imgHandleBottomLeft) }
    private val handleBottomRight: HandleView by lazy { findViewById(R.id.imgHandleBottomRight) }

    fun hideHandleViews() {
        listOf(handleBottomLeft, handleBottomRight, handleTopLeft, handleTopRight).forEach {
            it.visibility = View.GONE
        }
        borderView.setBackgroundColor(0)
    }

    fun showHandleViews() {
        listOf(handleBottomLeft, handleBottomRight, handleTopLeft, handleTopRight).forEach {
            it.visibility = View.VISIBLE
        }
        borderView.setBackgroundResource(R.drawable.rounded_border_tv)
    }
}