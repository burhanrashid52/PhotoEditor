package ja.burhanrashid52.photoeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * <p>
 * This ViewGroup will have the {@link DrawingView} to draw paint on it with {@link ImageView}
 * which our source image
 * </p>
 *
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.1
 * @since 1/18/2018
 */

public class PhotoEditorView extends ZoomLayout {

    private static final String TAG = "PhotoEditorView";

    private FilterImageView mImgSource;
    private ImageView mImageOverlay, mImageBackground;
    private DrawingView mDrawingView;
    private ImageFilterView mImageFilterView;
    private boolean clipSourceImage;
    private RelativeLayout mParentLayout, mCanvasLayout;
    private static final int imgSrcId = 1, shapeSrcId = 2, glFilterId = 3, imgOverlayId = 4, imgBackgroundId = 5, parentLayoutId = 6;

    public PhotoEditorView(Context context) {
        super(context);
        init(null);
    }

    public PhotoEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        //Setup image attributes
        mImgSource = new FilterImageView(getContext());
        RelativeLayout.LayoutParams sourceParam = setupImageSource(attrs);

        mImgSource.setOnImageChangedListener(new FilterImageView.OnImageChangedListener() {
            @Override
            public void onBitmapLoaded(@Nullable Bitmap sourceBitmap) {
                mImageFilterView.setFilterEffect(PhotoFilter.NONE);
                mImageFilterView.setSourceBitmap(sourceBitmap);
                Log.d(TAG, "onBitmapLoaded() called with: sourceBitmap = [" + sourceBitmap + "]");
            }
        });

        //Setup GLSurface attributes
        mImageFilterView = new ImageFilterView(getContext());
        RelativeLayout.LayoutParams filterParam = setupFilterView();

        //Setup drawing view
        mDrawingView = new DrawingView(getContext());
        RelativeLayout.LayoutParams brushParam = setupDrawingView();

        // NOTE(kleyow): This is custom added code diverging from https://github.com/burhanrashid52/PhotoEditor
        mImageOverlay = new ImageView(getContext());
        mImageOverlay.setId(imgOverlayId);
        RelativeLayout.LayoutParams imgOverlayParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // NOTE(kleyow): This is custom added code diverging from https://github.com/burhanrashid52/PhotoEditor
        // TODO(kleyow): Need to add logic to handle when the image is square.
        mImageBackground = new ImageView(getContext());
        mImageBackground.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageBackground.setId(imgBackgroundId);
        RelativeLayout.LayoutParams imgBackgroundParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // NOTE(kleyow): Order of addition of views is important.
        // Add background view
        mCanvasLayout.addView(mImageBackground, imgBackgroundParam);

        //Add image source
        mCanvasLayout.addView(mImgSource, sourceParam);

        //Add Gl FilterView
        mCanvasLayout.addView(mImageFilterView, filterParam);

        //Add brush view
        mCanvasLayout.addView(mDrawingView, brushParam);

        mParentLayout.addView(mCanvasLayout);

        // Add overlay view
        addView(mImageOverlay, imgOverlayParam);
    }


    @SuppressLint("Recycle")
    private RelativeLayout.LayoutParams setupImageSource(@Nullable AttributeSet attrs) {
        mImgSource.setId(imgSrcId);
        mImgSource.setAdjustViewBounds(true);
        mImgSource.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams imgSrcParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        imgSrcParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PhotoEditorView);
            Drawable imgSrcDrawable = a.getDrawable(R.styleable.PhotoEditorView_photo_src);
            if (imgSrcDrawable != null) {
                mImgSource.setImageDrawable(imgSrcDrawable);
            }
        }

        int widthParam = ViewGroup.LayoutParams.MATCH_PARENT;
        if (clipSourceImage) {
            widthParam = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                widthParam, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        return params;
    }


    private RelativeLayout.LayoutParams setupDrawingView() {
        mDrawingView.setVisibility(GONE);
        mDrawingView.setId(shapeSrcId);

        // Align drawing view to the size of image view
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);
        params.addRule(RelativeLayout.ALIGN_LEFT, imgSrcId);
        params.addRule(RelativeLayout.ALIGN_RIGHT, imgSrcId);
        return params;
    }


    private RelativeLayout.LayoutParams setupFilterView() {
        mImageFilterView.setVisibility(GONE);
        mImageFilterView.setId(glFilterId);

        //Align brush to the size of image view
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);

        // NOTE(kleyow): This is custom added code diverging from https://github.com/burhanrashid52/PhotoEditor
        mParentLayout = new RelativeLayout(getContext());
        mParentLayout.setId(parentLayoutId);
        RelativeLayout.LayoutParams parentLayoutParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // `ZoomLayout` must have only one child, so this will be the container for all sub-views.
        addView(mParentLayout, parentLayoutParam);


        // NOTE(kleyow): Seperate the view into layers so functionality is not fighting over a
        //               view's pivot. Better seperation of layouts here could be an improvement.
        mCanvasLayout = new RelativeLayout(getContext());
        mCanvasLayout.setId(parentLayoutId);
        RelativeLayout.LayoutParams rotateLayoutParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return params;
    }


    /**
     * Source image which you want to edit
     *
     * @return source ImageView
     */
    public ImageView getSource() {
        return mImgSource;
    }

    public void resetSourceImageSettings() {
        // NOTE(kleyow): Need to reset image after changing the main image because Zooming changes
        //               the settings.
        mImgSource.setAdjustViewBounds(true);
        mImgSource.setScaleType(ImageView.ScaleType.FIT_CENTER);

    }

    DrawingView getDrawingView() {
        return mDrawingView;
    }

    /**
     * Overlay view which you want to edit
     * NOTE(kleyow): This is custom added code diverging from https://github.com/burhanrashid52/PhotoEditor
     *
     * @return source ImageView
     */
    public ImageView getImageOverlayView() {
        return mImageOverlay;
    }

    /**
     * Background view which you want to edit
     * NOTE(kleyow): This is custom added code diverging from https://github.com/burhanrashid52/PhotoEditor
     *
     * @return source ImageView
     */
    public ImageView getBackgroundView() {
        return mImageBackground;
    }

    /**
     * Parent layout which holds all sub views
     * NOTE(kleyow): This is custom added code diverging from https://github.com/burhanrashid52/PhotoEditor
     *
     * @return source RelativeLayout
     */
    public RelativeLayout getParentLayout() {
        return mParentLayout;
    }

    public RelativeLayout getCanvasLayout() {
        return mCanvasLayout;
    }


    void saveFilter(@NonNull final OnSaveBitmap onSaveBitmap) {
        if (mImageFilterView.getVisibility() == VISIBLE) {
            mImageFilterView.saveBitmap(new OnSaveBitmap() {
                @Override
                public void onBitmapReady(final Bitmap saveBitmap) {
                    Log.e(TAG, "saveFilter: " + saveBitmap);
                    mImgSource.setImageBitmap(saveBitmap);
                    mImageFilterView.setVisibility(GONE);
                    onSaveBitmap.onBitmapReady(saveBitmap);
                }

                @Override
                public void onFailure(Exception e) {
                    onSaveBitmap.onFailure(e);
                }
            });
        } else {
            onSaveBitmap.onBitmapReady(mImgSource.getBitmap());
        }
    }

    void setFilterEffect(PhotoFilter filterType) {
        mImageFilterView.setVisibility(VISIBLE);
        mImageFilterView.setSourceBitmap(mImgSource.getBitmap());
        mImageFilterView.setFilterEffect(filterType);
    }

    void setFilterEffect(CustomEffect customEffect) {
        mImageFilterView.setVisibility(VISIBLE);
        mImageFilterView.setSourceBitmap(mImgSource.getBitmap());
        mImageFilterView.setFilterEffect(customEffect);
    }

    void setClipSourceImage(boolean clip) {
        clipSourceImage = clip;
        RelativeLayout.LayoutParams param = setupImageSource(null);
        mImgSource.setLayoutParams(param);
    }

    // endregion
}
