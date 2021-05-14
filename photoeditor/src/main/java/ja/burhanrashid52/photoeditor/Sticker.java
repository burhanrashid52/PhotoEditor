package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
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
class Sticker {

    private final Context mContext;
    private final ViewGroup mPhotoEditorView;
    private final MultiTouchListener mMultiTouchListener;
    private final PhotoEditorViewState mViewState;
    private final OnPhotoEditorListener mOnPhotoEditorListener;
    private final View mRootView;

    public Sticker(ViewGroup photoEditorView,
                   MultiTouchListener multiTouchListener,
                   PhotoEditorViewState viewState,
                   OnPhotoEditorListener onPhotoEditorListener) {
        mContext = photoEditorView.getContext();
        mPhotoEditorView = photoEditorView;
        mMultiTouchListener = multiTouchListener;
        mViewState = viewState;
        mOnPhotoEditorListener = onPhotoEditorListener;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = layoutInflater.inflate(R.layout.view_photo_editor_image, null);
    }

    View buildView(Bitmap desiredImage) {

        final View imageRootView = getLayout(ViewType.IMAGE);
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        final FrameLayout frmBorder = imageRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = imageRootView.findViewById(R.id.imgPhotoEditorClose);

        imageView.setImageBitmap(desiredImage);

        mMultiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                clearHelperBox();
                frmBorder.setBackgroundResource(R.drawable.rounded_border_tv);
                imgClose.setVisibility(View.VISIBLE);
                frmBorder.setTag(true);
                mViewState.setCurrentSelectedView(imageRootView);
            }

            @Override
            public void onLongClick() {

            }
        });

        imageRootView.setOnTouchListener(mMultiTouchListener);
        clearHelperBox();
        addViewToParent(imageRootView, ViewType.IMAGE);
        mViewState.setCurrentSelectedView(imageRootView);
        return imageRootView;
    }

    private View getLayout(final ViewType viewType) {
        if (mRootView != null) {
            //We are setting tag as ViewType to identify what type of the view it is
            //when we remove the view from stack i.e onRemoveViewListener(ViewType viewType, int numberOfAddedViews);
            mRootView.setTag(viewType);
            final ImageView imgClose = mRootView.findViewById(R.id.imgPhotoEditorClose);
            if (imgClose != null) {
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewUndo(mRootView, viewType);
                    }
                });
            }
        }
        return mRootView;
    }

    public void clearHelperBox() {
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

    private void viewUndo(View removedView, ViewType viewType) {
        if (mViewState.containsAddedView(removedView)) {
            mPhotoEditorView.removeView(removedView);
            mViewState.removeAddedView(removedView);
            mViewState.pushRedoView(removedView);
            if (mOnPhotoEditorListener != null) {
                mOnPhotoEditorListener.onRemoveViewListener(
                        viewType,
                        mViewState.getAddedViewsCount()
                );
            }
        }
    }

    private void addViewToParent(View rootView, ViewType viewType) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mPhotoEditorView.addView(rootView, params);
        mViewState.addAddedView(rootView);
        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onAddViewListener(viewType, mViewState.getAddedViewsCount());
    }
}
