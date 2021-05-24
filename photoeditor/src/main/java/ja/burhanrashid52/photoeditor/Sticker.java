package ja.burhanrashid52.photoeditor;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
class Sticker extends Graphic {

    private final MultiTouchListener mMultiTouchListener;
    private final ViewGroup mPhotoEditorView;
    private final PhotoEditorViewState mViewState;
    private ImageView imageView;

    public Sticker(ViewGroup photoEditorView,
                   MultiTouchListener multiTouchListener,
                   PhotoEditorViewState viewState,
                   GraphicManager graphicManager
    ) {
        super(photoEditorView.getContext(), graphicManager);
        mPhotoEditorView = photoEditorView;
        mViewState = viewState;
        mMultiTouchListener = multiTouchListener;
        setupGesture();
    }

    void buildView(Bitmap desiredImage) {
        imageView.setImageBitmap(desiredImage);
    }

    private void setupGesture() {
        MultiTouchListener.OnGestureControl onGestureControl = buildGestureController(mPhotoEditorView, mViewState);
        mMultiTouchListener.setOnGestureControl(onGestureControl);
        View rootView = getRootView();
        rootView.setOnTouchListener(mMultiTouchListener);
    }


    @Override
    ViewType getViewType() {
        return ViewType.IMAGE;
    }

    @Override
    int getLayoutId() {
        return R.layout.view_photo_editor_image;
    }

    @Override
    void setupView(View rootView) {
        imageView = rootView.findViewById(R.id.imgPhotoEditorImage);
    }
}
