package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.view.LayoutInflater;
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

    protected final View mRootView;

    private final GraphicManager mGraphicManager;

    abstract ViewType getViewType();

    abstract int getLayoutId();

    abstract void setupView(View rootView);

    void updateView(View view) {
        //Optional for subclass to override
    }

    Graphic(Context context, GraphicManager graphicManager) {
        mGraphicManager = graphicManager;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (getLayoutId() == 0)
            throw new UnsupportedOperationException("Layout id cannot be zero. Please define a layout");
        mRootView = layoutInflater.inflate(getLayoutId(), null);
        setupView(mRootView);
        setupRemoveView(mRootView);
    }

    private void setupRemoveView(final View rootView) {
        //We are setting tag as ViewType to identify what type of the view it is
        //when we remove the view from stack i.e onRemoveViewListener(ViewType viewType, int numberOfAddedViews);
        final ViewType viewType = getViewType();
        rootView.setTag(viewType);
        final ImageView imgClose = rootView.findViewById(R.id.imgPhotoEditorClose);
        if (imgClose != null) {
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGraphicManager.removeView(rootView, viewType);
                }
            });
        }
    }

    protected void addViewToParent() {
        mGraphicManager.addView(mRootView, getViewType());
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

    protected void toggleSelection() {
        View frmBorder = mRootView.findViewById(R.id.frmBorder);
        View imgClose = mRootView.findViewById(R.id.imgPhotoEditorClose);
        if (frmBorder != null) {
            frmBorder.setBackgroundResource(R.drawable.rounded_border_tv);
            frmBorder.setTag(true);
        }
        if (imgClose != null) {
            imgClose.setVisibility(View.VISIBLE);
        }
    }

    protected MultiTouchListener.OnGestureControl buildGestureController(final ViewGroup viewGroup, final PhotoEditorViewState viewState) {
        return new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                clearHelperBox(viewGroup, viewState);
                toggleSelection();
                // Change the in-focus view
                viewState.setCurrentSelectedView(mRootView);
            }

            @Override
            public void onLongClick() {
                updateView(mRootView);
            }
        };
    }
}
