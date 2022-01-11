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
    private val parentView: PhotoEditorView
    private val viewState: PhotoEditorViewState
    private val imageView: ImageView?
    private val deleteView: View?
    private val drawingView: DrawingView?
    private val mBrushDrawingStateListener: BrushDrawingStateListener
    private val mBoxHelper: BoxHelper
    private var mOnPhotoEditorListener: OnPhotoEditorListener? = null
    private val isTextPinchScalable: Boolean
    private val mDefaultTextTypeface: Typeface?
    private val mDefaultEmojiTypeface: Typeface?
    private val mGraphicManager: GraphicManager
    private val context: Context
    override fun addImage(desiredImage: Bitmap?) {
        val multiTouchListener = getMultiTouchListener(true)
        val sticker = Sticker(parentView, multiTouchListener, viewState, mGraphicManager)
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
        drawingView!!.enableDrawing(false)
        val multiTouchListener = getMultiTouchListener(isTextPinchScalable)
        val textGraphic =
            Text(parentView, multiTouchListener, viewState, mDefaultTextTypeface, mGraphicManager)
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
        drawingView!!.enableDrawing(false)
        val multiTouchListener = getMultiTouchListener(true)
        val emoji =
            Emoji(parentView, multiTouchListener, viewState, mGraphicManager, mDefaultEmojiTypeface)
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
            parentView,
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
        var opacity = opacity
        if (drawingView != null && drawingView.currentShapeBuilder != null) {
            opacity = (opacity / 100.0 * 255.0).toInt()
            drawingView.currentShapeBuilder!!.withShapeOpacity(opacity)
        }
    }

    override var brushSize: Float
        get() = if (drawingView != null && drawingView.currentShapeBuilder != null) {
            drawingView.currentShapeBuilder!!.shapeSize
        } else 0f
        set(size) {
            if (drawingView != null && drawingView.currentShapeBuilder != null) {
                drawingView.currentShapeBuilder!!.withShapeSize(size)
            }
        }
    override var brushColor: Int
        get() = if (drawingView != null && drawingView.currentShapeBuilder != null) {
            drawingView.currentShapeBuilder!!.shapeColor
        } else 0
        set(color) {
            if (drawingView != null && drawingView.currentShapeBuilder != null) {
                drawingView.currentShapeBuilder!!.withShapeColor(color)
            }
        }

    override fun setBrushEraserSize(brushEraserSize: Float) {
        drawingView?.setBrushEraserSize(brushEraserSize)
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
        parentView.setFilterEffect(customEffect)
    }

    override fun setFilterEffect(filterType: PhotoFilter?) {
        parentView.setFilterEffect(filterType)
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
        parentView.saveFilter(object : OnSaveBitmap {
            override fun onBitmapReady(saveBitmap: Bitmap?) {
                val photoSaverTask = PhotoSaverTask(parentView, mBoxHelper)
                photoSaverTask.setOnSaveListener(onSaveListener)
                photoSaverTask.setSaveSettings(saveSettings)
                photoSaverTask.execute(imagePath)
            }

            override fun onFailure(e: Exception?) {
                onSaveListener.onFailure(e!!)
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
        parentView.saveFilter(object : OnSaveBitmap {
            override fun onBitmapReady(saveBitmap: Bitmap?) {
                val photoSaverTask = PhotoSaverTask(parentView, mBoxHelper)
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
        drawingView!!.setShapeBuilder(shapeBuilder)
    } // endregion

    companion object {
        private const val TAG = "PhotoEditor"
    }

    init {
        context = builder.context
        parentView = builder.parentView
        imageView = builder.imageView
        deleteView = builder.deleteView
        drawingView = builder.drawingView
        isTextPinchScalable = builder.isTextPinchScalable
        mDefaultTextTypeface = builder.textTypeface
        mDefaultEmojiTypeface = builder.emojiTypeface
        viewState = PhotoEditorViewState()
        mGraphicManager = GraphicManager(builder.parentView, viewState)
        mBoxHelper = BoxHelper(builder.parentView, viewState)
        mBrushDrawingStateListener = BrushDrawingStateListener(builder.parentView, viewState)
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
        imageView?.setOnTouchListener { v, event ->
            if (mOnPhotoEditorListener != null) {
                mOnPhotoEditorListener!!.onTouchSourceImage(event)
            }
            mDetector.onTouchEvent(event)
        }
        parentView.setClipSourceImage(builder.clipSourceImage)
    }
}