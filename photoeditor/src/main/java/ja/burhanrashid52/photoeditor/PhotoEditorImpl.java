package ja.burhanrashid52.photoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

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
public class PhotoEditorImpl implements PhotoEditor {

    private static final String TAG = "PhotoEditor";
    private final PhotoEditorView editorView;
    private final PhotoEditorViewState viewState;
    private final ImageView mainImageView;
    private final View deleteView;
    private final DrawingView drawingView;
    private final BrushDrawingStateListener mBrushDrawingStateListener;
    private final BoxHelper mBoxHelper;
    private OnPhotoEditorListener mOnPhotoEditorListener;
    private final boolean isTextPinchScalable;
    private final Typeface mDefaultTextTypeface;
    private final Typeface mDefaultEmojiTypeface;
    private final GraphicManager mGraphicManager;

    // NOTE(kleyow): This is custom added code diverging from https://github.com/burhanrashid52/PhotoEditor
    private EditorTouchListener mEditorTouchListener;
    private RelativeLayout parentView;
    private RelativeLayout canvasView;
    private ImageView overlayView;
    private ImageView backgroundView;

    @SuppressLint("ClickableViewAccessibility")
    protected PhotoEditorImpl(Builder builder) {
        Context context = builder.context;
        this.editorView = builder.editorView;
        this.parentView = builder.parentView;
        this.canvasView = builder.canvasView;
        this.mainImageView = builder.imageView;
        this.deleteView = builder.deleteView;
        this.drawingView = builder.drawingView;
        this.overlayView = builder.overlayView;
        this.backgroundView = builder.backgroundView;
        this.isTextPinchScalable = builder.isTextPinchScalable;
        this.mDefaultTextTypeface = builder.textTypeface;
        this.mDefaultEmojiTypeface = builder.emojiTypeface;

        this.viewState = new PhotoEditorViewState();
        this.mGraphicManager = new GraphicManager(builder.canvasView, this.viewState);
        this.mBoxHelper = new BoxHelper(builder.canvasView, this.viewState);

        mBrushDrawingStateListener = new BrushDrawingStateListener(builder.editorView, this.viewState);
        this.drawingView.setBrushViewChangeListener(mBrushDrawingStateListener);

        // Create scaling logic for background image.
        this.mEditorTouchListener = new EditorTouchListener(
                parentView,
                canvasView,
                this.viewState);

        parentView.setOnTouchListener(mEditorTouchListener);
    }

    @Override
    public PhotoEditorViewState getViewState() {
        return viewState;
    }

    /**
     * This will rotate the main image and all sub views on {@link PhotoEditorView}
     * NOTE(kleyow): This is custom added code diverging from https://github.com/burhanrashid52/PhotoEditor
     */
    public void rotateImage(float rotation) {
        // Rotates the background image, main image, brush view and all currently placed stickers.
        canvasView.setRotation(rotation);

        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onRotateViewListener();
    }


    @Override
    public View addImage(Bitmap desiredImage) {
        drawingView.enableDrawing(false);

        final MultiTouchListener multiTouchListener = getMultiTouchListener(true);
        Sticker sticker = new Sticker(
                canvasView,
                editorView,
                multiTouchListener,
                viewState,
                mOnPhotoEditorListener,
                mGraphicManager
        );
        sticker.buildView(desiredImage);
        multiTouchListener.setItemRootFrameView(sticker.getRootView());

        sticker.getRootView().setOnTouchListener(multiTouchListener);

        addToEditor(sticker);
        final Map<View, MultiTouchListener> multiTouchListenerByView = viewState.getMultiTouchListenerByView();
        multiTouchListenerByView.put(sticker.getRootView(), multiTouchListener);

        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onInFocusViewChangeListener(sticker.getRootView());

        return sticker.getRootView();
    }

    @Override
    public View addText(String text, final int colorCodeTextView) {
        return addText(null, text, colorCodeTextView);
    }

    @Override
    public View addText(@Nullable Typeface textTypeface, String text, final int colorCodeTextView) {
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();

        styleBuilder.withTextColor(colorCodeTextView);
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface);
        }

        return addText(text, styleBuilder);
    }

    @Override
    public View addText(String text, @Nullable TextStyleBuilder styleBuilder) {
        drawingView.enableDrawing(false);
        MultiTouchListener multiTouchListener = getMultiTouchListener(isTextPinchScalable);
        Text textGraphic = new Text(
                canvasView,
                editorView,
                multiTouchListener,
                viewState,
                mOnPhotoEditorListener,
                mDefaultTextTypeface,
                mGraphicManager
        );
        textGraphic.buildView(text, styleBuilder);
        multiTouchListener.setItemRootFrameView(textGraphic.getRootView());

        textGraphic.getRootView().setOnTouchListener(multiTouchListener);

        addToEditor(textGraphic);

        final Map<View, MultiTouchListener> multiTouchListenerByView = viewState.getMultiTouchListenerByView();
        multiTouchListenerByView.put(textGraphic.getRootView(), multiTouchListener);

        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onInFocusViewChangeListener(textGraphic.getRootView());

        return textGraphic.getRootView();
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
    public View addEmoji(String emojiName) {
        return addEmoji(null, emojiName);
    }


    @Override
    public View addEmoji(Typeface emojiTypeface, String emojiName) {
        drawingView.enableDrawing(false);
        // NOTE(kleyow): Emoji disappear when they are too big for some reason.
        //               I believe screen density plays into it, investigate a suitable font size
        //               again.

        MultiTouchListener multiTouchListener = getMultiTouchListener(true);
        Emoji emoji = new Emoji(
                editorView,
                canvasView,
                multiTouchListener,
                viewState,
                mOnPhotoEditorListener,
                mGraphicManager,
                mDefaultEmojiTypeface
        );
        emoji.buildView(emojiTypeface, emojiName);
        multiTouchListener.setItemRootFrameView(emoji.getRootView());

        final TextView emojiTextView = emoji.getRootView().findViewById(R.id.tvPhotoEditorText);
        emojiTextView.setTextSize(70f);
        emojiTextView.setText(emojiName);

        emoji.getRootView().setOnTouchListener(multiTouchListener);

        addToEditor(emoji);

        final Map<View, MultiTouchListener> multiTouchListenerByView = viewState.getMultiTouchListenerByView();
        multiTouchListenerByView.put(emoji.getRootView(), multiTouchListener);

        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onInFocusViewChangeListener(emoji.getRootView());

        return emoji.getRootView();
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
                canvasView,
                this.mainImageView,
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
            drawingView.setEraserSize(brushEraserSize);
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

    public void removeInFocusView() {
        final View inFocusView = viewState.getCurrentSelectedView();
        if (inFocusView == null || viewState.getAddedViewsCount() == 0) {
            return;
        }

        // Remove the view from ViewState and the UI tree.
        final Map<View, MultiTouchListener> multiTouchListenerByView =
                viewState.getMultiTouchListenerByView();
        multiTouchListenerByView.remove(inFocusView);
        viewState.removeAddedView(inFocusView);
        canvasView.removeView(inFocusView);

        // Fire the callback if the listener exists.
        if (mOnPhotoEditorListener != null) {
            Object viewTag = inFocusView.getTag();
            if (viewTag != null && viewTag instanceof ViewType) {
                mOnPhotoEditorListener.onRemoveViewListener(
                        (ViewType) viewTag,
                        viewState.getAddedViewsCount()
                );
            }
            mOnPhotoEditorListener.onInFocusViewChangeListener(null);
        }
    }

    public void mirrorInFocusView() {
        final View inFocusView = viewState.getCurrentSelectedView();
        if (inFocusView == null) {
            return;
        }

        // Determine if image (sticker/emoji) or text, and rotate appropriately.
        if (inFocusView.findViewById(R.id.imgPhotoEditorImage) instanceof ImageView) {
            if (inFocusView.findViewById((R.id.imgPhotoEditorImage)).getRotationY() == 180) {
                inFocusView.findViewById((R.id.imgPhotoEditorImage)).setRotationY(0f);
            } else {
                inFocusView.findViewById((R.id.imgPhotoEditorImage)).setRotationY(180f);
            }
        } else if (inFocusView.findViewById(R.id.tvPhotoEditorText) instanceof TextView) {
            if (inFocusView.findViewById((R.id.tvPhotoEditorText)).getRotationY() == 180) {
                inFocusView.findViewById((R.id.tvPhotoEditorText)).setRotationY(0f);
            } else {
                inFocusView.findViewById((R.id.tvPhotoEditorText)).setRotationY(180f);
            }
        }

        // Clear the intersection pixel map to force a recalculation of the transparent click-through.
        final MultiTouchListener multiTouchListener = viewState.getMultiTouchListenerByView().get(inFocusView);
        if (multiTouchListener != null) {
            multiTouchListener.scaledImageIntersectionPixelMap = null;
        }

        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onMirrorViewListener();
    }

    public void bringToFrontInFocusView() {
        final View inFocusView = viewState.getCurrentSelectedView();
        if (inFocusView == null) {
            return;
        }

        inFocusView.bringToFront();
    }


    public void unfocusView() {
        clearHelperBox();
    }

    @Override
    public void clearAllViews() {
        mBoxHelper.clearAllViews(drawingView);
    }

    @Override
    public void clearHelperBox() {
        mBoxHelper.clearHelperBox();

        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onInFocusViewChangeListener(null);
    }

    @Override
    public void setFilterEffect(CustomEffect customEffect) {
        editorView.setFilterEffect(customEffect);
    }

    @Override
    public void setFilterEffect(PhotoFilter filterType) {
        editorView.setFilterEffect(filterType);
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
        editorView.saveFilter(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                PhotoSaverTask photoSaverTask = new PhotoSaverTask(editorView, mBoxHelper);
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
        editorView.saveFilter(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                PhotoSaverTask photoSaverTask = new PhotoSaverTask(editorView, mBoxHelper);
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

    private static String convertEmoji(String emoji) {
        String returnedEmoji;
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = new String(Character.toChars(convertEmojiToInt));
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    @Override
    public void setOnPhotoEditorListener(@NonNull OnPhotoEditorListener onPhotoEditorListener) {
        this.mOnPhotoEditorListener = onPhotoEditorListener;
        mGraphicManager.setOnPhotoEditorListener(mOnPhotoEditorListener);
        mBrushDrawingStateListener.setOnPhotoEditorListener(mOnPhotoEditorListener);
        mEditorTouchListener.setOnPhotoEditorListener(mOnPhotoEditorListener);
    }

    @Override
    public boolean isCacheEmpty() {
        return viewState.getAddedViewsCount() == 0 && viewState.getRedoViewsCount() == 0;
    }

    // region Shape
    @Override
    public void setShape(ShapeBuilder shapeBuilder) {
        drawingView.setCurrentShapeBuilder(shapeBuilder);
    }
    // endregion

    public void lockMainImage() {
        editorView.setLockedZoom(true);
    }

    public void unlockMainImage() {
        editorView.setLockedZoom(false);
    }

    public boolean getMainImageLockValue() {
        return editorView.getLockedZoom();
    }
}
