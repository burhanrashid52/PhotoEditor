package ja.burhanrashid52.photoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Burhanuddin Rashid on 18/01/2017.
 */

public class PhotoEditor implements BrushViewChangeListener {

    private static final String TAG = PhotoEditor.class.getSimpleName();
    private final LayoutInflater mLayoutInflater;
    private Context context;
    private RelativeLayout parentView;
    private ImageView imageView;
    private View deleteView;
    private BrushDrawingView brushDrawingView;
    private List<View> addedViews;
    private List<View> redoViews;
    private OnPhotoEditorListener mOnPhotoEditorListener;
    private boolean isTextPinchZoomable;
    private Typeface mDefaultTextTypeface;
    private Typeface mDefaultEmojiTypeface;


    private PhotoEditor(Builder builder) {
        this.context = builder.context;
        this.parentView = builder.parentView;
        this.imageView = builder.imageView;
        this.deleteView = builder.deleteView;
        this.brushDrawingView = builder.brushDrawingView;
        this.isTextPinchZoomable = builder.isTextPinchZoomable;
        this.mDefaultTextTypeface = builder.textTypeface;
        this.mDefaultEmojiTypeface = builder.emojiTypeface;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        brushDrawingView.setBrushViewChangeListener(this);
        addedViews = new ArrayList<>();
        redoViews = new ArrayList<>();
    }

    public void addImage(Bitmap desiredImage) {
        final View imageRootView = getLayout(ViewType.IMAGE);
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        final FrameLayout frmBorder = imageRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = imageRootView.findViewById(R.id.imgPhotoEditorClose);

        imageView.setImageBitmap(desiredImage);

        MultiTouchListener multiTouchListener = getMultiTouchListener();
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);
            }

            @Override
            public void onLongClick() {

            }
        });

        imageRootView.setOnTouchListener(multiTouchListener);

        addViewToParent(imageRootView, ViewType.IMAGE);

    }

    @SuppressLint("ClickableViewAccessibility")
    public void addText(String text, final int colorCodeTextView) {
        addText(null, text, colorCodeTextView);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addText(@Nullable Typeface textTypeface, String text, final int colorCodeTextView) {
        brushDrawingView.setBrushDrawingMode(false);
        final View textRootView = getLayout(ViewType.TEXT);
        final TextView textInputTv = textRootView.findViewById(R.id.tvPhotoEditorText);
        final ImageView imgClose = textRootView.findViewById(R.id.imgPhotoEditorClose);
        final FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);

        textInputTv.setText(text);
        textInputTv.setTextColor(colorCodeTextView);
        if (textTypeface != null) {
            textInputTv.setTypeface(textTypeface);
        }
        MultiTouchListener multiTouchListener = getMultiTouchListener();
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);
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

        textRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(textRootView, ViewType.TEXT);
    }


    public void editText(View view, String inputText, int colorCode) {
        editText(view, null, inputText, colorCode);
    }

    /**
     * This will update the text and color on provided view
     *
     * @param view         root view where text view is a child
     * @param textTypeface optional if provided
     * @param inputText    text to update textview
     * @param colorCode    color to update on textview
     */
    public void editText(View view, Typeface textTypeface, String inputText, int colorCode) {
        TextView inputTextView = view.findViewById(R.id.tvPhotoEditorText);
        if (inputTextView != null && addedViews.contains(view) && !TextUtils.isEmpty(inputText)) {
            inputTextView.setText(inputText);
            if (textTypeface != null) {
                inputTextView.setTypeface(textTypeface);
            }
            inputTextView.setTextColor(colorCode);
            parentView.updateViewLayout(view, view.getLayoutParams());
            int i = addedViews.indexOf(view);
            if (i > -1) addedViews.set(i, view);
        }
    }

    public void addEmoji(String emojiName) {
        addEmoji(null, emojiName);
    }

    public void addEmoji(Typeface emojiTypeface, String emojiName) {
        brushDrawingView.setBrushDrawingMode(false);
        final View emojiRootView = getLayout(ViewType.EMOJI);
        final TextView emojiTextView = emojiRootView.findViewById(R.id.tvPhotoEditorText);
        final FrameLayout frmBorder = emojiRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = emojiRootView.findViewById(R.id.imgPhotoEditorClose);

        if (emojiTypeface != null) {
            emojiTextView.setTypeface(emojiTypeface);
        }
        emojiTextView.setTextSize(56);
        emojiTextView.setText(emojiName);
        MultiTouchListener multiTouchListener = getMultiTouchListener();
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);
            }

            @Override
            public void onLongClick() {
            }
        });
        emojiRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(emojiRootView, ViewType.EMOJI);
    }


    /**
     * Add to root view from image,emoji and text to our parent view
     *
     * @param rootView rootview of image,text and emoji
     */
    private void addViewToParent(View rootView, ViewType viewType) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentView.addView(rootView, params);
        addedViews.add(rootView);
        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onAddViewListener(viewType, addedViews.size());
    }

    /**
     * Create a new instance and scalable touchview
     *
     * @return scalable multitouch listener
     */
    @NonNull
    private MultiTouchListener getMultiTouchListener() {
        MultiTouchListener multiTouchListener = new MultiTouchListener(
                deleteView,
                parentView,
                this.imageView,
                isTextPinchZoomable,
                mOnPhotoEditorListener);

        //multiTouchListener.setOnMultiTouchListener(this);

        return multiTouchListener;
    }

    /**
     * Get root view by its type i.e image,text and emoji
     *
     * @param viewType image,text or emoji
     * @return rootview
     */
    private View getLayout(ViewType viewType) {
        View rootView = null;
        switch (viewType) {
            case TEXT:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_text, null);
                TextView txtText = rootView.findViewById(R.id.tvPhotoEditorText);
                if (txtText != null && mDefaultTextTypeface != null) {
                    txtText.setGravity(Gravity.CENTER);
                    if (mDefaultEmojiTypeface != null) {
                        txtText.setTypeface(mDefaultTextTypeface);
                    }
                }
                break;
            case IMAGE:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_image, null);
                break;
            case EMOJI:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_text, null);
                TextView txtTextEmoji = rootView.findViewById(R.id.tvPhotoEditorText);
                if (txtTextEmoji != null) {
                    if (mDefaultEmojiTypeface != null) {
                        txtTextEmoji.setTypeface(mDefaultEmojiTypeface);
                    }
                    txtTextEmoji.setGravity(Gravity.CENTER);
                    txtTextEmoji.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                break;
        }

        if (rootView != null) {
            final ImageView imgClose = rootView.findViewById(R.id.imgPhotoEditorClose);
            final View finalRootView = rootView;
            if (imgClose != null) {
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewUndo(finalRootView);
                    }
                });
            }
        }
        return rootView;
    }

    public void setBrushDrawingMode(boolean brushDrawingMode) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushDrawingMode(brushDrawingMode);
    }

    public Boolean getBrushDrawableMode() {
        return brushDrawingView != null && brushDrawingView.getBrushDrawingMode();
    }

    public void setBrushSize(float size) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushSize(size);
    }

    public void setOpacity(@IntRange(from = 0, to = 100) int opacity) {
        if (brushDrawingView != null) {
            opacity = (int) ((opacity / 100.0) * 255.0);
            brushDrawingView.setOpacity(opacity);
        }
    }


    public void setBrushColor(@ColorInt int color) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushColor(color);
    }

    public void setBrushEraserSize(float brushEraserSize) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushEraserSize(brushEraserSize);
    }

    void setBrushEraserColor(@ColorInt int color) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushEraserColor(color);
    }

    public float getEraserSize() {
        return brushDrawingView != null ? brushDrawingView.getEraserSize() : 0;
    }

    public float getBrushSize() {
        if (brushDrawingView != null)
            return brushDrawingView.getBrushSize();
        return 0;
    }

    public int getBrushColor() {
        if (brushDrawingView != null)
            return brushDrawingView.getBrushColor();
        return 0;
    }

    public void brushEraser() {
        if (brushDrawingView != null)
            brushDrawingView.brushEraser();
    }

    private void viewUndo() {
        if (addedViews.size() > 0) {
            parentView.removeView(addedViews.remove(addedViews.size() - 1));
            if (mOnPhotoEditorListener != null)
                mOnPhotoEditorListener.onRemoveViewListener(addedViews.size());
        }
    }

    private void viewUndo(View removedView) {
        if (addedViews.size() > 0) {
            if (addedViews.contains(removedView)) {
                parentView.removeView(removedView);
                addedViews.remove(removedView);
                redoViews.add(removedView);
                if (mOnPhotoEditorListener != null)
                    mOnPhotoEditorListener.onRemoveViewListener(addedViews.size());
            }
        }
    }

    public boolean undo() {
        if (addedViews.size() > 0) {
            View removeView = addedViews.get(addedViews.size() - 1);
            if (removeView instanceof BrushDrawingView) {
                return brushDrawingView != null && brushDrawingView.undo();
            } else {
                addedViews.remove(addedViews.size() - 1);
                parentView.removeView(removeView);
                redoViews.add(removeView);
            }
            if (mOnPhotoEditorListener != null) {
                mOnPhotoEditorListener.onRemoveViewListener(addedViews.size());
            }
        }
        return addedViews.size() != 0;
    }

    public boolean redo() {
        if (redoViews.size() > 0) {
            View redoView = redoViews.get(redoViews.size() - 1);
            if (redoView instanceof BrushDrawingView) {
                return brushDrawingView != null && brushDrawingView.redo();
            } else {
                redoViews.remove(redoViews.size() - 1);
                parentView.addView(redoView);
                addedViews.add(redoView);
            }
        }
        return redoViews.size() != 0;
    }

    private void clearBrushAllViews() {
        if (brushDrawingView != null)
            brushDrawingView.clearAll();
    }

    public void clearAllViews() {
        for (int i = 0; i < addedViews.size(); i++) {
            parentView.removeView(addedViews.get(i));
        }
        if (addedViews.contains(brushDrawingView)) {
            parentView.addView(brushDrawingView);
        }
        addedViews.clear();
        redoViews.clear();
        clearBrushAllViews();
    }

    /**
     * Remove all helper boxes from text
     */
    @UiThread
    private void clearTextHelperBox() {
        for (int i = 0; i < parentView.getChildCount(); i++) {
            View childAt = parentView.getChildAt(i);
            FrameLayout frmBorder = childAt.findViewById(R.id.frmBorder);
            if (frmBorder != null) {
                frmBorder.setBackgroundResource(0);
            }
            ImageView imgClose = childAt.findViewById(R.id.imgPhotoEditorClose);
            if (imgClose != null) {
                imgClose.setVisibility(View.GONE);
            }
        }
    }

    public interface OnSaveListener {
        void onSuccess(@NonNull String imagePath);

        void onFailure(@NonNull Exception exception);
    }

    @SuppressLint("StaticFieldLeak")
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void saveImage(@NonNull final String imagePath, @NonNull final OnSaveListener onSaveListener) {
        Log.d(TAG, "Image Path: " + imagePath);
        new AsyncTask<String, String, Exception>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                clearTextHelperBox();
                parentView.setDrawingCacheEnabled(false);
            }

            @SuppressLint("MissingPermission")
            @Override
            protected Exception doInBackground(String... strings) {
                // Create a media file name
                File file = new File(imagePath);
                try {
                    FileOutputStream out = new FileOutputStream(file, false);
                    if (parentView != null) {
                        parentView.setDrawingCacheEnabled(true);
                        Bitmap drawingCache = parentView.getDrawingCache();
                        drawingCache.compress(Bitmap.CompressFormat.PNG, 100, out);
                    }
                    out.flush();
                    out.close();
                    Log.d(TAG, "Filed Saved Successfully");
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Failed to save File");
                    return e;
                }
            }

            @Override
            protected void onPostExecute(Exception e) {
                super.onPostExecute(e);
                if (e == null) {
                    clearAllViews();
                    onSaveListener.onSuccess(imagePath);
                } else {
                    onSaveListener.onFailure(e);
                }
            }

        }.execute();
    }

    private boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    private static String convertEmoji(String emoji) {
        String returnedEmoji;
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = getEmojiByUnicode(convertEmojiToInt);
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    private static String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public void setOnPhotoEditorListener(@NonNull OnPhotoEditorListener onPhotoEditorListener) {
        this.mOnPhotoEditorListener = onPhotoEditorListener;
    }

    /**
     * Check if any changes made need to save
     *
     * @return true is nothing is there to change
     */
    public boolean isCacheEmpty() {
        return addedViews.size() == 0 && redoViews.size() == 0;
    }


    @Override
    public void onViewAdd(BrushDrawingView brushDrawingView) {
        if (redoViews.size() > 0) {
            redoViews.remove(redoViews.size() - 1);
        }
        addedViews.add(brushDrawingView);
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onAddViewListener(ViewType.BRUSH_DRAWING, addedViews.size());
        }
    }

    @Override
    public void onViewRemoved(BrushDrawingView brushDrawingView) {
        if (addedViews.size() > 0) {
            View removeView = addedViews.remove(addedViews.size() - 1);
            if (!(removeView instanceof BrushDrawingView)) {
                parentView.removeView(removeView);
            }
            redoViews.add(removeView);
        }
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onRemoveViewListener(addedViews.size());
        }
    }

    @Override
    public void onStartDrawing() {
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onStartViewChangeListener(ViewType.BRUSH_DRAWING);
        }
    }

    @Override
    public void onStopDrawing() {
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onStopViewChangeListener(ViewType.BRUSH_DRAWING);
        }
    }

    public static class Builder {

        private Context context;
        private RelativeLayout parentView;
        private ImageView imageView;
        private View deleteView;
        private BrushDrawingView brushDrawingView;
        private Typeface textTypeface;
        private Typeface emojiTypeface;
        //By Default pinch zoom on text is enabled
        private boolean isTextPinchZoomable = true;

        public Builder(Context context, PhotoEditorView photoEditorView) {
            this.context = context;
            parentView = photoEditorView;
            imageView = photoEditorView.getSource();
            brushDrawingView = photoEditorView.getBrushDrawingView();
        }

        Builder setDeleteView(View deleteView) {
            this.deleteView = deleteView;
            return this;
        }

        public Builder setDefaultTextTypeface(Typeface textTypeface) {
            this.textTypeface = textTypeface;
            return this;
        }

        public Builder setDefaultEmojiTypeface(Typeface emojiTypeface) {
            this.emojiTypeface = emojiTypeface;
            return this;
        }

        public Builder setPinchTextScalable(boolean isTextPinchZoomable) {
            this.isTextPinchZoomable = isTextPinchZoomable;
            return this;
        }

        Builder setBrushDrawingView(BrushDrawingView brushDrawingView) {
            this.brushDrawingView = brushDrawingView;
            return this;
        }

        public PhotoEditor build() {
            return new PhotoEditor(this);
        }
    }

    public static ArrayList<String> getEmojis(Context context) {
        ArrayList<String> convertedEmojiList = new ArrayList<>();
        String[] emojiList = context.getResources().getStringArray(R.array.photo_editor_emoji);
        for (String emojiUnicode : emojiList) {
            convertedEmojiList.add(convertEmoji(emojiUnicode));
        }
        return convertedEmojiList;
    }
}
