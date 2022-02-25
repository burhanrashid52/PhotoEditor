package ja.burhanrashid52.photoeditor

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

/**
 * Created by Burhanuddin Rashid on 18/05/21.
 *
 * @author <https:></https:>//github.com/burhanrashid52>
 */
internal class BoxHelper(
    private val mPhotoEditorView: PhotoEditorView,
    private val mViewState: PhotoEditorViewState
) {
    fun clearHelperBox() {
        for (i in 0 until mPhotoEditorView.childCount) {
            val childAt = mPhotoEditorView.getChildAt(i)
            val frmBorder = childAt.findViewById<FrameLayout>(R.id.frmBorder)
            frmBorder?.setBackgroundResource(0)
            val imgClose = childAt.findViewById<ImageView>(R.id.imgPhotoEditorClose)
            imgClose?.visibility = View.GONE
        }
        mViewState.clearCurrentSelectedView()
    }

    fun clearAllViews(drawingView: DrawingView?) {
        for (i in 0 until mViewState.addedViewsCount) {
            mPhotoEditorView.removeView(mViewState.getAddedView(i))
        }
        drawingView?.let {
            if (mViewState.containsAddedView(it)) {
                mPhotoEditorView.addView(it)
            }
        }

        mViewState.clearAddedViews()
        mViewState.clearRedoViews()
        drawingView?.clearAll()
    }
}