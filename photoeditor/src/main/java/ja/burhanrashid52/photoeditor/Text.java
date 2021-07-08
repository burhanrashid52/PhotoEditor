package ja.burhanrashid52.photoeditor;

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.RelativeLayout;

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
class Text extends Graphic {

    private final MultiTouchListener mMultiTouchListener;
    private final Typeface mDefaultTextTypeface;
    private final GraphicManager mGraphicManager;
    private final OnPhotoEditorListener mOnPhotoEditorListener;
    private final RelativeLayout mCanvasView;
    private final PhotoEditorViewState mViewState;
    private TextView mTextView;

    public Text(RelativeLayout canvasView,
                PhotoEditorView photoEditorView,
                MultiTouchListener multiTouchListener,
                PhotoEditorViewState viewState,
                OnPhotoEditorListener onPhotoEditorListener,
                Typeface defaultTextTypeface,
                GraphicManager graphicManager
    ) {
        super(photoEditorView.getContext(), graphicManager);
        mCanvasView = canvasView;
        mViewState = viewState;
        mOnPhotoEditorListener = onPhotoEditorListener;
        mMultiTouchListener = multiTouchListener;
        mDefaultTextTypeface = defaultTextTypeface;
        mGraphicManager = graphicManager;
        setupGesture();
    }

    void buildView(String text, TextStyleBuilder styleBuilder) {
        mTextView.setText(text);
        if (styleBuilder != null)
            styleBuilder.applyStyle(mTextView);
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
        return ViewType.TEXT;
    }

    @Override
    int getLayoutId() {
        return R.layout.view_photo_editor_text;
    }

    @Override
    void setupView(View rootView) {
        mTextView = rootView.findViewById(R.id.tvPhotoEditorText);
        if (mTextView != null && mDefaultTextTypeface != null) {
            mTextView.setGravity(Gravity.CENTER);
            mTextView.setTypeface(mDefaultTextTypeface);
        }
    }

    @Override
    void updateView(View view) {
        String textInput = mTextView.getText().toString();
        int currentTextColor = mTextView.getCurrentTextColor();
        OnPhotoEditorListener photoEditorListener = mGraphicManager.getOnPhotoEditorListener();
        if (photoEditorListener != null) {
            photoEditorListener.onEditTextChangeListener(view, textInput, currentTextColor);
        }
    }
}
