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
class Emoji extends Graphic {

    private final MultiTouchListener mMultiTouchListener;
    private final View mRootView;
    private final Typeface mDefaultEmojiTypeface;
    private @Nullable
    OnPhotoEditorListener mOnPhotoEditorListener;


    public Emoji(ViewGroup photoEditorView,
                 MultiTouchListener multiTouchListener,
                 PhotoEditorViewState viewState,
                 Typeface defaultEmojiTypeface
    ) {
        super(photoEditorView, viewState);
        Context context = photoEditorView.getContext();
        mMultiTouchListener = multiTouchListener;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = layoutInflater.inflate(R.layout.view_photo_editor_text, null);
        this.mDefaultEmojiTypeface = defaultEmojiTypeface;
    }

    public void setOnPhotoEditorListener(@Nullable OnPhotoEditorListener onPhotoEditorListener) {
        mOnPhotoEditorListener = onPhotoEditorListener;
    }

    View buildView(Typeface emojiTypeface, String emojiName) {
        final View emojiRootView = getLayout(emojiTypeface, emojiName);
        final TextView emojiTextView = emojiRootView.findViewById(R.id.tvPhotoEditorText);
        final FrameLayout frmBorder = emojiRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = emojiRootView.findViewById(R.id.imgPhotoEditorClose);

        if (emojiTypeface != null) {
            emojiTextView.setTypeface(emojiTypeface);
        }
        emojiTextView.setTextSize(56);
        emojiTextView.setText(emojiName);
        mMultiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                clearHelperBox();
                frmBorder.setBackgroundResource(R.drawable.rounded_border_tv);
                imgClose.setVisibility(View.VISIBLE);
                frmBorder.setTag(true);

                // Change the in-focus view
                mViewState.setCurrentSelectedView(emojiRootView);
            }

            @Override
            public void onLongClick() {
            }
        });
        emojiRootView.setOnTouchListener(mMultiTouchListener);
        clearHelperBox();
        addViewToParent(emojiRootView);

        // Change the in-focus view
        mViewState.setCurrentSelectedView(emojiRootView);
        return emojiRootView;
    }

    private View getLayout(Typeface emojiTypeface, String emojiName) {
        final ViewType viewType = ViewType.EMOJI;
        TextView txtTextEmoji = mRootView.findViewById(R.id.tvPhotoEditorText);
        if (txtTextEmoji != null) {
            if (mDefaultEmojiTypeface != null) {
                txtTextEmoji.setTypeface(mDefaultEmojiTypeface);
            }
            txtTextEmoji.setGravity(Gravity.CENTER);
            txtTextEmoji.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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
        return ViewType.EMOJI;
    }

    @Override
    OnPhotoEditorListener getOnPhotoEditorListener() {
        return mOnPhotoEditorListener;
    }
}
