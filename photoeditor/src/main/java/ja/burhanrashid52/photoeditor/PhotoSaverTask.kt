package ja.burhanrashid52.photoeditor

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import android.view.View
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import ja.burhanrashid52.photoeditor.PhotoSaverTask.SaveResult
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Burhanuddin Rashid on 18/05/21.
 *
 * @author <https:></https:>//github.com/burhanrashid52>
 */
internal class PhotoSaverTask(private val mPhotoEditorView: PhotoEditorView?, boxHelper: BoxHelper) : AsyncTask<String?, String?, SaveResult>() {
    private var mSaveSettings: SaveSettings
    private var mOnSaveListener: OnSaveListener? = null
    private var mOnSaveBitmap: OnSaveBitmap? = null
    private val mBoxHelper: BoxHelper = boxHelper
    private val mDrawingView: DrawingView? = mPhotoEditorView!!.drawingView
    fun setOnSaveListener(onSaveListener: OnSaveListener?) {
        mOnSaveListener = onSaveListener
    }

    fun setOnSaveBitmap(onSaveBitmap: OnSaveBitmap?) {
        mOnSaveBitmap = onSaveBitmap
    }

    fun setSaveSettings(saveSettings: SaveSettings) {
        mSaveSettings = saveSettings
    }

    override fun onPreExecute() {
        super.onPreExecute()
        mBoxHelper.clearHelperBox()
        mDrawingView!!.destroyDrawingCache()
    }

    @SuppressLint("MissingPermission")
    override fun doInBackground(vararg inputs: String?): SaveResult {
        // Create a media file name
        return if (inputs.isEmpty()) {
            saveImageAsBitmap()
        } else {
            saveImageInFile(inputs[0]!!)
        }
    }

    private fun saveImageAsBitmap(): SaveResult {
        return if (mPhotoEditorView != null) {
            SaveResult(null, null, buildBitmap())
        } else {
            SaveResult(null, null, null)
        }
    }

    private fun saveImageInFile(mImagePath: String): SaveResult {
        val file = File(mImagePath)
        return try {
            val out = FileOutputStream(file, false)
            if (mPhotoEditorView != null) {
                val capturedBitmap = buildBitmap()
                capturedBitmap.compress(mSaveSettings.compressFormat, mSaveSettings.compressQuality, out)
            }
            out.flush()
            out.close()
            Log.d(TAG, "Filed Saved Successfully")
            SaveResult(null, mImagePath, null)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "Failed to save File")
            SaveResult(e, mImagePath, null)
        }
    }

    private fun buildBitmap(): Bitmap {
        return if (mSaveSettings.isTransparencyEnabled) BitmapUtil.removeTransparency(captureView(mPhotoEditorView)) else captureView(mPhotoEditorView)
    }

    override fun onPostExecute(saveResult: SaveResult) {
        super.onPostExecute(saveResult)
        if (TextUtils.isEmpty(saveResult.mImagePath)) {
            handleBitmapCallback(saveResult)
        } else {
            handleFileCallback(saveResult)
        }
    }

    private fun handleFileCallback(saveResult: SaveResult) {
        val exception = saveResult.mException
        val imagePath = saveResult.mImagePath!!
        if (exception == null) {
            //Clear all views if its enabled in save settings
            if (mSaveSettings.isClearViewsEnabled) {
                mBoxHelper.clearAllViews(mDrawingView)
            }
            if (mOnSaveListener != null) {
                mOnSaveListener!!.onSuccess(imagePath)
            }
        } else {
            if (mOnSaveListener != null) {
                mOnSaveListener!!.onFailure(exception)
            }
        }
    }

    private fun handleBitmapCallback(saveResult: SaveResult) {
        val bitmap = saveResult.mBitmap
        if (bitmap != null) {
            if (mSaveSettings.isClearViewsEnabled) {
                mBoxHelper.clearAllViews(mDrawingView)
            }
            if (mOnSaveBitmap != null) {
                mOnSaveBitmap!!.onBitmapReady(bitmap)
            }
        } else {
            if (mOnSaveBitmap != null) {
                mOnSaveBitmap!!.onFailure(Exception("Failed to load the bitmap"))
            }
        }
    }

    private fun captureView(view: View?): Bitmap {
        val bitmap = Bitmap.createBitmap(
                view!!.width,
                view.height,
                Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun saveBitmap() {
        execute()
    }

    fun saveFile(imagePath: String?) {
        execute(imagePath)
    }

    internal class SaveResult(val mException: Exception?, val mImagePath: String?, val mBitmap: Bitmap?)
    companion object {
        const val TAG = "PhotoSaverTask"
    }

    init {
        mSaveSettings = SaveSettings.Builder().build()
    }
}