package com.burhanrashid52.photoeditor;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.SaveSettings;

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
        System.out.println("Bako: ondestroy called on baby ");
        if (null != executor) {
            System.out.println("Bako: on Destroyedddddd........");
            executor.shutdownNow();
        }
    }

    public static boolean isSdk29OrHigher() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q);
    }

    @SuppressLint("InlinedApi")
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
                updateResult(true, filePath, null, editedImageUri);
            } catch (final Exception ex) {
                ex.printStackTrace();
                updateResult(false, null, ex.getMessage(), null);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        });

    }

    private static class FileCreateData {
        public boolean isCreated;
        public String filePath;
        public Uri uri;
        public String error;

        public FileCreateData(boolean isCreated, String filePath, Uri uri, String error) {
            this.isCreated = isCreated;
            this.filePath = filePath;
            this.uri = uri;
            this.error = error;
        }
    }

    public interface OnFileCreateResult {
        void onFileCreateResult(boolean created, String filePath, String error, Uri Uri);
    }

    private void updateResult(boolean result, String filePath, String error, Uri uri) {
        fileCreatedResult.postValue(new FileCreateData(result, filePath, uri, error));
    }

}
