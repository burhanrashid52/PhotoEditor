package ja.burhanrashid52.photoeditor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
abstract class Graphic {

    private GraphicManager mGraphicManager;

    abstract ViewType getViewType();

    Graphic(GraphicManager graphicManager) {
        mGraphicManager = graphicManager;
    }

    protected void addViewToParent(View view) {
        mGraphicManager.addView(view, getViewType());
    }

    protected void clearHelperBox(ViewGroup viewGroup, PhotoEditorViewState viewState) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            FrameLayout frmBorder = childAt.findViewById(R.id.frmBorder);
            if (frmBorder != null) {
                frmBorder.setBackgroundResource(0);
            }
            ImageView imgClose = childAt.findViewById(R.id.imgPhotoEditorClose);
            if (imgClose != null) {
                imgClose.setVisibility(View.GONE);
            }
        }
        viewState.clearCurrentSelectedView();
    }

    protected void viewUndo(View removedView) {
        mGraphicManager.removeView(removedView, getViewType());
    }
}
