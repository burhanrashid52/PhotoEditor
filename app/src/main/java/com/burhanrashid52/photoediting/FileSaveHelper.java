package com.burhanrashid52.photoediting;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * General contract of this class is to
 * create a file on a device.
 * </br>
 * How to Use it-
 * Call {@linkplain FileSaveHelper#createFile(String, OnFileCreateResult)}
 * if file is created you would receive it's file path and Uri
 * and after you are done with File call {@linkplain FileSaveHelper#notifyThatFileIsNowPubliclyAvailable(ContentResolver)}
 * </br>
 * Remember! in order to shutdown executor call {@linkplain FileSaveHelper#addObserver(LifecycleOwner)} or
 * create object with the {@linkplain FileSaveHelper#FileSaveHelper(AppCompatActivity)}
 */
public class FileSaveHelper implements LifecycleObserver {
    private final ContentResolver mContentResolver;
    private final ExecutorService executor;
    private final MutableLiveData<FileMeta> fileCreatedResult;
    private OnFileCreateResult resultListener;
    private final Observer<FileMeta> observer = fileMeta -> {
        if (resultListener != null) {
            resultListener.onFileCreateResult(fileMeta.isCreated,
                    fileMeta.filePath,
                    fileMeta.error,
                    fileMeta.uri);
        }
    };


    public FileSaveHelper(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
        executor = Executors.newSingleThreadExecutor();
        fileCreatedResult = new MutableLiveData<>();
    }

    public FileSaveHelper(AppCompatActivity activity) {
        this(activity.getContentResolver());
        addObserver(activity);
    }

    private void addObserver(LifecycleOwner lifecycleOwner) {
        fileCreatedResult.observe(lifecycleOwner, observer);
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void release() {
        if (null != executor) {
            executor.shutdownNow();
        }
    }

    public static boolean isSdkHigherThan28() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q);
    }


    /**
     * The effects of this method are
     * 1- insert new Image File data in MediaStore.Images column
     * 2- create File on Disk.
     *
     * @param fileNameToSave fileName
     * @param listener       result listener
     */

    public void createFile(final String fileNameToSave, OnFileCreateResult listener) {
        this.resultListener = listener;
        executor.submit(() -> {
            Cursor cursor = null;
            String filePath;
            try {
                final ContentValues newImageDetails = new ContentValues();
                Uri imageCollection = buildUriCollection(newImageDetails);
                final Uri editedImageUri = getEditedImageUri(fileNameToSave, newImageDetails, imageCollection);
                filePath = getFilePath(cursor, editedImageUri);
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

    private String getFilePath(Cursor cursor, Uri editedImageUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        cursor = mContentResolver.query(editedImageUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private Uri getEditedImageUri(String fileNameToSave, ContentValues newImageDetails, Uri imageCollection) throws IOException {
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileNameToSave);
        final Uri editedImageUri = mContentResolver.insert(imageCollection, newImageDetails);
        final OutputStream outputStream = mContentResolver.openOutputStream(editedImageUri);
        outputStream.close();
        return editedImageUri;
    }

    @SuppressLint("InlinedApi")
    private Uri buildUriCollection(ContentValues newImageDetails) {
        Uri imageCollection;
        if (isSdkHigherThan28()) {
            imageCollection = MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
            );
            newImageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        return imageCollection;
    }

    @SuppressLint("InlinedApi")
    public void notifyThatFileIsNowPubliclyAvailable(ContentResolver contentResolver) {
        if (isSdkHigherThan28()) {
            executor.submit(() -> {
                FileMeta value = fileCreatedResult.getValue();
                if (value != null) {
                    value.imageDetails.clear();
                    value.imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0);
                    contentResolver.update(value.uri, value.imageDetails, null, null);
                }
            });
        }
    }

    private static class FileMeta {
        public ContentValues imageDetails;
        public boolean isCreated;
        public String filePath;
        public Uri uri;
        public String error;

        public FileMeta(boolean isCreated, String filePath,
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
        fileCreatedResult.postValue(new FileMeta(result, filePath, uri, error, newImageDetails));
    }

}
