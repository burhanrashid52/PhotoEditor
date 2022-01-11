package ja.burhanrashid52.photoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import ja.burhanrashid52.photoeditor.FilterImageView.OnImageChangedListener

/**
 *
 *
 * This ViewGroup will have the [DrawingView] to draw paint on it with [ImageView]
 * which our source image
 *
 *
 * @author [Burhanuddin Rashid](https://github.com/burhanrashid52)
 * @version 0.1.1
 * @since 1/18/2018
 */
class PhotoEditorView : RelativeLayout {
    private var mImgSource: FilterImageView? = null
    var drawingView: DrawingView? = null
        private set
    private var mImageFilterView: ImageFilterView? = null
    private var clipSourceImage = false

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        //Setup image attributes
        mImgSource = FilterImageView(context)
        val sourceParam = setupImageSource(attrs)
        mImgSource!!.setOnImageChangedListener(object : OnImageChangedListener {
            override fun onBitmapLoaded(sourceBitmap: Bitmap?) {
                mImageFilterView!!.setFilterEffect(PhotoFilter.NONE)
                mImageFilterView!!.setSourceBitmap(sourceBitmap)
                Log.d(TAG, "onBitmapLoaded() called with: sourceBitmap = [$sourceBitmap]")
            }
        })

        //Setup GLSurface attributes
        mImageFilterView = ImageFilterView(context)
        val filterParam = setupFilterView()

        //Setup drawing view
        drawingView = DrawingView(context)
        val brushParam = setupDrawingView()

        //Add image source
        addView(mImgSource, sourceParam)

        //Add Gl FilterView
        addView(mImageFilterView, filterParam)

        //Add brush view
        addView(drawingView, brushParam)
    }

    @SuppressLint("Recycle")
    private fun setupImageSource(attrs: AttributeSet?): LayoutParams {
        mImgSource!!.id = imgSrcId
        mImgSource!!.adjustViewBounds = true
        mImgSource!!.scaleType = ImageView.ScaleType.CENTER_INSIDE
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.PhotoEditorView)
            val imgSrcDrawable = a.getDrawable(R.styleable.PhotoEditorView_photo_src)
            if (imgSrcDrawable != null) {
                mImgSource!!.setImageDrawable(imgSrcDrawable)
            }
        }
        var widthParam = ViewGroup.LayoutParams.MATCH_PARENT
        if (clipSourceImage) {
            widthParam = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        val params = LayoutParams(
            widthParam, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.addRule(CENTER_IN_PARENT, TRUE)
        return params
    }

    private fun setupDrawingView(): LayoutParams {
        drawingView!!.visibility = GONE
        drawingView!!.id = shapeSrcId

        // Align drawing view to the size of image view
        val params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.addRule(CENTER_IN_PARENT, TRUE)
        params.addRule(ALIGN_TOP, imgSrcId)
        params.addRule(ALIGN_BOTTOM, imgSrcId)
        params.addRule(ALIGN_LEFT, imgSrcId)
        params.addRule(ALIGN_RIGHT, imgSrcId)
        return params
    }

    private fun setupFilterView(): LayoutParams {
        mImageFilterView!!.visibility = GONE
        mImageFilterView!!.id = glFilterId

        //Align brush to the size of image view
        val params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.addRule(CENTER_IN_PARENT, TRUE)
        params.addRule(ALIGN_TOP, imgSrcId)
        params.addRule(ALIGN_BOTTOM, imgSrcId)
        return params
    }

    /**
     * Source image which you want to edit
     *
     * @return source ImageView
     */
    val source: ImageView?
        get() = mImgSource

    fun saveFilter(onSaveBitmap: OnSaveBitmap) {
        if (mImageFilterView!!.visibility == VISIBLE) {
            mImageFilterView!!.saveBitmap(object : OnSaveBitmap {
                override fun onBitmapReady(saveBitmap: Bitmap?) {
                    Log.e(TAG, "saveFilter: $saveBitmap")
                    mImgSource!!.setImageBitmap(saveBitmap!!)
                    mImageFilterView!!.visibility = GONE
                    onSaveBitmap.onBitmapReady(saveBitmap)
                }

                override fun onFailure(e: Exception?) {
                    onSaveBitmap.onFailure(e)
                }
            })
        } else {
            onSaveBitmap.onBitmapReady(mImgSource!!.bitmap)
        }
    }

    fun setFilterEffect(filterType: PhotoFilter?) {
        mImageFilterView!!.visibility = VISIBLE
        mImageFilterView!!.setSourceBitmap(mImgSource!!.bitmap)
        mImageFilterView!!.setFilterEffect(filterType)
    }

    fun setFilterEffect(customEffect: CustomEffect?) {
        mImageFilterView!!.visibility = VISIBLE
        mImageFilterView!!.setSourceBitmap(mImgSource!!.bitmap)
        mImageFilterView!!.setFilterEffect(customEffect)
    }

    fun setClipSourceImage(clip: Boolean) {
        clipSourceImage = clip
        val param = setupImageSource(null)
        mImgSource!!.layoutParams = param
    } // endregion

    companion object {
        private const val TAG = "PhotoEditorView"
        private const val imgSrcId = 1
        private const val shapeSrcId = 2
        private const val glFilterId = 3
    }
}