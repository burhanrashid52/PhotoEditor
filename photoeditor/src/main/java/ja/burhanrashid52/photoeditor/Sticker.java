package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.graphics.Bitmap;
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
class Sticker extends Graphic {

    private final MultiTouchListener mMultiTouchListener;
    private final View mRootView;
    private final ViewGroup mPhotoEditorView;
    private final PhotoEditorViewState mViewState;

    public Sticker(ViewGroup photoEditorView,
                   MultiTouchListener multiTouchListener,
                   PhotoEditorViewState viewState,
                   GraphicManager graphicManager
    ) {
        super(graphicManager);
        mPhotoEditorView = photoEditorView;
        mViewState = viewState;
        Context context = photoEditorView.getContext();
        mMultiTouchListener = multiTouchListener;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = layoutInflater.inflate(R.layout.view_photo_editor_image, null);
    }

    View buildView(Bitmap desiredImage) {

        final View imageRootView = getLayout();
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        final FrameLayout frmBorder = imageRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = imageRootView.findViewById(R.id.imgPhotoEditorClose);

        imageView.setImageBitmap(desiredImage);

        mMultiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                clearHelperBox(Sticker.this.mPhotoEditorView, Sticker.this.mViewState);
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
        clearHelperBox(mPhotoEditorView, mViewState);
        addViewToParent(imageRootView);
        mViewState.setCurrentSelectedView(imageRootView);
        return imageRootView;
    }

    private View getLayout() {
        final ViewType viewType = ViewType.IMAGE;
        if (mRootView != null) {
            //We are setting tag as ViewType to identify what type of the view it is
            //when we remove the view from stack i.e onRemoveViewListener(ViewType viewType, int numberOfAddedViews);
            mRootView.setTag(viewType);
            final ImageView imgClose = mRootView.findViewById(R.id.imgPhotoEditorClose);
            if (imgClose != null) {
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewUndo(mRootView);
                    }
                });
            }
        }
        return mRootView;
    }


    @Override
    ViewType getViewType() {
        return ViewType.IMAGE;
    }

}
