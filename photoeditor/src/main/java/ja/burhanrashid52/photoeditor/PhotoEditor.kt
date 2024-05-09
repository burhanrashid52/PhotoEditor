package ja.burhanrashid52.photoeditor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import androidx.annotation.IntRange
import androidx.annotation.RequiresPermission
import androidx.annotation.UiThread
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https:></https:>//github.com/burhanrashid52>
 */
interface PhotoEditor {
    /**
     * This will add image on [PhotoEditorView] which you drag,rotate and scale using pinch
     * if [PhotoEditor.Builder.setPinchTextScalable] enabled
     *
     * @param desiredImage bitmap image you want to add
     */
    fun addImage(desiredImage: Bitmap)

    /**
     * This add the text on the [PhotoEditorView] with provided parameters
     * by default [TextView.setText] will be 18sp
     *
     * @param text              text to display
     * @param colorCodeTextView text color to be displayed
     */
    @SuppressLint("ClickableViewAccessibility")
    fun addText(text: String, colorCodeTextView: Int)

    /**
     * This add the text on the [PhotoEditorView] with provided parameters
     * by default [TextView.setText] will be 18sp
     *
     * @param textTypeface      typeface for custom font in the text
     * @param text              text to display
     * @param colorCodeTextView text color to be displayed
     */
    @SuppressLint("ClickableViewAccessibility")
    fun addText(textTypeface: Typeface?, text: String, colorCodeTextView: Int)

    /**
     * This add the text on the [PhotoEditorView] with provided parameters
     * by default [TextView.setText] will be 18sp
     *
     * @param text         text to display
     * @param styleBuilder text style builder with your style
     */
    @SuppressLint("ClickableViewAccessibility")
    fun addText(text: String, styleBuilder: TextStyleBuilder?)

    /**
     * This will update text and color on provided view
     *
     * @param view      view on which you want update
     * @param inputText text to update [TextView]
     * @param colorCode color to update on [TextView]
     */
    fun editText(view: View, inputText: String, colorCode: Int)

    /**
     * This will update the text and color on provided view
     *
     * @param view         root view where text view is a child
     * @param textTypeface update typeface for custom font in the text
     * @param inputText    text to update [TextView]
     * @param colorCode    color to update on [TextView]
     */
    fun editText(view: View, textTypeface: Typeface?, inputText: String, colorCode: Int)

    /**
     * This will update the text and color on provided view
     *
     * @param view         root view where text view is a child
     * @param inputText    text to update [TextView]
     * @param styleBuilder style to apply on [TextView]
     */
    fun editText(view: View, inputText: String, styleBuilder: TextStyleBuilder?)

    /**
     * Adds emoji to the [PhotoEditorView] which you drag,rotate and scale using pinch
     * if [PhotoEditorImpl.Builder.setPinchTextScalable] enabled
     *
     * @param emojiName unicode in form of string to display emoji
     */
    fun addEmoji(emojiName: String)

    /**
     * Adds emoji to the [PhotoEditorView] which you drag,rotate and scale using pinch
     * if [PhotoEditorImpl.Builder.setPinchTextScalable] enabled
     *
     * @param emojiTypeface typeface for custom font to show emoji unicode in specific font
     * @param emojiName     unicode in form of string to display emoji
     */
    fun addEmoji(emojiTypeface: Typeface?, emojiName: String)

    /**
     * Enable/Disable drawing mode to draw on [PhotoEditorView]
     *
     * @param brushDrawingMode true if mode is enabled
     */
    fun setBrushDrawingMode(brushDrawingMode: Boolean)

    /**
     * @return true is brush mode is enabled
     */
    val brushDrawableMode: Boolean?

    /**
     * set opacity/transparency of brush while painting on [DrawingView]
     * @param opacity opacity is in form of percentage
     */
    @Deprecated(
        """use {@code setShape} of a ShapeBuilder
     
      """
    )
    fun setOpacity(@IntRange(from = 0, to = 100) opacity: Int)

    /**
     * set the eraser size
     * **Note :** Eraser size is different from the normal brush size
     *
     * @param brushEraserSize size of eraser
     */
    fun setBrushEraserSize(brushEraserSize: Float)

    /**
     * @return provide the size of eraser
     * @see PhotoEditor.setBrushEraserSize
     */
    val eraserSize: Float
    /**
     * @return provide the size of eraser
     * @see PhotoEditor.setBrushSize
     */
    /**
     * Set the size of brush user want to paint on canvas i.e [DrawingView]
     * @param size size of brush
     */
    @set:Deprecated(
        """use {@code setShape} of a ShapeBuilder
     
      """
    )
    var brushSize: Float
    /**
     * @return provide the size of eraser
     * @see PhotoEditor.setBrushColor
     */
    /**
     * set brush color which user want to paint
     * @param color color value for paint
     */
    @set:Deprecated(
        """use {@code setShape} of a ShapeBuilder
     
      """
    )
    var brushColor: Int

    /**
     *
     *
     * Its enables eraser mode after that whenever user drags on screen this will erase the existing
     * paint
     * <br></br>
     * **Note** : This eraser will work on paint views only
     *
     *
     */
    fun brushEraser()

    /**
     * Undo the last operation perform on the [PhotoEditor]
     *
     * @return true if there nothing more to undo
     */
    fun undo(): Boolean

    /**
     * Redo the last operation perform on the [PhotoEditor]
     *
     * @return true if there nothing more to redo
     */
    fun redo(): Boolean

    /**
     * How many redo operations are available.
     *
     * @return size of redo stack
     */
    val redoStackCount: Int

    /**
     * Removes all the edited operations performed [PhotoEditorView]
     * This will also clear the undo and redo stack
     */
    fun clearAllViews()

    /**
     * Remove all helper boxes from views
     */
    @UiThread
    fun clearHelperBox()

    /**
     * Setup of custom effect using effect type and set parameters values
     *
     * @param customEffect [CustomEffect.Builder.setParameter]
     */
    fun setFilterEffect(customEffect: CustomEffect?)

    /**
     * Set pre-define filter available
     *
     * @param filterType type of filter want to apply [PhotoEditorImpl]
     */
    fun setFilterEffect(filterType: PhotoFilter)

    /**
     * Save the edited image on given path
     *
     * @param imagePath      path on which image to be saved
     * @param saveSettings   builder for multiple save options [SaveSettings]
     */
    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    suspend fun saveAsFile(
        imagePath: String,
        saveSettings: SaveSettings = SaveSettings.Builder().build()
    ): SaveFileResult

    /**
     * Save the edited image as bitmap
     *
     * @param saveSettings builder for multiple save options [SaveSettings]
     */
    suspend fun saveAsBitmap(saveSettings: SaveSettings = SaveSettings.Builder().build()): Bitmap

    fun saveAsFile(imagePath: String, saveSettings: SaveSettings, onSaveListener: OnSaveListener)

    fun saveAsFile(imagePath: String, onSaveListener: OnSaveListener)

    fun saveAsBitmap(saveSettings: SaveSettings, onSaveBitmap: OnSaveBitmap)

    fun saveAsBitmap(onSaveBitmap: OnSaveBitmap)

    /**
     * Callback on editing operation perform on [PhotoEditorView]
     *
     * @param onPhotoEditorListener [OnPhotoEditorListener]
     */
    fun setOnPhotoEditorListener(onPhotoEditorListener: OnPhotoEditorListener)

    /**
     * Check if any changes made need to save
     *
     * @return true if nothing is there to change
     */
    val isCacheEmpty: Boolean

    /**
     * Builder pattern to define [PhotoEditor] Instance
     */
    class Builder(var context: Context, var photoEditorView: PhotoEditorView) {

        @JvmField
        var imageView: ImageView = photoEditorView.source

        @JvmField
        var deleteView: View? = null

        @JvmField
        var drawingView: DrawingView = photoEditorView.drawingView

        @JvmField
        var textTypeface: Typeface? = null

        @JvmField
        var emojiTypeface: Typeface? = null

        // By default, pinch-to-scale is enabled for text
        @JvmField
        var isTextPinchScalable = true

        @JvmField
        var clipSourceImage = false
        fun setDeleteView(deleteView: View?): Builder {
            this.deleteView = deleteView
            return this
        }

        /**
         * set default text font to be added on image
         *
         * @param textTypeface typeface for custom font
         * @return [Builder] instant to build [PhotoEditor]
         */
        fun setDefaultTextTypeface(textTypeface: Typeface?): Builder {
            this.textTypeface = textTypeface
            return this
        }

        /**
         * set default font specific to add emojis
         *
         * @param emojiTypeface typeface for custom font
         * @return [Builder] instant to build [PhotoEditor]
         */
        fun setDefaultEmojiTypeface(emojiTypeface: Typeface?): Builder {
            this.emojiTypeface = emojiTypeface
            return this
        }

        /**
         * Set false to disable pinch-to-scale for text inserts.
         * Set to "true" by default.
         *
         * @param isTextPinchScalable flag to make pinch to zoom for text inserts.
         * @return [Builder] instant to build [PhotoEditor]
         */
        fun setPinchTextScalable(isTextPinchScalable: Boolean): Builder {
            this.isTextPinchScalable = isTextPinchScalable
            return this
        }

        /**
         * @return build PhotoEditor instance
         */
        fun build(): PhotoEditor {
            return PhotoEditorImpl(this)
        }

        /**
         * Set true true to clip the drawing brush to the source image.
         *
         * @param clip a boolean to indicate if brush drawing is clipped or not.
         */
        fun setClipSourceImage(clip: Boolean): Builder {
            clipSourceImage = clip
            return this
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
        fun onSuccess(imagePath: String)

        /**
         * Call when failed to saved image on given path
         *
         * @param exception exception thrown while saving image
         */
        fun onFailure(exception: Exception)
    }

    // region Shape
    /**
     * Update the current shape to be drawn,
     * through the use of a ShapeBuilder.
     */
    fun setShape(shapeBuilder: ShapeBuilder) // endregion
}