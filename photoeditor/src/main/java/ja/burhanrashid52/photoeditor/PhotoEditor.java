package ja.burhanrashid52.photoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    /**
     * This will add image on {@link PhotoEditorView} which you drag,rotate and scale using pinch
     * if {@link PhotoEditor.Builder#setPinchTextScalable(boolean)} enabled
     *
     * @param desiredImage bitmap image you want to add
     */
    void addImage(Bitmap desiredImage);

    /**
     * This add the text on the {@link PhotoEditorView} with provided parameters
     * by default {@link TextView#setText(int)} will be 18sp
     *
     * @param text              text to display
     * @param colorCodeTextView text color to be displayed
     */
    @SuppressLint("ClickableViewAccessibility")
    void addText(String text, int colorCodeTextView);

    /**
     * This add the text on the {@link PhotoEditorView} with provided parameters
     * by default {@link TextView#setText(int)} will be 18sp
     *
     * @param textTypeface      typeface for custom font in the text
     * @param text              text to display
     * @param colorCodeTextView text color to be displayed
     */
    @SuppressLint("ClickableViewAccessibility")
    void addText(@Nullable Typeface textTypeface, String text, int colorCodeTextView);

    /**
     * This add the text on the {@link PhotoEditorView} with provided parameters
     * by default {@link TextView#setText(int)} will be 18sp
     *
     * @param text         text to display
     * @param styleBuilder text style builder with your style
     */
    @SuppressLint("ClickableViewAccessibility")
    void addText(String text, @Nullable TextStyleBuilder styleBuilder);

    /**
     * This will update text and color on provided view
     *
     * @param view      view on which you want update
     * @param inputText text to update {@link TextView}
     * @param colorCode color to update on {@link TextView}
     */
    void editText(@NonNull View view, String inputText, int colorCode);

    /**
     * This will update the text and color on provided view
     *
     * @param view         root view where text view is a child
     * @param textTypeface update typeface for custom font in the text
     * @param inputText    text to update {@link TextView}
     * @param colorCode    color to update on {@link TextView}
     */
    void editText(@NonNull View view, @Nullable Typeface textTypeface, String inputText, int colorCode);

    /**
     * This will update the text and color on provided view
     *
     * @param view         root view where text view is a child
     * @param inputText    text to update {@link TextView}
     * @param styleBuilder style to apply on {@link TextView}
     */
    void editText(@NonNull View view, String inputText, @Nullable TextStyleBuilder styleBuilder);

    /**
     * Adds emoji to the {@link PhotoEditorView} which you drag,rotate and scale using pinch
     * if {@link PhotoEditorImpl.Builder#setPinchTextScalable(boolean)} enabled
     *
     * @param emojiName unicode in form of string to display emoji
     */
    void addEmoji(String emojiName);

    /**
     * Adds emoji to the {@link PhotoEditorView} which you drag,rotate and scale using pinch
     * if {@link PhotoEditorImpl.Builder#setPinchTextScalable(boolean)} enabled
     *
     * @param emojiTypeface typeface for custom font to show emoji unicode in specific font
     * @param emojiName     unicode in form of string to display emoji
     */
    void addEmoji(Typeface emojiTypeface, String emojiName);

    /**
     * Enable/Disable drawing mode to draw on {@link PhotoEditorView}
     *
     * @param brushDrawingMode true if mode is enabled
     */
    void setBrushDrawingMode(boolean brushDrawingMode);

    /**
     * @return true is brush mode is enabled
     */
    Boolean getBrushDrawableMode();

    /**
     * set the size of brush user want to paint on canvas i.e {@link BrushDrawingView}
     *
     * @param size size of brush
     */
    void setBrushSize(float size);

    /**
     * set opacity/transparency of brush while painting on {@link BrushDrawingView}
     *
     * @param opacity opacity is in form of percentage
     */
    void setOpacity(@IntRange(from = 0, to = 100) int opacity);

    /**
     * set brush color which user want to paint
     *
     * @param color color value for paint
     */
    void setBrushColor(@ColorInt int color);

    /**
     * set the eraser size
     * <br></br>
     * <b>Note :</b> Eraser size is different from the normal brush size
     *
     * @param brushEraserSize size of eraser
     */
    void setBrushEraserSize(float brushEraserSize);

    /**
     * @return provide the size of eraser
     * @see PhotoEditor#setBrushEraserSize(float)
     */
    float getEraserSize();

    /**
     * @return provide the size of eraser
     * @see PhotoEditor#setBrushSize(float)
     */
    float getBrushSize();

    /**
     * @return provide the size of eraser
     * @see PhotoEditor#setBrushColor(int)
     */
    int getBrushColor();

    /**
     * <p>
     * Its enables eraser mode after that whenever user drags on screen this will erase the existing
     * paint
     * <br>
     * <b>Note</b> : This eraser will work on paint views only
     * <p>
     */
    void brushEraser();

    /**
     * Undo the last operation perform on the {@link PhotoEditor}
     *
     * @return true if there nothing more to undo
     */
    boolean undo();

    /**
     * Redo the last operation perform on the {@link PhotoEditor}
     *
     * @return true if there nothing more to redo
     */
    boolean redo();

    /**
     * Removes all the edited operations performed {@link PhotoEditorView}
     * This will also clear the undo and redo stack
     */
    void clearAllViews();

    /**
     * Remove all helper boxes from views
     */
    @UiThread
    void clearHelperBox();

    /**
     * Setup of custom effect using effect type and set parameters values
     *
     * @param customEffect {@link CustomEffect.Builder#setParameter(String, Object)}
     */
    void setFilterEffect(CustomEffect customEffect);


    /**
     * Set pre-define filter available
     *
     * @param filterType type of filter want to apply {@link PhotoEditorImpl}
     */
    void setFilterEffect(PhotoFilter filterType);

    /**
     * Save the edited image on given path
     *
     * @param imagePath      path on which image to be saved
     * @param onSaveListener callback for saving image
     * @see OnSaveListener
     */
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void saveAsFile(@NonNull String imagePath, @NonNull PhotoEditor.OnSaveListener onSaveListener);


    /**
     * Save the edited image on given path
     *
     * @param imagePath      path on which image to be saved
     * @param saveSettings   builder for multiple save options {@link SaveSettings}
     * @param onSaveListener callback for saving image
     * @see OnSaveListener
     */
    @SuppressLint("StaticFieldLeak")
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void saveAsFile(@NonNull String imagePath,
                    @NonNull SaveSettings saveSettings,
                    @NonNull PhotoEditor.OnSaveListener onSaveListener);


    /**
     * Save the edited image as bitmap
     *
     * @param onSaveBitmap callback for saving image as bitmap
     * @see OnSaveBitmap
     */
    @SuppressLint("StaticFieldLeak")
    void saveAsBitmap(@NonNull OnSaveBitmap onSaveBitmap);

    /**
     * Save the edited image as bitmap
     *
     * @param saveSettings builder for multiple save options {@link SaveSettings}
     * @param onSaveBitmap callback for saving image as bitmap
     * @see OnSaveBitmap
     */
    @SuppressLint("StaticFieldLeak")
    void saveAsBitmap(@NonNull SaveSettings saveSettings,
                      @NonNull OnSaveBitmap onSaveBitmap);

    /**
     * Callback on editing operation perform on {@link PhotoEditorView}
     *
     * @param onPhotoEditorListener {@link OnPhotoEditorListener}
     */
    void setOnPhotoEditorListener(@NonNull OnPhotoEditorListener onPhotoEditorListener);

    /**
     * Check if any changes made need to save
     *
     * @return true if nothing is there to change
     */
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
