package ja.burhanrashid52.photoeditor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Burhanuddin Rashid on 18/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
class GraphicHelper {
    private final ViewGroup mViewGroup;
    private final PhotoEditorViewState mViewState;

    public GraphicHelper(ViewGroup viewGroup, PhotoEditorViewState viewState) {
        mViewGroup = viewGroup;
        mViewState = viewState;
    }

    void clearHelperBox() {
        for (int i = 0; i < mViewGroup.getChildCount(); i++) {
            View childAt = mViewGroup.getChildAt(i);
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

    public void clearAllViews(BrushDrawingView brushDrawingView) {
        for (int i = 0; i < mViewState.getAddedViewsCount(); i++) {
            mViewGroup.removeView(mViewState.getAddedView(i));
        }
        if (mViewState.containsAddedView(brushDrawingView)) {
            mViewGroup.addView(brushDrawingView);
        }
        mViewState.clearAddedViews();
        mViewState.clearRedoViews();

        if (brushDrawingView != null)
            brushDrawingView.clearAll();
    }
}
