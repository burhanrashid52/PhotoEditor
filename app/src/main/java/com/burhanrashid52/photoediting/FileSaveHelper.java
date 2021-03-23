package com.burhanrashid52.photoediting;

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

/**
 * General contract of this class is to
 * create a file on devices running android 10 and above.
 * As android 10 introduced scope storage
 * </br>
 * <p>
 * How to Use it-
 * Call {@linkplain FileSaveHelper#createFileForSdk29orHigher(String, OnFileCreateResult)}
 * if file is created you would receive it's file path and Uri
 * and after you are done with File call {@linkplain FileSaveHelper#notifyThatFileIsNowPubliclyAvailable()}
 * <p>
 * </br>
 * <p>
 * It's designed to avoid using libraries ,handler and methods like {@linkplain android.app.Activity#runOnUiThread(Runnable)}.
 * Remember it observes the lifecycle of {@linkplain AppCompatActivity} in order to shutdown the executor.
 * </p>
 *
 */
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


    /**
     * The effect of this method is
     * 1- insert new Image File data in MediaStore.Images column
     * 2- create File on Disk.
     * Remember we had exclusive access to the file and only our app can see the file
     * unless {@linkplain FileSaveHelper#notifyThatFileIsNowPubliclyAvailable()} is called on.
     *
     * @param fileNameToSave fileName
     * @param listener       result listener
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void createFileForSdk29orHigher(String fileNameToSave, OnFileCreateResult listener) {
        this.resultListener = listener;
        executor.submit(() -> {
            Cursor cursor = null;
            String filePath = null;
            try {
                // insert new image file data into media store
                Uri imageCollection;
                imageCollection = MediaStore.Images.Media
                        .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                final ContentValues newImageDetails = new ContentValues();
                newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileNameToSave);
                newImageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);
                final Uri editedImageUri = contentResolver
                        .insert(imageCollection, newImageDetails);
                // create a file on disk . simply File#createNewFile() won't work. simply to meet library needs.
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

    /**
     * The general contract of this method is to notify that
     * file is visible now and can be accessed by all other entities that has access to File System.
     * call it when you are done processing the file.
     */
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

        public FileCreateData(boolean isCreated, String filePath,
                              Uri uri, String error,
                              ContentValues newImageDetails) {
            this.isCreated = isCreated;
            this.filePath = filePath;
            this.uri = uri;
            this.error = error;
            this.imageDetails = newImageDetails;
        }
    }

    public interface OnFileCreateResult {
        /**
         * @param created  whether file creation is success or failure
         * @param filePath filepath on disk. null in case of failure
         * @param error    in case file creation is failed . it would represent the cause
         * @param Uri      Uri to the newly created file. null in case of failure
         */
        void onFileCreateResult(boolean created, String filePath, String error, Uri Uri);
    }

    private void updateResult(boolean result, String filePath, String error, Uri uri, ContentValues newImageDetails) {
        fileCreatedResult.postValue(new FileCreateData(result, filePath, uri, error, newImageDetails));
    }

}
