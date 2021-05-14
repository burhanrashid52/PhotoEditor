package ja.burhanrashid52.photoeditor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
abstract class Graphic {
    protected final ViewGroup mPhotoEditorView;
    protected final PhotoEditorViewState mViewState;

    abstract ViewType getViewType();

    abstract OnPhotoEditorListener getOnPhotoEditorListener();

    Graphic(ViewGroup photoEditorView, PhotoEditorViewState viewState) {
        mPhotoEditorView = photoEditorView;
        mViewState = viewState;
    }

    protected void addViewToParent(View rootView) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mPhotoEditorView.addView(rootView, params);
        mViewState.addAddedView(rootView);
        OnPhotoEditorListener onPhotoEditorListener = getOnPhotoEditorListener();
        if (onPhotoEditorListener != null)
            onPhotoEditorListener.onAddViewListener(getViewType(), mViewState.getAddedViewsCount());
    }

    protected void clearHelperBox() {
        for (int i = 0; i < mPhotoEditorView.getChildCount(); i++) {
            View childAt = mPhotoEditorView.getChildAt(i);
            FrameLayout frmBorder = childAt.findViewById(R.id.frmBorder);
            if (frmBorder != null) {
                frmBorder.setBackgroundResource(0);
            }
            ImageView imgClose = childAt.findViewById(R.id.imgPhotoEditorClose);
            if (imgClose != null) {
                imgClose.setVisibility(View.GONE);
            }
        }
        mViewState.clearCurrentSelectedView();
    }

    protected void viewUndo(View removedView) {
        if (mViewState.containsAddedView(removedView)) {
            mPhotoEditorView.removeView(removedView);
            mViewState.removeAddedView(removedView);
            mViewState.pushRedoView(removedView);
            OnPhotoEditorListener onPhotoEditorListener = getOnPhotoEditorListener();
            if (onPhotoEditorListener != null) {
                onPhotoEditorListener.onRemoveViewListener(
                        getViewType(),
                        mViewState.getAddedViewsCount()
                );
            }
        }
    }
}
