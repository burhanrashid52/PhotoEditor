package ja.burhanrashid52.photoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import java.io.File;
import java.io.FileOutputStream;

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
class PhotoEditorImpl implements BrushViewChangeListener, PhotoEditor {

    private static final String TAG = "PhotoEditor";
    private final PhotoEditorView parentView;
    private final PhotoEditorViewState viewState;
    private final ImageView imageView;
    private final View deleteView;
    private final BrushDrawingView brushDrawingView;
    private OnPhotoEditorListener mOnPhotoEditorListener;
    private final boolean isTextPinchScalable;
    private final Typeface mDefaultTextTypeface;
    private final Typeface mDefaultEmojiTypeface;
    private final GraphicManager mGraphicManager;


    protected PhotoEditorImpl(Builder builder) {
        Context context = builder.context;
        this.parentView = builder.parentView;
        this.imageView = builder.imageView;
        this.deleteView = builder.deleteView;
        this.brushDrawingView = builder.brushDrawingView;
        this.isTextPinchScalable = builder.isTextPinchScalable;
        this.mDefaultTextTypeface = builder.textTypeface;
        this.mDefaultEmojiTypeface = builder.emojiTypeface;
        this.viewState = new PhotoEditorViewState();
        brushDrawingView.setBrushViewChangeListener(this);
        this.mGraphicManager = new GraphicManager(builder.parentView, this.viewState);
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
                return mDetector.onTouchEvent(event);
            }
        });
    }


    @Override
    public void addImage(Bitmap desiredImage) {
        MultiTouchListener multiTouchListener = getMultiTouchListener(true);
        Sticker sticker = new Sticker(parentView, multiTouchListener, viewState, mGraphicManager);
        sticker.buildView(desiredImage);
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
        brushDrawingView.setBrushDrawingMode(false);
        MultiTouchListener multiTouchListener = getMultiTouchListener(isTextPinchScalable);
        Text textGraphic = new Text(parentView, multiTouchListener, viewState, mDefaultTextTypeface, mGraphicManager);
        textGraphic.setOnPhotoEditorListener(mOnPhotoEditorListener);
        textGraphic.buildView(text, styleBuilder);
    }

    @Override
    public void editText(@NonNull View view, String inputText, @NonNull int colorCode) {
        editText(view, null, inputText, colorCode);
    }

    @Override
    public void editText(@NonNull View view, @Nullable Typeface textTypeface, String inputText, @NonNull int colorCode) {
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

            parentView.updateViewLayout(view, view.getLayoutParams());
            viewState.replaceAddedView(view);
        }
    }

    @Override
    public void addEmoji(String emojiName) {
        addEmoji(null, emojiName);
    }


    @Override
    public void addEmoji(Typeface emojiTypeface, String emojiName) {
        brushDrawingView.setBrushDrawingMode(false);
        MultiTouchListener multiTouchListener = getMultiTouchListener(true);
        Emoji emoji = new Emoji(parentView, multiTouchListener, viewState, mGraphicManager, mDefaultEmojiTypeface);
        emoji.buildView(emojiTypeface, emojiName);
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
        if (brushDrawingView != null)
            brushDrawingView.setBrushDrawingMode(brushDrawingMode);
    }

    @Override
    public Boolean getBrushDrawableMode() {
        return brushDrawingView != null && brushDrawingView.getBrushDrawingMode();
    }

    @Override
    public void setBrushSize(float size) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushSize(size);
    }

    @Override
    public void setOpacity(@IntRange(from = 0, to = 100) int opacity) {
        if (brushDrawingView != null) {
            opacity = (int) ((opacity / 100.0) * 255.0);
            brushDrawingView.setOpacity(opacity);
        }
    }

    @Override
    public void setBrushColor(@ColorInt int color) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushColor(color);
    }

    @Override
    public void setBrushEraserSize(float brushEraserSize) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushEraserSize(brushEraserSize);
    }


    @Override
    public float getEraserSize() {
        return brushDrawingView != null ? brushDrawingView.getEraserSize() : 0;
    }

    @Override
    public float getBrushSize() {
        if (brushDrawingView != null)
            return brushDrawingView.getBrushSize();
        return 0;
    }

    @Override
    public int getBrushColor() {
        if (brushDrawingView != null)
            return brushDrawingView.getBrushColor();
        return 0;
    }

    @Override
    public void brushEraser() {
        if (brushDrawingView != null)
            brushDrawingView.brushEraser();
    }

    @Override
    public boolean undo() {
        return mGraphicManager.undo();
    }

    @Override
    public boolean redo() {
        return mGraphicManager.redo();
    }

    private void clearBrushAllViews() {
        if (brushDrawingView != null)
            brushDrawingView.clearAll();
    }

    @Override
    public void clearAllViews() {
        for (int i = 0; i < viewState.getAddedViewsCount(); i++) {
            parentView.removeView(viewState.getAddedView(i));
        }
        if (viewState.containsAddedView(brushDrawingView)) {
            parentView.addView(brushDrawingView);
        }
        viewState.clearAddedViews();
        viewState.clearRedoViews();
        clearBrushAllViews();
    }

    @Override
    public void clearHelperBox() {
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
        viewState.clearCurrentSelectedView();
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
                new AsyncTask<String, String, Exception>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        clearHelperBox();
                        brushDrawingView.destroyDrawingCache();
                    }

                    @SuppressLint("MissingPermission")
                    @Override
                    protected Exception doInBackground(String... strings) {
                        // Create a media file name
                        File file = new File(imagePath);
                        try {
                            FileOutputStream out = new FileOutputStream(file, false);
                            if (parentView != null) {
                                Bitmap capturedBitmap = saveSettings.isTransparencyEnabled()
                                        ? BitmapUtil.removeTransparency(captureView(parentView))
                                        : captureView(parentView);
                                capturedBitmap.compress(saveSettings.getCompressFormat(), saveSettings.getCompressQuality(), out);
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
                            //Clear all views if its enabled in save settings
                            if (saveSettings.isClearViewsEnabled()) clearAllViews();
                            onSaveListener.onSuccess(imagePath);
                        } else {
                            onSaveListener.onFailure(e);
                        }
                    }

                }.execute();
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
                new AsyncTask<String, String, Bitmap>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        clearHelperBox();
                        brushDrawingView.destroyDrawingCache();
                    }

                    @Override
                    protected Bitmap doInBackground(String... strings) {
                        if (parentView != null) {
                            return saveSettings.isTransparencyEnabled() ?
                                    BitmapUtil.removeTransparency(captureView(parentView))
                                    : captureView(parentView);
                        } else {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        if (bitmap != null) {
                            if (saveSettings.isClearViewsEnabled()) clearAllViews();
                            onSaveBitmap.onBitmapReady(bitmap);
                        } else {
                            onSaveBitmap.onFailure(new Exception("Failed to load the bitmap"));
                        }
                    }

                }.execute();
            }

            @Override
            public void onFailure(Exception e) {
                onSaveBitmap.onFailure(e);
            }
        });
    }

    private Bitmap captureView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    @Override
    public void setOnPhotoEditorListener(@NonNull OnPhotoEditorListener onPhotoEditorListener) {
        this.mOnPhotoEditorListener = onPhotoEditorListener;
        mGraphicManager.setOnPhotoEditorListener(mOnPhotoEditorListener);
    }

    @Override
    public boolean isCacheEmpty() {
        return viewState.getAddedViewsCount() == 0 && viewState.getRedoViewsCount() == 0;
    }


    @Override
    public void onViewAdd(BrushDrawingView brushDrawingView) {
        if (viewState.getRedoViewsCount() > 0) {
            viewState.popRedoView();
        }
        viewState.addAddedView(brushDrawingView);
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onAddViewListener(
                    ViewType.BRUSH_DRAWING,
                    viewState.getAddedViewsCount()
            );
        }
    }

    @Override
    public void onViewRemoved(BrushDrawingView brushDrawingView) {
        if (viewState.getAddedViewsCount() > 0) {
            View removeView = viewState.removeAddedView(
                    viewState.getAddedViewsCount() - 1
            );
            if (!(removeView instanceof BrushDrawingView)) {
                parentView.removeView(removeView);
            }
            viewState.pushRedoView(removeView);
        }
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onRemoveViewListener(
                    ViewType.BRUSH_DRAWING,
                    viewState.getAddedViewsCount()
            );
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
}