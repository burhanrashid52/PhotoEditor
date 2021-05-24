package ja.burhanrashid52.photoeditor;

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
class Emoji extends Graphic {

    private final MultiTouchListener mMultiTouchListener;
    private final Typeface mDefaultEmojiTypeface;
    private final ViewGroup mPhotoEditorView;
    private final PhotoEditorViewState mViewState;
    private TextView txtEmoji;

    public Emoji(ViewGroup photoEditorView,
                 MultiTouchListener multiTouchListener,
                 PhotoEditorViewState viewState,
                 GraphicManager graphicManager,
                 Typeface defaultEmojiTypeface
    ) {
        super(photoEditorView.getContext(), graphicManager);
        mPhotoEditorView = photoEditorView;
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
        MultiTouchListener.OnGestureControl onGestureControl = buildGestureController(mPhotoEditorView, mViewState);
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
