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
    private final ViewGroup mViewGroup;
    private final PhotoEditorViewState mViewState;
    private OnPhotoEditorListener mOnPhotoEditorListener;
    private @Nullable
    GraphicHelper mGraphicHelper;

    public GraphicManager(ViewGroup viewGroup, PhotoEditorViewState viewState) {
        mViewGroup = viewGroup;
        mViewState = viewState;
    }

    public void addView(Graphic graphic) {
        View view = graphic.getRootView();
        if (view instanceof BrushDrawingView) {
            if (mViewState.getRedoViewsCount() > 0) {
                mViewState.popRedoView();
            }
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            mViewGroup.addView(view, params);
        }

        mViewState.addAddedView(view);
        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onAddViewListener(graphic.getViewType(), mViewState.getAddedViewsCount());
    }

    public void removeView(Graphic graphic) {
        View view = graphic.getRootView();
        if (view instanceof BrushDrawingView) {
            if (mViewState.getAddedViewsCount() > 0) {
                View removeView = mViewState.removeAddedView(
                        mViewState.getAddedViewsCount() - 1
                );
                if (!(removeView instanceof BrushDrawingView)) {
                    mViewGroup.removeView(removeView);
                }
                mViewState.pushRedoView(removeView);
            }
            if (mOnPhotoEditorListener != null) {
                mOnPhotoEditorListener.onRemoveViewListener(
                        ViewType.BRUSH_DRAWING,
                        mViewState.getAddedViewsCount()
                );
            }
            return;
        }

        if (mViewState.containsAddedView(view)) {
            mViewGroup.removeView(view);
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
        mViewGroup.updateViewLayout(view, view.getLayoutParams());
        mViewState.replaceAddedView(view);
    }

    public void setOnPhotoEditorListener(OnPhotoEditorListener onPhotoEditorListener) {
        mOnPhotoEditorListener = onPhotoEditorListener;
    }

    OnPhotoEditorListener getOnPhotoEditorListener() {
        return mOnPhotoEditorListener;
    }

    public boolean undo() {
        if (mViewState.getAddedViewsCount() > 0) {
            View removeView = mViewState.getAddedView(
                    mViewState.getAddedViewsCount() - 1
            );
            if (removeView instanceof BrushDrawingView) {
                BrushDrawingView brushDrawingView = (BrushDrawingView) removeView;
                return brushDrawingView.undo();
            } else {
                mViewState.removeAddedView(mViewState.getAddedViewsCount() - 1);
                mViewGroup.removeView(removeView);
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

    public boolean redo() {
        if (mViewState.getRedoViewsCount() > 0) {
            View redoView = mViewState.getRedoView(
                    mViewState.getRedoViewsCount() - 1
            );
            if (redoView instanceof BrushDrawingView) {
                BrushDrawingView brushDrawingView = (BrushDrawingView) redoView;
                return brushDrawingView.redo();
            } else {
                mViewState.popRedoView();
                mViewGroup.addView(redoView);
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

    GraphicHelper getGraphicHelper() {
        if (mGraphicHelper == null) {
            mGraphicHelper = new GraphicHelper(mViewGroup, mViewState);
        }
        return mGraphicHelper;
    }
}
