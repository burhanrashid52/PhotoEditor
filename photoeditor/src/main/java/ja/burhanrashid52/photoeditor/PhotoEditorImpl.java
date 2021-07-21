package ja.burhanrashid52.photoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import ja.burhanrashid52.photoeditor.shape.ShapeBuilder;

/**
 * <p>
 * This class in initialize by {@link PhotoEditor.Builder} using a builder pattern with multiple
 * editing attributes
 * </p>
 *
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.1
 * @since 18/01/2017
 */
class PhotoEditorImpl implements PhotoEditor {

    private static final String TAG = "PhotoEditor";
    private final PhotoEditorView parentView;
    private final PhotoEditorViewState viewState;
    private final ImageView imageView;
    private final View deleteView;
    private final DrawingView drawingView;
    private final BrushDrawingStateListener mBrushDrawingStateListener;
    private final BoxHelper mBoxHelper;
    private OnPhotoEditorListener mOnPhotoEditorListener;
    private final boolean isTextPinchScalable;
    private final Typeface mDefaultTextTypeface;
    private final Typeface mDefaultEmojiTypeface;
    private final GraphicManager mGraphicManager;
    private final Context context;

    @SuppressLint("ClickableViewAccessibility")
    protected PhotoEditorImpl(Builder builder) {
        this.context = builder.context;
        this.parentView = builder.parentView;
        this.imageView = builder.imageView;
        this.deleteView = builder.deleteView;
        this.drawingView = builder.drawingView;
        this.isTextPinchScalable = builder.isTextPinchScalable;
        this.mDefaultTextTypeface = builder.textTypeface;
        this.mDefaultEmojiTypeface = builder.emojiTypeface;

        this.viewState = new PhotoEditorViewState();
        this.mGraphicManager = new GraphicManager(builder.parentView, this.viewState);
        this.mBoxHelper = new BoxHelper(builder.parentView, this.viewState);

        mBrushDrawingStateListener = new BrushDrawingStateListener(builder.parentView, this.viewState);
        this.drawingView.setBrushViewChangeListener(mBrushDrawingStateListener);

        final GestureDetector mDetector = new GestureDetector(
                context,
                new PhotoEditorImageViewListener(
                        this.viewState,
                        new PhotoEditorImageViewListener.OnSingleTapUpCallback() {
                            @Override
                            public void onSingleTapUp() {
                                clearHelperBox();
                            }
                        }
                )
        );

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onTouchSourceImage(event);
                }
                return mDetector.onTouchEvent(event);
            }
        });

        this.parentView.setClipSourceImage(builder.clipSourceImage);
    }


    @Override
    public void addImage(Bitmap desiredImage) {
        MultiTouchListener multiTouchListener = getMultiTouchListener(true);
        Sticker sticker = new Sticker(parentView, multiTouchListener, viewState, mGraphicManager);
        sticker.buildView(desiredImage);
        addToEditor(sticker);
    }

    @Override
    public void addText(String text, final int colorCodeTextView) {
        addText(null, text, colorCodeTextView);
    }

    @Override
    public void addText(@Nullable Typeface textTypeface, String text, final int colorCodeTextView) {
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();

        styleBuilder.withTextColor(colorCodeTextView);
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface);
        }

        addText(text, styleBuilder);
    }

    @Override
    public void addText(String text, @Nullable TextStyleBuilder styleBuilder) {
        drawingView.enableDrawing(false);
        MultiTouchListener multiTouchListener = getMultiTouchListener(isTextPinchScalable);
        Text textGraphic = new Text(parentView, multiTouchListener, viewState, mDefaultTextTypeface, mGraphicManager);
        textGraphic.buildView(text, styleBuilder);
        addToEditor(textGraphic);
    }

    @Override
    public void editText(@NonNull View view, String inputText, int colorCode) {
        editText(view, null, inputText, colorCode);
    }

    @Override
    public void editText(@NonNull View view, @Nullable Typeface textTypeface, String inputText, int colorCode) {
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
        styleBuilder.withTextColor(colorCode);
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface);
        }

        editText(view, inputText, styleBuilder);
    }

    @Override
    public void editText(@NonNull View view, String inputText, @Nullable TextStyleBuilder styleBuilder) {
        TextView inputTextView = view.findViewById(R.id.tvPhotoEditorText);
        if (inputTextView != null && viewState.containsAddedView(view) && !TextUtils.isEmpty(inputText)) {
            inputTextView.setText(inputText);
            if (styleBuilder != null)
                styleBuilder.applyStyle(inputTextView);
            mGraphicManager.updateView(view);
        }
    }

    @Override
    public void addEmoji(String emojiName) {
        addEmoji(null, emojiName);
    }


    @Override
    public void addEmoji(Typeface emojiTypeface, String emojiName) {
        drawingView.enableDrawing(false);
        MultiTouchListener multiTouchListener = getMultiTouchListener(true);
        Emoji emoji = new Emoji(parentView, multiTouchListener, viewState, mGraphicManager, mDefaultEmojiTypeface);
        emoji.buildView(emojiTypeface, emojiName);
        addToEditor(emoji);
    }

    private void addToEditor(Graphic graphic) {
        clearHelperBox();
        mGraphicManager.addView(graphic);
        // Change the in-focus view
        viewState.setCurrentSelectedView(graphic.getRootView());
    }

    /**
     * Create a new instance and scalable touchview
     *
     * @param isPinchScalable true if make pinch-scalable, false otherwise.
     * @return scalable multitouch listener
     */
    @NonNull
    private MultiTouchListener getMultiTouchListener(final boolean isPinchScalable) {
        return new MultiTouchListener(
                deleteView,
                parentView,
                this.imageView,
                isPinchScalable,
                mOnPhotoEditorListener,
                this.viewState);
    }

    @Override
    public void setBrushDrawingMode(boolean brushDrawingMode) {
        if (drawingView != null) {
            drawingView.enableDrawing(brushDrawingMode);
        }
    }

    @Override
    public Boolean getBrushDrawableMode() {
        return drawingView != null && drawingView.isDrawingEnabled();
    }


    @Override
    public void setBrushSize(float size) {
        if (drawingView != null && drawingView.getCurrentShapeBuilder() != null) {
            drawingView.getCurrentShapeBuilder().withShapeSize(size);
        }
    }

    @Override
    public void setOpacity(@IntRange(from = 0, to = 100) int opacity) {
        if (drawingView != null && drawingView.getCurrentShapeBuilder() != null) {
            opacity = (int) ((opacity / 100.0) * 255.0);
            drawingView.getCurrentShapeBuilder().withShapeOpacity(opacity);
        }
    }

    @Override
    public void setBrushColor(@ColorInt int color) {
        if (drawingView != null && drawingView.getCurrentShapeBuilder() != null) {
            drawingView.getCurrentShapeBuilder().withShapeColor(color);
        }
    }

    @Override
    public float getBrushSize() {
        if (drawingView != null && drawingView.getCurrentShapeBuilder() != null) {
            return drawingView.getCurrentShapeBuilder().getShapeSize();
        }
        return 0;
    }

    @Override
    public int getBrushColor() {
        if (drawingView != null && drawingView.getCurrentShapeBuilder() != null) {
            return drawingView.getCurrentShapeBuilder().getShapeColor();
        }
        return 0;
    }

    @Override
    public void setBrushEraserSize(float brushEraserSize) {
        if (drawingView != null) {
            drawingView.setBrushEraserSize(brushEraserSize);
        }
    }

    @Override
    public float getEraserSize() {
        return drawingView != null ? drawingView.getEraserSize() : 0;
    }

    @Override
    public void brushEraser() {
        if (drawingView != null)
            drawingView.brushEraser();
    }

    @Override
    public boolean undo() {
        return mGraphicManager.undoView();
    }

    @Override
    public boolean redo() {
        return mGraphicManager.redoView();
    }

    @Override
    public void clearAllViews() {
        mBoxHelper.clearAllViews(drawingView);
    }

    @Override
    public void clearHelperBox() {
        mBoxHelper.clearHelperBox();
    }

    @Override
    public void setFilterEffect(CustomEffect customEffect) {
        parentView.setFilterEffect(customEffect);
    }

    @Override
    public void setFilterEffect(PhotoFilter filterType) {
        parentView.setFilterEffect(filterType);
    }

    @Override
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void saveAsFile(@NonNull final String imagePath, @NonNull final OnSaveListener onSaveListener) {
        saveAsFile(imagePath, new SaveSettings.Builder().build(), onSaveListener);
    }

    @Override
    @SuppressLint("StaticFieldLeak")
    public void saveAsFile(@NonNull final String imagePath,
                           @NonNull final SaveSettings saveSettings,
                           @NonNull final OnSaveListener onSaveListener) {
        Log.d(TAG, "Image Path: " + imagePath);
        parentView.saveFilter(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                PhotoSaverTask photoSaverTask = new PhotoSaverTask(parentView, mBoxHelper);
                photoSaverTask.setOnSaveListener(onSaveListener);
                photoSaverTask.setSaveSettings(saveSettings);
                photoSaverTask.execute(imagePath);
            }

            @Override
            public void onFailure(Exception e) {
                onSaveListener.onFailure(e);
            }
        });
    }

    @Override
    public void saveAsBitmap(@NonNull final OnSaveBitmap onSaveBitmap) {
        saveAsBitmap(new SaveSettings.Builder().build(), onSaveBitmap);
    }

    @Override
    @SuppressLint("StaticFieldLeak")
    public void saveAsBitmap(@NonNull final SaveSettings saveSettings,
                             @NonNull final OnSaveBitmap onSaveBitmap) {
        parentView.saveFilter(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                PhotoSaverTask photoSaverTask = new PhotoSaverTask(parentView, mBoxHelper);
                photoSaverTask.setOnSaveBitmap(onSaveBitmap);
                photoSaverTask.setSaveSettings(saveSettings);
                photoSaverTask.saveBitmap();
            }

            @Override
            public void onFailure(Exception e) {
                onSaveBitmap.onFailure(e);
            }
        });
    }

    @Override
    public void setOnPhotoEditorListener(@NonNull OnPhotoEditorListener onPhotoEditorListener) {
        this.mOnPhotoEditorListener = onPhotoEditorListener;
        mGraphicManager.setOnPhotoEditorListener(mOnPhotoEditorListener);
        mBrushDrawingStateListener.setOnPhotoEditorListener(mOnPhotoEditorListener);
    }

    @Override
    public boolean isCacheEmpty() {
        return viewState.getAddedViewsCount() == 0 && viewState.getRedoViewsCount() == 0;
    }

    // region Shape
    @Override
    public void setShape(ShapeBuilder shapeBuilder) {
        drawingView.setShapeBuilder(shapeBuilder);
    }
    // endregion

}
