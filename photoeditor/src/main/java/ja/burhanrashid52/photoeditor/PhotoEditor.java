package ja.burhanrashid52.photoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.UiThread;

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
public interface PhotoEditor {
    void addImage(Bitmap desiredImage);

    @SuppressLint("ClickableViewAccessibility")
    void addText(String text, int colorCodeTextView);

    @SuppressLint("ClickableViewAccessibility")
    void addText(@Nullable Typeface textTypeface, String text, int colorCodeTextView);

    @SuppressLint("ClickableViewAccessibility")
    void addText(String text, @Nullable TextStyleBuilder styleBuilder);

    void editText(@NonNull View view, String inputText, @NonNull int colorCode);

    void editText(@NonNull View view, @Nullable Typeface textTypeface, String inputText, @NonNull int colorCode);

    void editText(@NonNull View view, String inputText, @Nullable TextStyleBuilder styleBuilder);

    void addEmoji(String emojiName);

    void addEmoji(Typeface emojiTypeface, String emojiName);

    void setBrushDrawingMode(boolean brushDrawingMode);

    Boolean getBrushDrawableMode();

    void setBrushSize(float size);

    void setOpacity(@IntRange(from = 0, to = 100) int opacity);

    void setBrushColor(@ColorInt int color);

    void setBrushEraserSize(float brushEraserSize);

    float getEraserSize();

    float getBrushSize();

    int getBrushColor();

    void brushEraser();

    boolean undo();

    boolean redo();

    void clearAllViews();

    @UiThread
    void clearHelperBox();

    void setFilterEffect(CustomEffect customEffect);

    void setFilterEffect(PhotoFilter filterType);

    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void saveAsFile(@NonNull String imagePath, @NonNull PhotoEditor.OnSaveListener onSaveListener);

    @SuppressLint("StaticFieldLeak")
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void saveAsFile(@NonNull String imagePath,
                    @NonNull SaveSettings saveSettings,
                    @NonNull PhotoEditor.OnSaveListener onSaveListener);

    @SuppressLint("StaticFieldLeak")
    void saveAsBitmap(@NonNull OnSaveBitmap onSaveBitmap);

    @SuppressLint("StaticFieldLeak")
    void saveAsBitmap(@NonNull SaveSettings saveSettings,
                      @NonNull OnSaveBitmap onSaveBitmap);

    void setOnPhotoEditorListener(@NonNull OnPhotoEditorListener onPhotoEditorListener);

    boolean isCacheEmpty();

    /**
     * Builder pattern to define {@link PhotoEditor} Instance
     */
    class Builder {

        Context context;
        PhotoEditorView parentView;
        ImageView imageView;
        View deleteView;
        BrushDrawingView brushDrawingView;
        Typeface textTypeface;
        Typeface emojiTypeface;
        // By default, pinch-to-scale is enabled for text
        boolean isTextPinchScalable = true;

        /**
         * Building a PhotoEditor which requires a Context and PhotoEditorView
         * which we have setup in our xml layout
         *
         * @param context         context
         * @param photoEditorView {@link PhotoEditorView}
         */
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

        /**
         * set default text font to be added on image
         *
         * @param textTypeface typeface for custom font
         * @return {@link Builder} instant to build {@link PhotoEditor}
         */
        public Builder setDefaultTextTypeface(Typeface textTypeface) {
            this.textTypeface = textTypeface;
            return this;
        }

        /**
         * set default font specific to add emojis
         *
         * @param emojiTypeface typeface for custom font
         * @return {@link Builder} instant to build {@link PhotoEditor}
         */
        public Builder setDefaultEmojiTypeface(Typeface emojiTypeface) {
            this.emojiTypeface = emojiTypeface;
            return this;
        }

        /**
         * Set false to disable pinch-to-scale for text inserts.
         * Set to "true" by default.
         *
         * @param isTextPinchScalable flag to make pinch to zoom for text inserts.
         * @return {@link Builder} instant to build {@link PhotoEditor}
         */
        public Builder setPinchTextScalable(boolean isTextPinchScalable) {
            this.isTextPinchScalable = isTextPinchScalable;
            return this;
        }

        /**
         * @return build PhotoEditor instance
         */
        public PhotoEditor build() {
            return new PhotoEditorImpl(this);
        }
    }


    /**
     * A callback to save the edited image asynchronously
     */
    interface OnSaveListener {

        /**
         * Call when edited image is saved successfully on given path
         *
         * @param imagePath path on which image is saved
         */
        void onSuccess(@NonNull String imagePath);

        /**
         * Call when failed to saved image on given path
         *
         * @param exception exception thrown while saving image
         */
        void onFailure(@NonNull Exception exception);
    }
}
