package ja.burhanrashid52.photoeditor

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Burhanuddin Rashid on 18/05/21.
 *
 * @author <https:></https:>//github.com/burhanrashid52>
 */
internal class PhotoSaverTask (photoEditorView: PhotoEditorView, boxHelper: BoxHelper){
    private var mSaveSettings: SaveSettings
    private var mOnSaveListener: PhotoEditor.OnSaveListener? = null
    private var mOnSaveBitmap: OnSaveBitmap? = null
    private val mPhotoEditorView: PhotoEditorView?
    private val mBoxHelper: BoxHelper
    private val mDrawingView: DrawingView?

    fun setOnSaveListener(onSaveListener: PhotoEditor.OnSaveListener?) {
        mOnSaveListener = onSaveListener
    }

    fun setOnSaveBitmap(onSaveBitmap: OnSaveBitmap?) {
        mOnSaveBitmap = onSaveBitmap
    }

    fun setSaveSettings(saveSettings: SaveSettings) {
        mSaveSettings = saveSettings
    }

    fun execute(vararg inputs: String?){
        mBoxHelper.clearHelperBox()
        mDrawingView?.destroyDrawingCache()

        CoroutineScope(Dispatchers.Default).launch {
            if(inputs.isEmpty()){
                saveImageAsBitmap(::handleBitmapCallback)
            }else{
                saveImageInFile(inputs.first().toString(), ::handleFileCallback)
            }
        }
    }

    private fun saveImageAsBitmap(onSaveResult: (Exception?, Bitmap?) -> Unit) {
        captureView(mPhotoEditorView, onSaveResult)
    }

    private fun saveImageInFile(
        mImagePath: String,
        onSaveResult: (Exception?, String?) -> Unit
    ) {
        val file= File(mImagePath)
        try {
            val out = FileOutputStream(file, false)
            if (mPhotoEditorView != null) {
                captureView(mPhotoEditorView){ exception, bitmap ->
                    bitmap?.let {
                        it.compress(
                            mSaveSettings.compressFormat,
                            mSaveSettings.compressQuality,
                            out
                        )
                        out.flush()
                        out.close()

                        onSaveResult(null, mImagePath)
                    }?: onSaveResult(exception, null)
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            onSaveResult(e, null)
        }
    }

    private fun captureView(view: View?, onSaveResult: (Exception?, Bitmap?) -> Unit){
        if(view==null){
            onSaveResult(Exception("View is null"), null)
        }else{
            val bitmap = Bitmap.createBitmap(
                view.width,
                view.height,
                Bitmap.Config.ARGB_8888
            )
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val location= IntArray(2)
                view.getLocationInWindow(location)
                PixelCopy.request(
                    (view.context as Activity).window,
                    Rect(location[0], location[1],location[0]+view.width, location[1]+view.height),
                    bitmap,
                    {
                        onSaveResult(
                            null,
                            if(mSaveSettings.isTransparencyEnabled) {
                                BitmapUtil.removeTransparency(bitmap)
                            }else{
                                bitmap
                            }
                        )
                    },
                    Handler(Looper.getMainLooper())
                )
            }else{
                val canvas = Canvas(bitmap)
                view.draw(canvas)
                onSaveResult(
                    null,
                    if(mSaveSettings.isTransparencyEnabled) {
                        BitmapUtil.removeTransparency(bitmap)
                    }else{
                        bitmap
                    }
                )
            }
        }
    }


    private fun handleFileCallback(ex: Exception?, path: String?) {
        if (ex == null) {
            //Clear all views if its enabled in save settings
            if (mSaveSettings.isClearViewsEnabled) {
                mBoxHelper.clearAllViews(mDrawingView)
            }
            assert(path != null)
            mOnSaveListener?.onSuccess(path!!)
        } else {
            mOnSaveListener?.onFailure(ex)
        }
    }

    private fun handleBitmapCallback(ex: Exception?, bitmap:Bitmap?) {
        if (bitmap != null) {
            if (mSaveSettings.isClearViewsEnabled) {
                mBoxHelper.clearAllViews(mDrawingView)
            }
            mOnSaveBitmap?.onBitmapReady(bitmap)

        } else {
            mOnSaveBitmap?.onFailure(ex)
        }
    }

    fun saveBitmap(){
        execute()
    }
    companion object {
        const val TAG = "PhotoSaverTask"
    }

    init {
        mPhotoEditorView = photoEditorView
        mDrawingView = photoEditorView.drawingView
        mBoxHelper = boxHelper
        mSaveSettings = SaveSettings.Builder().build()
    }
}