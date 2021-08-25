package ja.burhanrashid52.photoeditor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

/**
 * Created by Burhanuddin Rashid on 15/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
class GraphicManager {
    private final RelativeLayout mCanvasView;
    private final PhotoEditorViewState mViewState;
    private OnPhotoEditorListener mOnPhotoEditorListener;

    public GraphicManager(RelativeLayout canvasView, PhotoEditorViewState viewState) {
        mCanvasView = canvasView;
        mViewState = viewState;
    }

    public void addView(Graphic graphic) {
        View view = graphic.getRootView();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // NOTE(cheng): Commenting this out since this makes it impossible to resize/move the graphic
        //              after it has been added.
        // params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mCanvasView.addView(view, params);

        mViewState.addAddedView(view);
        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onAddViewListener(graphic.getViewType(), mViewState.getAddedViewsCount());
    }

    public void removeView(Graphic graphic) {
        View view = graphic.getRootView();
        if (mViewState.containsAddedView(view)) {
            mCanvasView.removeView(view);
            mViewState.removeAddedView(view);
            mViewState.pushRedoView(view);
            if (mOnPhotoEditorListener != null) {
                mOnPhotoEditorListener.onRemoveViewListener(
                        graphic.getViewType(),
                        mViewState.getAddedViewsCount()
                );
            }
        }
    }

    public void updateView(View view) {
        mCanvasView.updateViewLayout(view, view.getLayoutParams());
        mViewState.replaceAddedView(view);
    }

    public void setOnPhotoEditorListener(OnPhotoEditorListener onPhotoEditorListener) {
        mOnPhotoEditorListener = onPhotoEditorListener;
    }

    @Nullable
    OnPhotoEditorListener getOnPhotoEditorListener() {
        return mOnPhotoEditorListener;
    }

    public boolean undoView() {
        if (mViewState.getAddedViewsCount() > 0) {
            View removeView = mViewState.getAddedView(
                    mViewState.getAddedViewsCount() - 1
            );
            if (removeView instanceof DrawingView) {
                DrawingView drawingView = (DrawingView) removeView;
                return drawingView.undo();
            } else {
                mViewState.removeAddedView(mViewState.getAddedViewsCount() - 1);
                mCanvasView.removeView(removeView);
                mViewState.pushRedoView(removeView);
            }
            if (mOnPhotoEditorListener != null) {
                Object viewTag = removeView.getTag();
                if (viewTag instanceof ViewType) {
                    mOnPhotoEditorListener.onRemoveViewListener(
                            (ViewType) viewTag,
                            mViewState.getAddedViewsCount()
                    );
                }
            }
        }
        return mViewState.getAddedViewsCount() != 0;
    }

    public boolean redoView() {
        if (mViewState.getRedoViewsCount() > 0) {
            View redoView = mViewState.getRedoView(
                    mViewState.getRedoViewsCount() - 1
            );
            if (redoView instanceof DrawingView) {
                DrawingView drawingView = (DrawingView) redoView;
                return drawingView.redo();
            } else {
                mViewState.popRedoView();
                mCanvasView.addView(redoView);
                mViewState.addAddedView(redoView);
            }
            Object viewTag = redoView.getTag();
            if (mOnPhotoEditorListener != null && viewTag instanceof ViewType) {
                mOnPhotoEditorListener.onAddViewListener(
                        (ViewType) viewTag,
                        mViewState.getAddedViewsCount()
                );
            }
        }
        return mViewState.getRedoViewsCount() != 0;
    }
}
