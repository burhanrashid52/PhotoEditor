package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
class Text extends Graphic {

    private final MultiTouchListener mMultiTouchListener;
    private final View mRootView;
    private final Typeface mDefaultTextTypeface;
    private @Nullable
    OnPhotoEditorListener mOnPhotoEditorListener;

    public Text(ViewGroup photoEditorView,
                MultiTouchListener multiTouchListener,
                PhotoEditorViewState viewState,
                Typeface defaultTextTypeface
    ) {
        super(photoEditorView, viewState);
        Context context = photoEditorView.getContext();
        mMultiTouchListener = multiTouchListener;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = layoutInflater.inflate(R.layout.view_photo_editor_text, null);
        this.mDefaultTextTypeface = defaultTextTypeface;
    }

    public void setOnPhotoEditorListener(@Nullable OnPhotoEditorListener onPhotoEditorListener) {
        mOnPhotoEditorListener = onPhotoEditorListener;
    }

    View buildView(String text, TextStyleBuilder styleBuilder) {
        final View textRootView = getLayout();
        final TextView textInputTv = textRootView.findViewById(R.id.tvPhotoEditorText);
        final ImageView imgClose = textRootView.findViewById(R.id.imgPhotoEditorClose);
        final FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);

        textInputTv.setText(text);
        if (styleBuilder != null)
            styleBuilder.applyStyle(textInputTv);

        mMultiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                clearHelperBox();
                frmBorder.setBackgroundResource(R.drawable.rounded_border_tv);
                imgClose.setVisibility(View.VISIBLE);
                frmBorder.setTag(true);
                mViewState.setCurrentSelectedView(textRootView);
            }

            @Override
            public void onLongClick() {
                String textInput = textInputTv.getText().toString();
                int currentTextColor = textInputTv.getCurrentTextColor();
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onEditTextChangeListener(textRootView, textInput, currentTextColor);
                }
            }
        });

        textRootView.setOnTouchListener(mMultiTouchListener);
        clearHelperBox();
        addViewToParent(textRootView);

        // Change the in-focus view
        mViewState.setCurrentSelectedView(textRootView);
        return textRootView;
    }

    private View getLayout() {
        final ViewType viewType = ViewType.TEXT;
        TextView txtText = mRootView.findViewById(R.id.tvPhotoEditorText);
        if (txtText != null && mDefaultTextTypeface != null) {
            txtText.setGravity(Gravity.CENTER);
            if (mDefaultTextTypeface != null) {
                txtText.setTypeface(mDefaultTextTypeface);
            }
        }

        if (mRootView != null) {
            //We are setting tag as ViewType to identify what type of the view it is
            //when we remove the view from stack i.e onRemoveViewListener(ViewType viewType, int numberOfAddedViews);
            mRootView.setTag(viewType);
            final ImageView imgClose = mRootView.findViewById(R.id.imgPhotoEditorClose);
            final View finalRootView = mRootView;
            if (imgClose != null) {
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewUndo(finalRootView);
                    }
                });
            }
        }
        return mRootView;
    }


    @Override
    ViewType getViewType() {
        return ViewType.TEXT;
    }

    @Override
    OnPhotoEditorListener getOnPhotoEditorListener() {
        return mOnPhotoEditorListener;
    }
}
