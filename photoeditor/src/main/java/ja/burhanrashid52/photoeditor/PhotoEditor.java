package ja.burhanrashid52.photoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.RequiresPermission;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class PhotoEditor implements MultiTouchListener.OnMultiTouchListener, BrushViewChangeListener {

    private static final String TAG = PhotoEditor.class.getSimpleName();
    private Context context;
    private RelativeLayout parentView;
    private ImageView imageView;
    private View deleteView;
    private BrushDrawingView brushDrawingView;
    private List<View> addedViews;
    private List<View> redoViews;
    private OnPhotoEditorListener mOnPhotoEditorListener;
    private boolean isTextPinchZoomable;
    private boolean mIsBackground = false;


    private PhotoEditor(Builder builder) {
        this.context = builder.context;
        this.parentView = builder.parentView;
        this.imageView = builder.imageView;
        this.deleteView = builder.deleteView;
        this.brushDrawingView = builder.brushDrawingView;
        this.isTextPinchZoomable = builder.isTextPinchZoomable;
        brushDrawingView.setBrushViewChangeListener(this);
        addedViews = new ArrayList<>();
        redoViews = new ArrayList<>();
    }

    public void addImage(Bitmap desiredImage) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View imageRootView = inflater.inflate(R.layout.photo_editor_sdk_image_item_list, null);
        ImageView imageView = imageRootView.findViewById(R.id.photo_editor_sdk_image_iv);
        imageView.setImageBitmap(desiredImage);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        MultiTouchListener multiTouchListener = new MultiTouchListener(
                deleteView,
                parentView,
                this.imageView,
                isTextPinchZoomable,
                mOnPhotoEditorListener
        );
        multiTouchListener.setOnMultiTouchListener(this);
        imageRootView.setOnTouchListener(multiTouchListener);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentView.addView(imageRootView, params);
        addedViews.add(imageRootView);
        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onAddViewListener(ViewType.IMAGE, addedViews.size());
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addText(String text, final int colorCodeTextView) {
        mIsBackground = true;
        brushDrawingView.setBrushDrawingMode(false);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addTextRootView = inflater.inflate(R.layout.photo_editor_sdk_text_item_list, null);
        final TextView textInputTv = addTextRootView.findViewById(R.id.photo_editor_sdk_text_tv);
        final ImageView imgClose = addTextRootView.findViewById(R.id.imgClose);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUndo(addTextRootView);
            }
        });

        textInputTv.setGravity(Gravity.CENTER);
        textInputTv.setText(text);
        textInputTv.setTextColor(colorCodeTextView);
        MultiTouchListener multiTouchListener = new MultiTouchListener(
                deleteView,
                parentView,
                this.imageView,
                isTextPinchZoomable,
                mOnPhotoEditorListener);
        multiTouchListener.setOnMultiTouchListener(this);
        textInputTv.setBackgroundResource(R.drawable.rounded_border_tv);
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                textInputTv.setBackgroundResource(mIsBackground ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(mIsBackground ? View.GONE : View.VISIBLE);
                mIsBackground = !mIsBackground;
            }

            @Override
            public void onLongClick() {
                String textInput = textInputTv.getText().toString();
                int currentTextColor = textInputTv.getCurrentTextColor();
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onEditTextChangeListener(addTextRootView, textInput, currentTextColor);
                }
            }
        });

        addTextRootView.setOnTouchListener(multiTouchListener);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentView.addView(addTextRootView, params);
        addedViews.add(addTextRootView);
        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onAddViewListener(ViewType.TEXT, addedViews.size());
    }

    /**
     * This will update the text and color on provided view
     *
     * @param view      root view where text view is a child
     * @param inputText text to update textview
     * @param colorCode color to update on textview
     */
    public void editText(View view, String inputText, int colorCode) {
        TextView inputTextView = view.findViewById(R.id.photo_editor_sdk_text_tv);
        if (inputTextView != null && addedViews.contains(view) && !TextUtils.isEmpty(inputText)) {
            inputTextView.setText(inputText);
            inputTextView.setTextColor(colorCode);
            parentView.updateViewLayout(view, view.getLayoutParams());
            int i = addedViews.indexOf(view);
            if (i > -1) addedViews.set(i, view);
        }
    }

    public void addEmoji(String emojiName, Typeface emojiFont) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View emojiRootView = inflater.inflate(R.layout.photo_editor_sdk_text_item_list, null);
        TextView emojiTextView = emojiRootView.findViewById(R.id.photo_editor_sdk_text_tv);
        emojiTextView.setTypeface(emojiFont);
        emojiTextView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    //    emojiTextView.setText(convertEmoji(emojiName));
        MultiTouchListener multiTouchListener = new MultiTouchListener(
                deleteView,
                parentView,
                this.imageView,
                isTextPinchZoomable,
                mOnPhotoEditorListener);
        multiTouchListener.setOnMultiTouchListener(this);
        emojiRootView.setOnTouchListener(multiTouchListener);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentView.addView(emojiRootView, params);
        addedViews.add(emojiRootView);
        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onAddViewListener(ViewType.EMOJI, addedViews.size());
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

    public void setBrushEraserColor(@ColorInt int color) {
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

    public void viewUndo() {
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
            TextView txtInput = childAt.findViewById(R.id.photo_editor_sdk_text_tv);
            if (txtInput != null) {
                txtInput.setBackgroundResource(0);
            }
            ImageView imgClose = childAt.findViewById(R.id.imgClose);
            if (imgClose != null) {
                imgClose.setVisibility(View.GONE);
            }
        }
    }


    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void saveImage(String imagePath) {
        Log.d(TAG, "Image Path: " + imagePath);

        clearTextHelperBox();
        parentView.setDrawingCacheEnabled(false);
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
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to save File");
        }
    }

    private boolean isSDCARDMounted() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    private String convertEmoji(String emoji) {
        String returnedEmoji;
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = getEmojiByUnicode(convertEmojiToInt);
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public void setOnPhotoEditorListener(OnPhotoEditorListener onPhotoEditorListener) {
        this.mOnPhotoEditorListener = onPhotoEditorListener;
    }

    public boolean isCacheEmpty() {
        return addedViews.size() == 0 && redoViews.size() == 0;
    }

    @Override
    public void onEditTextClickListener(String text, int colorCode) {
       /* if (addTextRootView != null) {
            parentView.removeView(addTextRootView);
            addedViews.remove(addTextRootView);
        }*/
    }

    @Override
    public void onRemoveViewListener(View removedView) {
        //viewUndo(removedView);
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

    public static class Builder {

        private Context context;
        private RelativeLayout parentView;
        private ImageView imageView;
        private View deleteView;
        private BrushDrawingView brushDrawingView;
        //By Default pinch zoom on text is enabled
        private boolean isTextPinchZoomable = true;

        public Builder(Context context, PhotoEditorView photoEditorView) {
            this.context = context;
            parentView = photoEditorView.getParentLayout();
            imageView = photoEditorView.getImageSource();
            brushDrawingView = photoEditorView.getBrushDrawingView();
        }

        Builder setParentView(RelativeLayout parentView) {
            this.parentView = parentView;
            return this;
        }

        Builder setChildView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public Builder setDeleteView(View deleteView) {
            this.deleteView = deleteView;
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
}
