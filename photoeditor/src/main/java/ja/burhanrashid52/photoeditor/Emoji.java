package ja.burhanrashid52.photoeditor;

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
class Emoji extends Graphic {

    private final MultiTouchListener mMultiTouchListener;
    private final Typeface mDefaultEmojiTypeface;
    private final OnPhotoEditorListener mOnPhotoEditorListener;
    private final RelativeLayout mCanvasView;
    private final PhotoEditorViewState mViewState;
    private TextView txtEmoji;

    public Emoji(RelativeLayout canvasView,
                 MultiTouchListener multiTouchListener,
                 PhotoEditorView photoEditorView,
                 PhotoEditorViewState viewState,
                 OnPhotoEditorListener onPhotoEditorListener,
                 GraphicManager graphicManager,
                 Typeface defaultEmojiTypeface
    ) {
        super(photoEditorView.getContext(), graphicManager);
        mCanvasView = canvasView;
        mOnPhotoEditorListener = onPhotoEditorListener;
        mViewState = viewState;
        mMultiTouchListener = multiTouchListener;
        mDefaultEmojiTypeface = defaultEmojiTypeface;
        setupGesture();
    }

    void buildView(Typeface emojiTypeface, String emojiName) {
        if (emojiTypeface != null) {
            txtEmoji.setTypeface(emojiTypeface);
        }
        txtEmoji.setTextSize(56);
        txtEmoji.setText(emojiName);
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
        return ViewType.EMOJI;
    }

    @Override
    int getLayoutId() {
        return R.layout.view_photo_editor_text;
    }

    @Override
    void setupView(View rootView) {
        txtEmoji = rootView.findViewById(R.id.tvPhotoEditorText);
        if (txtEmoji != null) {
            if (mDefaultEmojiTypeface != null) {
                txtEmoji.setTypeface(mDefaultEmojiTypeface);
            }
            txtEmoji.setGravity(Gravity.CENTER);
            txtEmoji.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }
}
