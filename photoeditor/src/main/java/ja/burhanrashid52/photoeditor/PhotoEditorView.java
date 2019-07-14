package ja.burhanrashid52.photoeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * <p>
 * This ViewGroup will have the {@link BrushDrawingView} to draw paint on it with {@link ImageView}
 * which our source image
 * </p>
 *
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.1
 * @since 1/18/2018
 */

public class PhotoEditorView extends RelativeLayout {

    private static final String TAG = "PhotoEditorView";

    private FilterImageView mImgSource;
    private BrushDrawingView mBrushDrawingView;
    private ImageFilterView mImageFilterView;
    private static final int imgSrcId = 1, brushSrcId = 2, glFilterId = 3;

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

    @SuppressLint("Recycle")
    private void init(@Nullable AttributeSet attrs) {
        //Setup image attributes
        mImgSource = new FilterImageView(getContext());
        mImgSource.setId(imgSrcId);
        mImgSource.setAdjustViewBounds(true);
        RelativeLayout.LayoutParams imgSrcParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgSrcParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PhotoEditorView);
            Drawable imgSrcDrawable = a.getDrawable(R.styleable.PhotoEditorView_photo_src);
            if (imgSrcDrawable != null) {
                mImgSource.setImageDrawable(imgSrcDrawable);
            }
        }

        //Setup brush view
        mBrushDrawingView = new BrushDrawingView(getContext());
        mBrushDrawingView.setVisibility(GONE);
        mBrushDrawingView.setId(brushSrcId);
        //Align brush to the size of image view
        RelativeLayout.LayoutParams brushParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        brushParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        brushParam.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
        brushParam.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);

        //Setup GLSurface attributes
        mImageFilterView = new ImageFilterView(getContext());
        mImageFilterView.setId(glFilterId);
        mImageFilterView.setVisibility(GONE);

        //Align brush to the size of image view
        RelativeLayout.LayoutParams imgFilterParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgFilterParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imgFilterParam.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
        imgFilterParam.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);

        mImgSource.setOnImageChangedListener(new FilterImageView.OnImageChangedListener() {
            @Override
            public void onBitmapLoaded(@Nullable Bitmap sourceBitmap) {
                mImageFilterView.setFilterEffect(PhotoFilter.NONE);
                mImageFilterView.setSourceBitmap(sourceBitmap);
                Log.d(TAG, "onBitmapLoaded() called with: sourceBitmap = [" + sourceBitmap + "]");
            }
        });


        //Add image source
        addView(mImgSource, imgSrcParam);

        //Add Gl FilterView
        addView(mImageFilterView, imgFilterParam);

        //Add brush view
        addView(mBrushDrawingView, brushParam);

    }


    /**
     * Source image which you want to edit
     *
     * @return source ImageView
     */
    public ImageView getSource() {
        return mImgSource;
    }

    BrushDrawingView getBrushDrawingView() {
        return mBrushDrawingView;
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
}
