package ja.burhanrashid52.photoeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ja.burhanrashid52.photoeditor.imagezoom.ImageViewTouch;
import ja.burhanrashid52.photoeditor.imagezoom.utils.Matrix3;

public class CropPhotoView extends RelativeLayout {

    private int imgSourceId = 1;
    private int cropPanelId = 2;
    private ImageViewTouch mImgCropSource;
    private CropImageView mCropPanel;
    private boolean mIsCropMode;
    private RectF mSourceImageRectF;
    private CropRatio mCropRatio = CropRatio.NONE;

    public CropPhotoView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public CropPhotoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CropPhotoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CropPhotoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @SuppressLint("Recycle")
    private void init(@Nullable AttributeSet attrs) {
        mImgCropSource = new ImageViewTouch(getContext(), attrs);
        mImgCropSource.setId(imgSourceId);
        mImgCropSource.setAdjustViewBounds(true);

        RelativeLayout.LayoutParams imgCropParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imgCropParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imgCropParams.addRule(RelativeLayout.ALIGN_TOP, imgSourceId);
        imgCropParams.addRule(RelativeLayout.ALIGN_BOTTOM, imgSourceId);


        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PhotoEditorView);
            Drawable imgSrcDrawable = a.getDrawable(R.styleable.PhotoEditorView_photo_src);
            if (imgSrcDrawable != null) {
                mImgCropSource.setImageDrawable(imgSrcDrawable);
            }
        }

        mCropPanel = new CropImageView(getContext());
        mCropPanel.setId(cropPanelId);
        mCropPanel.setVisibility(GONE);
        RelativeLayout.LayoutParams cropPanelParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cropPanelParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        cropPanelParam.addRule(RelativeLayout.ALIGN_TOP, imgSourceId);
        cropPanelParam.addRule(RelativeLayout.ALIGN_LEFT, imgSourceId);
        cropPanelParam.addRule(RelativeLayout.ALIGN_RIGHT, imgSourceId);
        cropPanelParam.addRule(RelativeLayout.ALIGN_BOTTOM, imgSourceId);

        addView(mImgCropSource, imgCropParams);

        addView(mCropPanel, cropPanelParam);
    }

    public void setImageBitmap(final Bitmap bitmap) {
        mImgCropSource.setImageBitmap(bitmap);
    }

    public void setCropMode(boolean isCropMode) {
        mIsCropMode = isCropMode;
        mCropPanel.setVisibility(isCropMode ? VISIBLE : GONE);
        if (isCropMode) {
            mImgCropSource.post(new Runnable() {
                @Override
                public void run() {
                    mCropPanel.setCropRect(mImgCropSource.getBitmapRect());
                    mCropPanel.setRatioCropRect(mImgCropSource.getBitmapRect(), mCropRatio.getRationValue());
                }
            });
        }
    }

    public void saveCropImage(OnSaveBitmap onSaveBitmap) {
        CropImageTask cropImageTask = new CropImageTask(mImgCropSource.getBitmap(), onSaveBitmap);
        cropImageTask.execute();
    }

    public void setCropRatio(CropRatio cropRatio) {
        mCropRatio = cropRatio;
    }

    private final class CropImageTask extends AsyncTask<Void, Void, Bitmap> {

        private OnSaveBitmap mOnSaveBitmap;
        private Bitmap mBitmap;

        public CropImageTask(Bitmap bitmap, OnSaveBitmap onSaveBitmap) {
            mBitmap = bitmap;
            mOnSaveBitmap = onSaveBitmap;
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Bitmap doInBackground(Void... params) {
            RectF cropRect = mCropPanel.getCropRect();
            Matrix touchMatrix = mImgCropSource.getImageViewMatrix();
            // Canvas canvas = new Canvas(resultBit);
            float[] data = new float[9];
            touchMatrix.getValues(data);// 底部图片变化记录矩阵原始数据
            Matrix3 cal = new Matrix3(data);// 辅助矩阵计算类
            Matrix3 inverseMatrix = cal.inverseMatrix();// 计算逆矩阵
            Matrix m = new Matrix();
            m.setValues(inverseMatrix.getValues());
            m.mapRect(cropRect);// 变化剪切矩形

            // Paint paint = new Paint();
            // paint.setColor(Color.RED);
            // paint.setStrokeWidth(10);
            // canvas.drawRect(cropRect, paint);
            // Bitmap resultBit = Bitmap.createBitmap(params[0]).copy(
            // Bitmap.Config.ARGB_8888, true);
            Bitmap resultBit = Bitmap.createBitmap(mBitmap,
                    (int) cropRect.left, (int) cropRect.top,
                    (int) cropRect.width(), (int) cropRect.height());
            return resultBit;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result == null) {
                mOnSaveBitmap.onFailure(new Exception("Failed to fetch image"));
            } else {
                setCropMode(false);
                mOnSaveBitmap.onBitmapReady(result);
            }

        }
    }
}
