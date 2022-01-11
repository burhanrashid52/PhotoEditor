package ja.burhanrashid52.photoeditor

import android.graphics.Bitmap.CompressFormat
import androidx.annotation.IntRange

/**
 * @author [Burhanuddin Rashid](https://github.com/burhanrashid52)
 * @since 8/8/2018
 * Builder Class to apply multiple save options
 */
class SaveSettings private constructor(builder: Builder) {
    val isTransparencyEnabled: Boolean
    val isClearViewsEnabled: Boolean
    val compressFormat: CompressFormat
    val compressQuality: Int

    class Builder {
        var isTransparencyEnabled = true
        var isClearViewsEnabled = true
        var compressFormat = CompressFormat.PNG
        var compressQuality = 100

        /**
         * Define a flag to enable transparency while saving image
         *
         * @param transparencyEnabled true if enabled
         * @return Builder
         * @see BitmapUtil.removeTransparency
         */
        fun setTransparencyEnabled(transparencyEnabled: Boolean): Builder {
            isTransparencyEnabled = transparencyEnabled
            return this
        }

        /**
         * Define a flag to clear the view after saving the image
         *
         * @param clearViewsEnabled true if you want to clear all the views on [PhotoEditorView]
         * @return Builder
         */
        fun setClearViewsEnabled(clearViewsEnabled: Boolean): Builder {
            isClearViewsEnabled = clearViewsEnabled
            return this
        }

        /**
         * Set the compression format for the file to save: JPEG, PNG or WEBP
         * @see{android.graphics.Bitmap.CompressFormat}
         * @param compressFormat JPEG, PNG or WEBP
         * @return Builder
         */
        fun setCompressFormat(compressFormat: CompressFormat): Builder {
            this.compressFormat = compressFormat
            return this
        }

        /**
         * Set the expected compression quality for the output, a number between
         * 0 and 100
         * @param compressQuality An integer from 0 to 100
         * @return Builder
         */
        fun setCompressQuality(@IntRange(from = 0, to = 100) compressQuality: Int): Builder {
            this.compressQuality = compressQuality
            return this
        }

        fun build(): SaveSettings {
            return SaveSettings(this)
        }
    }

    init {
        isClearViewsEnabled = builder.isClearViewsEnabled
        isTransparencyEnabled = builder.isTransparencyEnabled
        compressFormat = builder.compressFormat
        compressQuality = builder.compressQuality
    }
}