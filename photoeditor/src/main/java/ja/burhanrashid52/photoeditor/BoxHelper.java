package ja.burhanrashid52.photoeditor;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Burhanuddin Rashid on 18/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
class BoxHelper {
    private final RelativeLayout mCanvasView;
    private final PhotoEditorViewState mViewState;

    public BoxHelper(RelativeLayout canvasView, PhotoEditorViewState viewState) {
        mCanvasView = canvasView;
        mViewState = viewState;
    }

    void clearHelperBox() {
        for (int i = 0; i < mCanvasView.getChildCount(); i++) {
            View childAt = mCanvasView.getChildAt(i);
            FrameLayout frmBorder = childAt.findViewById(R.id.frmBorder);
            if (frmBorder != null) {
                frmBorder.setBackgroundResource(0);
            }
            ImageView imgClose = null;
            if (imgClose != null) {
                imgClose.setVisibility(View.GONE);
            }
        }
        mViewState.clearCurrentSelectedView();
    }

    public void clearAllViews(DrawingView drawingView) {
        for (int i = 0; i < mViewState.getAddedViewsCount(); i++) {
            mCanvasView.removeView(mViewState.getAddedView(i));
        }
        if (mViewState.containsAddedView(drawingView)) {
            mCanvasView.addView(drawingView);
        }
        mViewState.clearAddedViews();
        mViewState.clearRedoViews();

        if (drawingView != null)
            drawingView.clearAll();
    }
}
