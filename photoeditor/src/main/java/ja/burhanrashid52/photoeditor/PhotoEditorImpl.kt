package ja.burhanrashid52.photoeditor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.text.TextUtils
import android.util.Log
import android.view.GestureDetector
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.annotation.RequiresPermission
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import ja.burhanrashid52.photoeditor.PhotoEditorImageViewListener.OnSingleTapUpCallback
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder

/**
 *
 *
 * This class in initialize by [PhotoEditor.Builder] using a builder pattern with multiple
 * editing attributes
 *
 *
 * @author [Burhanuddin Rashid](https://github.com/burhanrashid52)
 * @version 0.1.1
 * @since 18/01/2017
 */
internal class PhotoEditorImpl @SuppressLint("ClickableViewAccessibility") constructor(
    builder: PhotoEditor.Builder
) : PhotoEditor {
    private val photoEditorView: PhotoEditorView = builder.photoEditorView
    private val viewState: PhotoEditorViewState = PhotoEditorViewState()
    private val imageView: ImageView? = builder.imageView
    private val deleteView: View? = builder.deleteView
    private val drawingView: DrawingView? = builder.drawingView
    private val mBrushDrawingStateListener: BrushDrawingStateListener =
        BrushDrawingStateListener(builder.photoEditorView, viewState)
    private val mBoxHelper: BoxHelper = BoxHelper(builder.photoEditorView, viewState)
    private var mOnPhotoEditorListener: OnPhotoEditorListener? = null
    private val isTextPinchScalable: Boolean = builder.isTextPinchScalable
    private val mDefaultTextTypeface: Typeface? = builder.textTypeface
    private val mDefaultEmojiTypeface: Typeface? = builder.emojiTypeface
    private val mGraphicManager: GraphicManager = GraphicManager(builder.photoEditorView, viewState)
    private val context: Context = builder.context

    override fun addImage(desiredImage: Bitmap?) {
        val multiTouchListener = getMultiTouchListener(true)
        val sticker = Sticker(photoEditorView, multiTouchListener, viewState, mGraphicManager)
        sticker.buildView(desiredImage)
        addToEditor(sticker)
    }

    override fun addText(text: String?, colorCodeTextView: Int) {
        addText(null, text, colorCodeTextView)
    }

    override fun addText(textTypeface: Typeface?, text: String?, colorCodeTextView: Int) {
        val styleBuilder = TextStyleBuilder()
        styleBuilder.withTextColor(colorCodeTextView)
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface)
        }
        addText(text, styleBuilder)
    }

    override fun addText(text: String?, styleBuilder: TextStyleBuilder?) {
        drawingView?.enableDrawing(false)
        val multiTouchListener = getMultiTouchListener(isTextPinchScalable)
        val textGraphic =
            Text(photoEditorView, multiTouchListener, viewState, mDefaultTextTypeface, mGraphicManager)
        textGraphic.buildView(text, styleBuilder)
        addToEditor(textGraphic)
    }

    override fun editText(view: View, inputText: String?, colorCode: Int) {
        editText(view, null, inputText, colorCode)
    }

    override fun editText(view: View, textTypeface: Typeface?, inputText: String?, colorCode: Int) {
        val styleBuilder = TextStyleBuilder()
        styleBuilder.withTextColor(colorCode)
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface)
        }
        editText(view, inputText, styleBuilder)
    }

    override fun editText(view: View, inputText: String?, styleBuilder: TextStyleBuilder?) {
        val inputTextView = view.findViewById<TextView>(R.id.tvPhotoEditorText)
        if (inputTextView != null && viewState.containsAddedView(view) && !TextUtils.isEmpty(
                inputText
            )
        ) {
            inputTextView.text = inputText
            styleBuilder?.applyStyle(inputTextView)
            mGraphicManager.updateView(view)
        }
    }

    override fun addEmoji(emojiName: String?) {
        addEmoji(null, emojiName)
    }

    override fun addEmoji(emojiTypeface: Typeface?, emojiName: String?) {
        drawingView?.enableDrawing(false)
        val multiTouchListener = getMultiTouchListener(true)
        val emoji =
            Emoji(photoEditorView, multiTouchListener, viewState, mGraphicManager, mDefaultEmojiTypeface)
        emoji.buildView(emojiTypeface, emojiName)
        addToEditor(emoji)
    }

    private fun addToEditor(graphic: Graphic) {
        clearHelperBox()
        mGraphicManager.addView(graphic)
        // Change the in-focus view
        viewState.currentSelectedView = graphic.rootView
    }

    /**
     * Create a new instance and scalable touchview
     *
     * @param isPinchScalable true if make pinch-scalable, false otherwise.
     * @return scalable multitouch listener
     */
    private fun getMultiTouchListener(isPinchScalable: Boolean): MultiTouchListener {
        return MultiTouchListener(
            deleteView,
            photoEditorView,
            imageView,
            isPinchScalable,
            mOnPhotoEditorListener,
            viewState
        )
    }

    override fun setBrushDrawingMode(brushDrawingMode: Boolean) {
        drawingView?.enableDrawing(brushDrawingMode)
    }

    override val brushDrawableMode: Boolean
        get() = drawingView != null && drawingView.isDrawingEnabled

    override fun setOpacity(@IntRange(from = 0, to = 100) opacity: Int) {
        var opacityValue = opacity
        opacityValue = (opacityValue / 100.0 * 255.0).toInt()
        drawingView?.currentShapeBuilder?.withShapeOpacity(opacityValue)
    }

    override var brushSize: Float
        get() = drawingView?.currentShapeBuilder?.shapeSize ?: 0f
        set(size) {
            drawingView?.currentShapeBuilder?.withShapeSize(size)
        }
    override var brushColor: Int
        get() = drawingView?.currentShapeBuilder?.shapeColor ?: 0
        set(color) {
            drawingView?.currentShapeBuilder?.withShapeColor(color)
        }

    override fun setBrushEraserSize(brushEraserSize: Float) {
        drawingView?.eraserSize = brushEraserSize
    }

    override val eraserSize: Float
        get() = drawingView?.eraserSize ?: 0f

    override fun brushEraser() {
        drawingView?.brushEraser()
    }

    override fun undo(): Boolean {
        return mGraphicManager.undoView()
    }

    override fun redo(): Boolean {
        return mGraphicManager.redoView()
    }

    override fun clearAllViews() {
        mBoxHelper.clearAllViews(drawingView)
    }

    override fun clearHelperBox() {
        mBoxHelper.clearHelperBox()
    }

    override fun setFilterEffect(customEffect: CustomEffect?) {
        photoEditorView.setFilterEffect(customEffect)
    }

    override fun setFilterEffect(filterType: PhotoFilter?) {
        photoEditorView.setFilterEffect(filterType)
    }

    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    override fun saveAsFile(imagePath: String, onSaveListener: OnSaveListener) {
        saveAsFile(imagePath, SaveSettings.Builder().build(), onSaveListener)
    }

    @SuppressLint("StaticFieldLeak")
    override fun saveAsFile(
        imagePath: String,
        saveSettings: SaveSettings,
        onSaveListener: OnSaveListener
    ) {
        Log.d(TAG, "Image Path: $imagePath")
        photoEditorView.saveFilter(object : OnSaveBitmap {
            override fun onBitmapReady(saveBitmap: Bitmap?) {
                val photoSaverTask = PhotoSaverTask(photoEditorView, mBoxHelper)
                photoSaverTask.setOnSaveListener(onSaveListener)
                photoSaverTask.setSaveSettings(saveSettings)
                photoSaverTask.execute(imagePath)
            }

            override fun onFailure(e: Exception?) {
                e?.run {
                    onSaveListener.onFailure(this)
                }
            }
        })
    }

    override fun saveAsBitmap(onSaveBitmap: OnSaveBitmap) {
        saveAsBitmap(SaveSettings.Builder().build(), onSaveBitmap)
    }

    @SuppressLint("StaticFieldLeak")
    override fun saveAsBitmap(
        saveSettings: SaveSettings,
        onSaveBitmap: OnSaveBitmap
    ) {
        photoEditorView.saveFilter(object : OnSaveBitmap {
            override fun onBitmapReady(saveBitmap: Bitmap?) {
                val photoSaverTask = PhotoSaverTask(photoEditorView, mBoxHelper)
                photoSaverTask.setOnSaveBitmap(onSaveBitmap)
                photoSaverTask.setSaveSettings(saveSettings)
                photoSaverTask.saveBitmap()
            }

            override fun onFailure(e: Exception?) {
                onSaveBitmap.onFailure(e)
            }
        })
    }

    override fun setOnPhotoEditorListener(onPhotoEditorListener: OnPhotoEditorListener) {
        mOnPhotoEditorListener = onPhotoEditorListener
        mGraphicManager.onPhotoEditorListener = mOnPhotoEditorListener
        mBrushDrawingStateListener.setOnPhotoEditorListener(mOnPhotoEditorListener)
    }

    override val isCacheEmpty: Boolean
        get() = viewState.addedViewsCount == 0 && viewState.redoViewsCount == 0

    // region Shape
    override fun setShape(shapeBuilder: ShapeBuilder?) {
        drawingView?.currentShapeBuilder = shapeBuilder
    } // endregion

    companion object {
        private const val TAG = "PhotoEditor"
    }

    init {
        drawingView?.setBrushViewChangeListener(mBrushDrawingStateListener)
        val mDetector = GestureDetector(
            context,
            PhotoEditorImageViewListener(
                viewState,
                object : OnSingleTapUpCallback {
                    override fun onSingleTapUp() {
                        clearHelperBox()
                    }
                }
            )
        )
        imageView?.setOnTouchListener { _, event ->
            mOnPhotoEditorListener?.onTouchSourceImage(event)
            mDetector.onTouchEvent(event)
        }
        photoEditorView.setClipSourceImage(builder.clipSourceImage)
    }
}