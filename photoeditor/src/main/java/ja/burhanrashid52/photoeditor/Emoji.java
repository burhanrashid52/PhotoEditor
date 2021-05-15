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

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
class Emoji extends Graphic {

    private final MultiTouchListener mMultiTouchListener;
    private final View mRootView;
    private final Typeface mDefaultEmojiTypeface;
    private final ViewGroup mPhotoEditorView;
    private final PhotoEditorViewState mViewState;


    public Emoji(ViewGroup photoEditorView,
                 MultiTouchListener multiTouchListener,
                 PhotoEditorViewState viewState,
                 GraphicManager graphicManager,
                 Typeface defaultEmojiTypeface
    ) {
        super(graphicManager);
        mPhotoEditorView = photoEditorView;
        mViewState = viewState;
        Context context = photoEditorView.getContext();
        mMultiTouchListener = multiTouchListener;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = layoutInflater.inflate(R.layout.view_photo_editor_text, null);
        this.mDefaultEmojiTypeface = defaultEmojiTypeface;
    }

    View buildView(Typeface emojiTypeface, String emojiName) {
        final View emojiRootView = getLayout();
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
                clearHelperBox(Emoji.this.mPhotoEditorView, Emoji.this.mViewState);
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
        clearHelperBox(mPhotoEditorView, mViewState);
        addViewToParent(emojiRootView);

        // Change the in-focus view
        mViewState.setCurrentSelectedView(emojiRootView);
        return emojiRootView;
    }

    private View getLayout() {
        final ViewType viewType = ViewType.EMOJI;
        TextView txtTextEmoji = mRootView.findViewById(R.id.tvPhotoEditorText);
        if (txtTextEmoji != null) {
            if (mDefaultEmojiTypeface != null) {
                txtTextEmoji.setTypeface(mDefaultEmojiTypeface);
            }
            txtTextEmoji.setGravity(Gravity.CENTER);
            txtTextEmoji.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

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
        return mRootView;
    }

    @Override
    ViewType getViewType() {
        return ViewType.EMOJI;
    }

}
