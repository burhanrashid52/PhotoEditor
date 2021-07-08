package ja.burhanrashid52.photoeditor;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
class Sticker extends Graphic {

    private final MultiTouchListener mMultiTouchListener;
    private final OnPhotoEditorListener mOnPhotoEditorListener;
    private final RelativeLayout mCanvasView;
    private final PhotoEditorViewState mViewState;
    private ImageView imageView;

    public Sticker(RelativeLayout canvasView,
                   PhotoEditorView photoEditorView,
                   MultiTouchListener multiTouchListener,
                   PhotoEditorViewState viewState,
                   OnPhotoEditorListener onPhotoEditorListener,
                   GraphicManager graphicManager
    ) {
        super(photoEditorView.getContext(), graphicManager);
        mCanvasView = canvasView;
        mViewState = viewState;
        mOnPhotoEditorListener = onPhotoEditorListener;
        mMultiTouchListener = multiTouchListener;
        setupGesture();
    }

    void buildView(Bitmap desiredImage) {
        imageView.setImageBitmap(desiredImage);
    }

    private void setupGesture() {
        MultiTouchListener.OnGestureControl onGestureControl = buildGestureController(
                mCanvasView,
                mViewState,
                mOnPhotoEditorListener
        );
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
