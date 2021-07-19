package ja.burhanrashid52.photoeditor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

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
class PhotoSaverTask {

    public static final String TAG = "PhotoSaverTask";
    private @NonNull
    SaveSettings mSaveSettings;
    private @Nullable
    PhotoEditor.OnSaveListener mOnSaveListener;
    private @Nullable
    OnSaveBitmap mOnSaveBitmap;
    private final Bitmap mBaseBitmap;
    private final PhotoEditorView mPhotoEditorView;
    private final BoxHelper mBoxHelper;
    private final DrawingView mDrawingView;

    private SaveResult saveResult;

    public PhotoSaverTask(PhotoEditorView photoEditorView, BoxHelper boxHelper) {
        mPhotoEditorView = photoEditorView;
        mBaseBitmap = Bitmap.createBitmap(mPhotoEditorView.getWidth(), mPhotoEditorView.getHeight(), Bitmap.Config.ARGB_8888);
        mDrawingView = photoEditorView.getDrawingView();
        mBoxHelper = boxHelper;
        mSaveSettings = new SaveSettings.Builder().build();
        
        // PRE
        mBoxHelper.clearHelperBox();
        mDrawingView.destroyDrawingCache();
        
    }

    public void saveBitmap() {
        saveResult = saveImageAsBitmap();
        handleBitmapCallback(saveResult);
    }

    public void saveFile(String imagePath) {
        saveResult = saveImageInFile(imagePath);
        handleFileCallback(saveResult);
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
        // Create a new canvas with the content of the editor
        Canvas canvas = new Canvas(mBaseBitmap);
        mPhotoEditorView.draw(canvas);
        
        return mSaveSettings.isTransparencyEnabled()
                ? BitmapUtil.removeTransparency(mBaseBitmap)
                : mBaseBitmap;
    }

   
    private void handleFileCallback(SaveResult saveResult) {
        Exception exception = saveResult.mException;
        String imagePath = saveResult.mImagePath;
        if (exception == null) {
            //Clear all views if its enabled in save settings
            if (mSaveSettings.isClearViewsEnabled()) {
                mBoxHelper.clearAllViews(mDrawingView);
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
                mBoxHelper.clearAllViews(mDrawingView);
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
