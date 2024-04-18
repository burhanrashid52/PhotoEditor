package ja.mhdxbilal007.photoeditor

import android.graphics.Bitmap

/**
 * @author [mhdxbilal007](https://github.com/mhdxbilal007)
 * @version 0.1.2
 * @since 5/21/2018
 */
interface OnSaveBitmap {
    fun onBitmapReady(saveBitmap: Bitmap)
}