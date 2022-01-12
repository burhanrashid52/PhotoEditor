package ja.burhanrashid52.photoeditor

import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLException
import android.opengl.GLSurfaceView
import java.nio.IntBuffer
import javax.microedition.khronos.opengles.GL10

/**
 *
 *
 * Bitmap utility class to perform different transformation on bitmap
 *
 *
 * @author [Burhanuddin Rashid](https://github.com/burhanrashid52)
 * @version 0.1.2
 * @since 5/21/2018
 */
internal object BitmapUtil {
    /**
     * Remove transparency in edited bitmap
     *
     * @param source edited image
     * @return bitmap without any transparency
     */
    fun removeTransparency(source: Bitmap?): Bitmap? {
        if (source == null) return source
        var firstX = 0
        var firstY = 0
        var lastX = source.width
        var lastY = source.height
        val pixels = IntArray(source.width * source.height)
        source.getPixels(pixels, 0, source.width, 0, 0, source.width, source.height)
        loop@ for (x in 0 until source.width) {
            for (y in 0 until source.height) {
                if (pixels[x + y * source.width] != Color.TRANSPARENT) {
                    firstX = x
                    break@loop
                }
            }
        }
        loop@ for (y in 0 until source.height) {
            for (x in firstX until source.width) {
                if (pixels[x + y * source.width] != Color.TRANSPARENT) {
                    firstY = y
                    break@loop
                }
            }
        }
        loop@ for (x in source.width - 1 downTo firstX) {
            for (y in source.height - 1 downTo firstY) {
                if (pixels[x + y * source.width] != Color.TRANSPARENT) {
                    lastX = x
                    break@loop
                }
            }
        }
        loop@ for (y in source.height - 1 downTo firstY) {
            for (x in source.width - 1 downTo firstX) {
                if (pixels[x + y * source.width] != Color.TRANSPARENT) {
                    lastY = y
                    break@loop
                }
            }
        }
        return Bitmap.createBitmap(source, firstX, firstY, lastX - firstX, lastY - firstY)
    }

    /**
     * Save filter bitmap from [ImageFilterView]
     *
     * @param glSurfaceView surface view on which is image is drawn
     * @param gl            open gl source to read pixels from [GLSurfaceView]
     * @return save bitmap
     * @throws OutOfMemoryError error when system is out of memory to load and save bitmap
     */
    @Throws(OutOfMemoryError::class)
    fun createBitmapFromGLSurface(glSurfaceView: GLSurfaceView, gl: GL10): Bitmap? {
        val x = 0
        val y = 0
        val w = glSurfaceView.width
        val h = glSurfaceView.height
        val bitmapBuffer = IntArray(w * h)
        val bitmapSource = IntArray(w * h)
        val intBuffer = IntBuffer.wrap(bitmapBuffer)
        intBuffer.position(0)
        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer)
            var offset1: Int
            var offset2: Int
            for (i in 0 until h) {
                offset1 = i * w
                offset2 = (h - i - 1) * w
                for (j in 0 until w) {
                    val texturePixel = bitmapBuffer[offset1 + j]
                    val blue = texturePixel shr 16 and 0xff
                    val red = texturePixel shl 16 and 0x00ff0000
                    val pixel = texturePixel and -0xff0100 or red or blue
                    bitmapSource[offset2 + j] = pixel
                }
            }
        } catch (e: GLException) {
            return null
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888)
    }
}