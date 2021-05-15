package ja.burhanrashid52.photoeditor;

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
class Text extends Graphic {

    private final MultiTouchListener mMultiTouchListener;
    private final Typeface mDefaultTextTypeface;
    private @Nullable
    OnPhotoEditorListener mOnPhotoEditorListener;
    private final ViewGroup mPhotoEditorView;
    private final PhotoEditorViewState mViewState;
    private TextView txtText;

    public Text(ViewGroup photoEditorView,
                MultiTouchListener multiTouchListener,
                PhotoEditorViewState viewState,
                Typeface defaultTextTypeface,
                GraphicManager graphicManager
    ) {
        super(photoEditorView.getContext(), graphicManager);
        mPhotoEditorView = photoEditorView;
        mViewState = viewState;
        mMultiTouchListener = multiTouchListener;
        mDefaultTextTypeface = defaultTextTypeface;
    }

    public void setOnPhotoEditorListener(@Nullable OnPhotoEditorListener onPhotoEditorListener) {
        mOnPhotoEditorListener = onPhotoEditorListener;
    }

    void buildView(String text, TextStyleBuilder styleBuilder) {

        txtText.setText(text);
        if (styleBuilder != null)
            styleBuilder.applyStyle(txtText);

        MultiTouchListener.OnGestureControl onGestureControl = buildGestureController(mPhotoEditorView, mViewState);
        mMultiTouchListener.setOnGestureControl(onGestureControl);

        mRootView.setOnTouchListener(mMultiTouchListener);
        clearHelperBox(mPhotoEditorView, mViewState);
        addViewToParent();

        // Change the in-focus view
        mViewState.setCurrentSelectedView(mRootView);
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
        txtText = mRootView.findViewById(R.id.tvPhotoEditorText);
        if (txtText != null && mDefaultTextTypeface != null) {
            txtText.setGravity(Gravity.CENTER);
            txtText.setTypeface(mDefaultTextTypeface);
        }
    }

    @Override
    void updateView(View view) {
        String textInput = txtText.getText().toString();
        int currentTextColor = txtText.getCurrentTextColor();
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onEditTextChangeListener(view, textInput, currentTextColor);
        }
    }
}
