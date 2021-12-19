package ja.burhanrashid52.photoeditor

import android.view.View
import android.view.ViewGroup
import ja.burhanrashid52.photoeditor.PhotoEditorViewState
import android.widget.FrameLayout
import android.widget.ImageView
import ja.burhanrashid52.photoeditor.R
import ja.burhanrashid52.photoeditor.DrawingView

/**
 * Created by Burhanuddin Rashid on 18/05/21.
 *
 * @author <https:></https:>//github.com/burhanrashid52>
 */
internal class BoxHelper(
    private val mViewGroup: ViewGroup,
    private val mViewState: PhotoEditorViewState
) {
    fun clearHelperBox() {
        for (i in 0 until mViewGroup.childCount) {
            val childAt = mViewGroup.getChildAt(i)
            val frmBorder = childAt.findViewById<FrameLayout>(R.id.frmBorder)
            frmBorder?.setBackgroundResource(0)
            val imgClose = childAt.findViewById<ImageView>(R.id.imgPhotoEditorClose)
            if (imgClose != null) {
                imgClose.visibility = View.GONE
            }
        }
        mViewState.clearCurrentSelectedView()
    }

    fun clearAllViews(drawingView: DrawingView?) {
        for (i in 0 until mViewState.addedViewsCount) {
            mViewGroup.removeView(mViewState.getAddedView(i))
        }
        if (mViewState.containsAddedView(drawingView)) {
            mViewGroup.addView(drawingView)
        }
        mViewState.clearAddedViews()
        mViewState.clearRedoViews()
        drawingView?.clearAll()
    }
}