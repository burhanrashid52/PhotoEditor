package com.burhanrashid52.photoediting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

import com.burhanrashid52.photoediting.base.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.SaveSettings;

/**
 * General contract of this class is to
 * create a file on a device.
 * </br>
 * How to Use it-
 * Call {@linkplain FileSaveHelper#createFile(ContentResolver,String, OnFileCreateResult)}
 * if file is created you would receive it's file path and Uri
 * and after you are done with File call {@linkplain FileSaveHelper#notifyThatFileIsNowPubliclyAvailable(ContentResolver)}
 * </br>
 * Remember! in order to shutdown executor call {@linkplain FileSaveHelper#addObserver(LifecycleOwner)} or
 * create object with the {@linkplain FileSaveHelper#FileSaveHelper(LifecycleOwner)}
 */
public class FileSaveHelper implements LifecycleObserver {
    private final ExecutorService executor;
    private final MutableLiveData<FileCreateData> fileCreatedResult;
    private OnFileCreateResult resultListener;
    private final Observer<FileCreateData> observer = fileCreateData -> {
        if (resultListener != null) {
            resultListener.onFileCreateResult(fileCreateData.isCreated,
                    fileCreateData.filePath,
                    fileCreateData.error,
                    fileCreateData.uri);
        }
    };


    public FileSaveHelper() {
        executor = Executors.newSingleThreadExecutor();
        fileCreatedResult = new MutableLiveData<>();
    }

    public FileSaveHelper(LifecycleOwner lifecycleOwner) {
        this();
        addObserver(lifecycleOwner);
    }

    public void addObserver(LifecycleOwner lifecycleOwner) {
        fileCreatedResult.observe(lifecycleOwner, observer);
        lifecycleOwner.getLifecycle().addObserver(this);
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
     * The effects of this method are
     * 1- insert new Image File data in MediaStore.Images column
     * 2- create File on Disk.
     * @param fileNameToSave fileName
     * @param listener       result listener
     */

    @SuppressLint("InlinedApi")
    public void createFile(ContentResolver contentResolver, String fileNameToSave, OnFileCreateResult listener) {
        this.resultListener = listener;
        final boolean isSdk29OrHigher = isSdk29OrHigher();
        executor.submit(() -> {
            Cursor cursor = null;
            String filePath = null;
            try {
                final ContentValues newImageDetails = new ContentValues();
                Uri imageCollection;
                if (isSdk29OrHigher) {
                    imageCollection = MediaStore.Images.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL_PRIMARY
                    );
                    newImageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);
                } else {
                    imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileNameToSave);
                final Uri editedImageUri = contentResolver
                        .insert(imageCollection, newImageDetails);
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

    @SuppressLint("InlinedApi")
    public void notifyThatFileIsNowPubliclyAvailable(ContentResolver contentResolver) {
        if (isSdk29OrHigher()) {
            executor.submit(() -> {
                FileCreateData value = fileCreatedResult.getValue();
                if (value != null) {
                    value.imageDetails.clear();
                    value.imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0);
                    contentResolver.update(value.uri, value.imageDetails, null, null);
                }
            });
        }
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
