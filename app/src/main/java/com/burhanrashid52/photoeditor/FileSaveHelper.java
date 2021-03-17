package com.burhanrashid52.photoeditor;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileSaveHelper implements LifecycleObserver {
    private ContentResolver contentResolver;
    private ExecutorService executor;
    private MutableLiveData<FileCreateData> fileCreatedResult;
    private OnFileCreateResult resultListener;
    private final Observer<FileCreateData> observer = fileCreateData -> {
        if (resultListener != null) {
            resultListener.onFileCreateResult(fileCreateData.isCreated,
                    fileCreateData.filePath,
                    fileCreateData.error,
                    fileCreateData.uri);
        }
    };

    public FileSaveHelper(AppCompatActivity context) {
        if (isSdk29OrHigher()) {
            executor = Executors.newSingleThreadExecutor();
            this.contentResolver = context.getContentResolver();
            fileCreatedResult = new MutableLiveData<>();
            fileCreatedResult.observe(context, observer);
            context.getLifecycle().addObserver(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void release() {
        if (null != executor) {
            executor.shutdownNow();
        }
    }

    public static boolean isSdk29OrHigher() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void createFileForSdk29orHigher(String fileNameToSave, OnFileCreateResult listener) {
        this.resultListener = listener;
        executor.submit(() -> {
            Cursor cursor = null;
            String filePath = null;
            try {
                Uri imageCollection;
                imageCollection = MediaStore.Images.Media
                        .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                final ContentValues newImageDetails = new ContentValues();
                newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileNameToSave);
                newImageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);
                final Uri editedImageUri = contentResolver
                        .insert(imageCollection, newImageDetails);
                // create a file . simply File#createNewFile() won't work. simply to meet library needs.
                final OutputStream outputStream = contentResolver.openOutputStream(editedImageUri);
                outputStream.close();
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = contentResolver.query(editedImageUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                filePath = cursor.getString(column_index);
                updateResult(true, filePath, null, editedImageUri, newImageDetails);
            } catch (final Exception ex) {
                ex.printStackTrace();
                updateResult(false, null, ex.getMessage(), null, null);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void notifyThatFileIsNowPubliclyAvailable() {
        executor.submit(() -> {
            FileCreateData value = fileCreatedResult.getValue();
            if (value != null) {
                value.imageDetails.clear();
                value.imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0);
                contentResolver.update(value.uri, value.imageDetails, null, null);
            }
        });
    }

    private static class FileCreateData {
        public ContentValues imageDetails;
        public boolean isCreated;
        public String filePath;
        public Uri uri;
        public String error;

        public FileCreateData(boolean isCreated, String filePath, Uri uri, String error, ContentValues newImageDetails) {
            this.isCreated = isCreated;
            this.filePath = filePath;
            this.uri = uri;
            this.error = error;
            this.imageDetails = newImageDetails;
        }
    }

    public interface OnFileCreateResult {
        void onFileCreateResult(boolean created, String filePath, String error, Uri Uri);
    }

    private void updateResult(boolean result, String filePath, String error, Uri uri, ContentValues newImageDetails) {
        fileCreatedResult.postValue(new FileCreateData(result, filePath, uri, error,newImageDetails));
    }

}
