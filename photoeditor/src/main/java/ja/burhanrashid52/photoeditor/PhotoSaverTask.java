package ja.burhanrashid52.photoeditor;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Burhanuddin Rashid on 18/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
class PhotoSaverTask extends AsyncTask<String, String, PhotoSaverTask.SaveResult> {

    public static final String TAG = "PhotoSaverTask";
    private @NonNull
    SaveSettings mSaveSettings;
    private @Nullable
    PhotoEditor.OnSaveListener mOnSaveListener;
    private @Nullable
    OnSaveBitmap mOnSaveBitmap;
    private final PhotoEditorView mPhotoEditorView;
    private final BoxHelper mBoxHelper;
    private final BrushDrawingView mBrushDrawingView;


    public PhotoSaverTask(PhotoEditorView photoEditorView, BoxHelper boxHelper) {
        mPhotoEditorView = photoEditorView;
        mBrushDrawingView = photoEditorView.getBrushDrawingView();
        mBoxHelper = boxHelper;
        mSaveSettings = new SaveSettings.Builder().build();
    }

    public void setOnSaveListener(@Nullable PhotoEditor.OnSaveListener onSaveListener) {
        this.mOnSaveListener = onSaveListener;
    }

    public void setOnSaveBitmap(@Nullable OnSaveBitmap onSaveBitmap) {
        mOnSaveBitmap = onSaveBitmap;
    }

    public void setSaveSettings(@NonNull SaveSettings saveSettings) {
        mSaveSettings = saveSettings;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mBoxHelper.clearHelperBox();
        mBrushDrawingView.destroyDrawingCache();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected SaveResult doInBackground(String... inputs) {
        // Create a media file name
        if (inputs.length == 0) {
            return saveImageAsBitmap();
        } else {
            return saveImageInFile(inputs[0]);
        }
    }

    private SaveResult saveImageAsBitmap() {
        if (mPhotoEditorView != null) {
            return new SaveResult(null, null, buildBitmap());
        } else {
            return new SaveResult(null, null, null);
        }
    }

    @NonNull
    private SaveResult saveImageInFile(String mImagePath) {
        File file = new File(mImagePath);
        try {
            FileOutputStream out = new FileOutputStream(file, false);
            if (mPhotoEditorView != null) {
                Bitmap capturedBitmap = buildBitmap();
                capturedBitmap.compress(mSaveSettings.getCompressFormat(), mSaveSettings.getCompressQuality(), out);
            }
            out.flush();
            out.close();
            Log.d(TAG, "Filed Saved Successfully");
            return new SaveResult(null, mImagePath, null);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to save File");
            return new SaveResult(e, mImagePath, null);
        }
    }

    private Bitmap buildBitmap() {
        return mSaveSettings.isTransparencyEnabled()
                ? BitmapUtil.removeTransparency(captureView(mPhotoEditorView))
                : captureView(mPhotoEditorView);
    }

    @Override
    protected void onPostExecute(SaveResult saveResult) {
        super.onPostExecute(saveResult);
        if (TextUtils.isEmpty(saveResult.mImagePath)) {
            handleBitmapCallback(saveResult);
        } else {
            handleFileCallback(saveResult);
        }

    }

    private void handleFileCallback(SaveResult saveResult) {
        Exception exception = saveResult.mException;
        String imagePath = saveResult.mImagePath;
        if (exception == null) {
            //Clear all views if its enabled in save settings
            if (mSaveSettings.isClearViewsEnabled()) {
                mBoxHelper.clearAllViews(mBrushDrawingView);
            }
            if (mOnSaveListener != null) {
                assert imagePath != null;
                mOnSaveListener.onSuccess(imagePath);
            }
        } else {
            if (mOnSaveListener != null) {
                mOnSaveListener.onFailure(exception);
            }
        }
    }

    private void handleBitmapCallback(SaveResult saveResult) {
        Bitmap bitmap = saveResult.mBitmap;
        if (bitmap != null) {
            if (mSaveSettings.isClearViewsEnabled()) {
                mBoxHelper.clearAllViews(mBrushDrawingView);
            }
            if (mOnSaveBitmap != null) {
                mOnSaveBitmap.onBitmapReady(bitmap);
            }
        } else {
            if (mOnSaveBitmap != null) {
                mOnSaveBitmap.onFailure(new Exception("Failed to load the bitmap"));
            }
        }
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

    public void saveBitmap() {
        execute();
    }

    public void saveFile(String imagePath) {
        execute(imagePath);
    }

    static class SaveResult {
        final Exception mException;
        final String mImagePath;
        final Bitmap mBitmap;

        public SaveResult(Exception exception, String imagePath, Bitmap bitmap) {
            mException = exception;
            mImagePath = imagePath;
            mBitmap = bitmap;
        }
    }
}
