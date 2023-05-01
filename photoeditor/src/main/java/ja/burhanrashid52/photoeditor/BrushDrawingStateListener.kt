package ja.burhanrashid52.photoeditor

/**
 * Created by Burhanuddin Rashid on 17/05/21.
 *
 * @author <https:></https:>//github.com/burhanrashid52>
 */
class BrushDrawingStateListener internal constructor(
    private val mPhotoEditorView: PhotoEditorView,
    private val mViewState: PhotoEditorViewState
) : BrushViewChangeListener {
    private var mOnPhotoEditorListener: OnPhotoEditorListener? = null
    fun setOnPhotoEditorListener(onPhotoEditorListener: OnPhotoEditorListener?) {
        mOnPhotoEditorListener = onPhotoEditorListener
    }

    override fun onViewAdd(drawingView: DrawingView) {
        if (mViewState.redoViewsCount > 0) {
            mViewState.popRedoView()
        }
        mViewState.addAddedView(drawingView)
        drawingView.post {
            mOnPhotoEditorListener?.onAddViewListener(
                drawingView,
                ViewType.Brush,
                mViewState.addedViewsCount
            )
        }
    }

    override fun onViewRemoved(drawingView: DrawingView) {
        if (mViewState.addedViewsCount > 0) {
            val removeView = mViewState.removeAddedView(
                mViewState.addedViewsCount - 1
            )
            if (removeView !is DrawingView) {
                mPhotoEditorView.removeView(removeView)
            }
            mViewState.pushRedoView(removeView)
        }
        mOnPhotoEditorListener?.onRemoveViewListener(
            drawingView,
            ViewType.Brush,
            mViewState.addedViewsCount
        )
    }

    override fun onStartDrawing(drawingView: DrawingView) {
        mOnPhotoEditorListener?.onStartViewChangeListener(drawingView, ViewType.Brush)

    }

    override fun onStopDrawing(drawingView: DrawingView) {
        mOnPhotoEditorListener?.onStopViewChangeListener(drawingView, ViewType.Brush)
    }
}