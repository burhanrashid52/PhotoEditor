package ja.burhanrashid52.photoeditor

import android.view.View
import ja.burhanrashid52.photoeditor.Graphic.rootView
import ja.burhanrashid52.photoeditor.Graphic.viewType
import ja.burhanrashid52.photoeditor.DrawingView.undo
import ja.burhanrashid52.photoeditor.DrawingView.redo
import android.view.ViewGroup
import ja.burhanrashid52.photoeditor.PhotoEditorViewState
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.Graphic
import android.widget.RelativeLayout
import ja.burhanrashid52.photoeditor.DrawingView
import ja.burhanrashid52.photoeditor.ViewType

/**
 * Created by Burhanuddin Rashid on 15/05/21.
 *
 * @author <https:></https:>//github.com/burhanrashid52>
 */
internal class GraphicManager(
    private val mViewGroup: ViewGroup,
    private val mViewState: PhotoEditorViewState
) {
    var onPhotoEditorListener: OnPhotoEditorListener? = null
    fun addView(graphic: Graphic) {
        val view = graphic.rootView
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        mViewGroup.addView(view, params)
        mViewState.addAddedView(view)
        if (onPhotoEditorListener != null) onPhotoEditorListener!!.onAddViewListener(
            graphic.viewType,
            mViewState.addedViewsCount
        )
    }

    fun removeView(graphic: Graphic) {
        val view = graphic.rootView
        if (mViewState.containsAddedView(view)) {
            mViewGroup.removeView(view)
            mViewState.removeAddedView(view)
            mViewState.pushRedoView(view)
            if (onPhotoEditorListener != null) {
                onPhotoEditorListener!!.onRemoveViewListener(
                    graphic.viewType,
                    mViewState.addedViewsCount
                )
            }
        }
    }

    fun updateView(view: View) {
        mViewGroup.updateViewLayout(view, view.layoutParams)
        mViewState.replaceAddedView(view)
    }

    fun undoView(): Boolean {
        if (mViewState.addedViewsCount > 0) {
            val removeView = mViewState.getAddedView(
                mViewState.addedViewsCount - 1
            )
            if (removeView is DrawingView) {
                return removeView.undo()
            } else {
                mViewState.removeAddedView(mViewState.addedViewsCount - 1)
                mViewGroup.removeView(removeView)
                mViewState.pushRedoView(removeView)
            }
            if (onPhotoEditorListener != null) {
                val viewTag = removeView.tag
                if (viewTag is ViewType) {
                    onPhotoEditorListener!!.onRemoveViewListener(
                        viewTag,
                        mViewState.addedViewsCount
                    )
                }
            }
        }
        return mViewState.addedViewsCount != 0
    }

    fun redoView(): Boolean {
        if (mViewState.redoViewsCount > 0) {
            val redoView = mViewState.getRedoView(
                mViewState.redoViewsCount - 1
            )
            if (redoView is DrawingView) {
                return redoView.redo()
            } else {
                mViewState.popRedoView()
                mViewGroup.addView(redoView)
                mViewState.addAddedView(redoView)
            }
            val viewTag = redoView.tag
            if (onPhotoEditorListener != null && viewTag is ViewType) {
                onPhotoEditorListener!!.onAddViewListener(
                    viewTag,
                    mViewState.addedViewsCount
                )
            }
        }
        return mViewState.redoViewsCount != 0
    }
}