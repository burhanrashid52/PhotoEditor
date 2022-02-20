package com.burhanrashid52.photoediting

import com.burhanrashid52.photoediting.GraphicHelper.addTouchHandleCallbacks
import com.burhanrashid52.photoediting.Helper.realignNewGraphicToCanvas
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import com.burhanrashid52.photoediting.ColorPickerAdapter.OnColorPickerClickListener
import com.burhanrashid52.photoediting.ColorPickerAdapter
import android.view.ViewGroup
import com.burhanrashid52.photoediting.R
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.LayerDrawable
import androidx.core.content.ContextCompat
import com.burhanrashid52.photoediting.base.BaseActivity
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import com.burhanrashid52.photoediting.EmojiBSFragment.EmojiListener
import com.burhanrashid52.photoediting.StickerBSFragment.StickerListener
import com.burhanrashid52.photoediting.tools.EditingToolsAdapter.OnItemSelected
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import com.burhanrashid52.photoediting.PropertiesBSFragment
import com.burhanrashid52.photoediting.ShapeBSFragment
import com.burhanrashid52.photoediting.EmojiBSFragment
import com.burhanrashid52.photoediting.StickerBSFragment
import android.widget.TextView
import android.graphics.Typeface
import com.burhanrashid52.photoediting.tools.EditingToolsAdapter
import com.burhanrashid52.photoediting.filters.FilterViewAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.burhanrashid52.photoediting.FileSaveHelper
import com.burhanrashid52.photoediting.GraphicHelper
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.burhanrashid52.photoediting.EditImageActivity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import com.burhanrashid52.photoediting.TextEditorDialogFragment
import com.burhanrashid52.photoediting.TextEditorDialogFragment.TextEditor
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import ja.burhanrashid52.photoeditor.ViewType
import android.annotation.SuppressLint
import androidx.core.content.FileProvider
import android.content.pm.PackageManager
import com.burhanrashid52.photoediting.FileSaveHelper.OnFileCreateResult
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import android.app.Activity
import ja.burhanrashid52.photoeditor.shape.ShapeType
import ja.burhanrashid52.photoeditor.PhotoFilter
import com.burhanrashid52.photoediting.tools.ToolType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.animation.AnticipateOvershootInterpolator
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.recyclerview.widget.GridLayoutManager
import com.burhanrashid52.photoediting.EmojiBSFragment.EmojiAdapter
import android.content.ContentResolver
import com.burhanrashid52.photoediting.FileSaveHelper.FileMeta
import androidx.appcompat.app.AppCompatActivity
import android.content.ContentValues
import android.database.Cursor
import kotlin.Throws
import com.burhanrashid52.photoediting.PhotoApp
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.SeekBar
import android.widget.RadioGroup
import com.burhanrashid52.photoediting.StickerBSFragment.StickerAdapter
import android.graphics.BitmapFactory
import android.widget.EditText
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import kotlin.jvm.JvmOverloads
import androidx.annotation.ColorInt
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * General contract of this class is to
 * create a file on a device.
 *
 * How to Use it-
 * Call [FileSaveHelper.createFile]
 * if file is created you would receive it's file path and Uri
 * and after you are done with File call [FileSaveHelper.notifyThatFileIsNowPubliclyAvailable]
 *
 * Remember! in order to shutdown executor call [FileSaveHelper.addObserver] or
 * create object with the [FileSaveHelper.FileSaveHelper]
 */
class FileSaveHelper constructor(private val mContentResolver: ContentResolver) :
    LifecycleObserver {
    private val executor: ExecutorService?
    private val fileCreatedResult: MutableLiveData<FileMeta>
    private var resultListener: OnFileCreateResult? = null
    private val observer: Observer<FileMeta> = Observer({ fileMeta: FileMeta ->
        if (resultListener != null) {
            resultListener!!.onFileCreateResult(
                fileMeta.isCreated,
                fileMeta.filePath,
                fileMeta.error,
                fileMeta.uri
            )
        }
    })

    constructor(activity: AppCompatActivity) : this(activity.getContentResolver()) {
        addObserver(activity)
    }

    private fun addObserver(lifecycleOwner: LifecycleOwner) {
        fileCreatedResult.observe(lifecycleOwner, observer)
        lifecycleOwner.getLifecycle().addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun release() {
        if (null != executor) {
            executor.shutdownNow()
        }
    }

    /**
     * The effects of this method are
     * 1- insert new Image File data in MediaStore.Images column
     * 2- create File on Disk.
     *
     * @param fileNameToSave fileName
     * @param listener       result listener
     */
    fun createFile(fileNameToSave: String, listener: OnFileCreateResult?) {
        resultListener = listener
        executor!!.submit(Runnable({
            val cursor: Cursor? = null
            val filePath: String
            try {
                val newImageDetails: ContentValues = ContentValues()
                val imageCollection: Uri = buildUriCollection(newImageDetails)
                val editedImageUri: Uri? =
                    getEditedImageUri(fileNameToSave, newImageDetails, imageCollection)
                filePath = getFilePath(cursor, editedImageUri)
                updateResult(true, filePath, null, editedImageUri, newImageDetails)
            } catch (ex: Exception) {
                ex.printStackTrace()
                updateResult(false, null, ex.message, null, null)
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }))
    }

    private fun getFilePath(cursor: Cursor?, editedImageUri: Uri?): String {
        var cursor: Cursor? = cursor
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        cursor = mContentResolver.query((editedImageUri)!!, proj, null, null, null)
        val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    @Throws(IOException::class)
    private fun getEditedImageUri(
        fileNameToSave: String,
        newImageDetails: ContentValues,
        imageCollection: Uri
    ): Uri? {
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileNameToSave)
        val editedImageUri: Uri? = mContentResolver.insert(imageCollection, newImageDetails)
        val outputStream: OutputStream? = mContentResolver.openOutputStream((editedImageUri)!!)
        outputStream!!.close()
        return editedImageUri
    }

    @SuppressLint("InlinedApi")
    private fun buildUriCollection(newImageDetails: ContentValues): Uri {
        val imageCollection: Uri
        if (isSdkHigherThan28) {
            imageCollection = MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
            newImageDetails.put(MediaStore.Images.Media.IS_PENDING, 1)
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        return imageCollection
    }

    @SuppressLint("InlinedApi")
    fun notifyThatFileIsNowPubliclyAvailable(contentResolver: ContentResolver) {
        if (isSdkHigherThan28) {
            executor!!.submit(Runnable({
                val value: FileMeta? = fileCreatedResult.getValue()
                if (value != null) {
                    value.imageDetails!!.clear()
                    value.imageDetails!!.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update((value.uri)!!, value.imageDetails, null, null)
                }
            }))
        }
    }

    private class FileMeta constructor(
        var isCreated: Boolean, var filePath: String?,
        var uri: Uri?, var error: String?,
        var imageDetails: ContentValues?
    )

    open interface OnFileCreateResult {
        /**
         * @param created  whether file creation is success or failure
         * @param filePath filepath on disk. null in case of failure
         * @param error    in case file creation is failed . it would represent the cause
         * @param Uri      Uri to the newly created file. null in case of failure
         */
        fun onFileCreateResult(created: Boolean, filePath: String?, error: String?, Uri: Uri?)
    }

    private fun updateResult(
        result: Boolean,
        filePath: String?,
        error: String?,
        uri: Uri?,
        newImageDetails: ContentValues?
    ) {
        fileCreatedResult.postValue(FileMeta(result, filePath, uri, error, newImageDetails))
    }

    companion object {
        val isSdkHigherThan28: Boolean
            get() {
                return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            }
    }

    init {
        executor = Executors.newSingleThreadExecutor()
        fileCreatedResult = MutableLiveData()
    }
}