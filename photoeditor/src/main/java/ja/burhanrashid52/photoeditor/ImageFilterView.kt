package ja.burhanrashid52.photoeditor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.effect.Effect
import android.media.effect.EffectContext
import android.media.effect.EffectFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import ja.burhanrashid52.photoeditor.BitmapUtil.createBitmapFromGLSurface
import ja.burhanrashid52.photoeditor.GLToolbox.initTexParams
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *
 *
 * Filter Images using ImageFilterView
 *
 *
 * @author [Burhanuddin Rashid](https://github.com/burhanrashid52)
 * @version 0.1.2
 * @since 2/14/2018
 */
internal class ImageFilterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs), GLSurfaceView.Renderer {
    private val mTextures = IntArray(2)
    private var mEffectContext: EffectContext? = null
    private var mEffect: Effect? = null
    private val mTexRenderer: TextureRenderer = TextureRenderer()
    private var mImageWidth = 0
    private var mImageHeight = 0
    private var mInitialized = false
    private var mCurrentEffect: PhotoFilter? = null
    private var mSourceBitmap: Bitmap? = null
    private var mCustomEffect: CustomEffect? = null
    private var mOnSaveBitmap: OnSaveBitmap? = null
    private var isSaveImage = false

    init {
        setEGLContextClientVersion(2)
        setRenderer(this)
        renderMode = RENDERMODE_WHEN_DIRTY
        setFilterEffect(PhotoFilter.NONE)
    }

    fun setSourceBitmap(sourceBitmap: Bitmap?) {
        /* if (mSourceBitmap != null && mSourceBitmap.sameAs(sourceBitmap)) {
            //mCurrentEffect = NONE;
        }*/
        mSourceBitmap = sourceBitmap
        mInitialized = false
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {}
    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        mTexRenderer.updateViewSize(width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        if (!mInitialized) {
            //Only need to do this once
            mEffectContext = EffectContext.createWithCurrentGlContext()
            mTexRenderer.init()
            loadTextures()
            mInitialized = true
        }
        if (mCurrentEffect != PhotoFilter.NONE || mCustomEffect != null) {
            //if an effect is chosen initialize it and apply it to the texture
            initEffect()
            applyEffect()
        }
        renderResult()
        if (isSaveImage) {
            val mFilterBitmap = createBitmapFromGLSurface(this, gl)
            Log.e(TAG, "onDrawFrame: $mFilterBitmap")
            isSaveImage = false
            Handler(Looper.getMainLooper()).post { mOnSaveBitmap?.onBitmapReady(mFilterBitmap) }
        }
    }

    fun setFilterEffect(effect: PhotoFilter?) {
        mCurrentEffect = effect
        mCustomEffect = null
        requestRender()
    }

    fun setFilterEffect(customEffect: CustomEffect?) {
        mCustomEffect = customEffect
        requestRender()
    }

    fun saveBitmap(onSaveBitmap: OnSaveBitmap?) {
        mOnSaveBitmap = onSaveBitmap
        isSaveImage = true
        requestRender()
    }

    private fun loadTextures() {
        // Generate textures
        GLES20.glGenTextures(2, mTextures, 0)

        // Load input bitmap
        mSourceBitmap?.let {
            mImageWidth = it.width
            mImageHeight = it.height
            mTexRenderer.updateTextureSize(mImageWidth, mImageHeight)

            // Upload to texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0])
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, it, 0)

            // Set texture parameters
            initTexParams()
        }
    }

    private fun initEffect() {
        mEffectContext?.factory?.apply {
            mEffect?.release()

            if (mCustomEffect != null) {
                mEffect = createEffect(mCustomEffect!!.effectName)
                val parameters = mCustomEffect!!.parameters
                for ((key, value) in parameters) {
                    mEffect?.setParameter(key, value)
                }
            } else {
                // Initialize the correct effect based on the selected menu/action item
                when (mCurrentEffect) {
                    PhotoFilter.AUTO_FIX -> {
                        mEffect = createEffect(EffectFactory.EFFECT_AUTOFIX)
                        mEffect?.setParameter("scale", 0.5f)
                    }
                    PhotoFilter.BLACK_WHITE -> {
                        mEffect = createEffect(EffectFactory.EFFECT_BLACKWHITE)
                        mEffect?.setParameter("black", .1f)
                        mEffect?.setParameter("white", .7f)
                    }
                    PhotoFilter.BRIGHTNESS -> {
                        mEffect = createEffect(EffectFactory.EFFECT_BRIGHTNESS)
                        mEffect?.setParameter("brightness", 2.0f)
                    }
                    PhotoFilter.CONTRAST -> {
                        mEffect = createEffect(EffectFactory.EFFECT_CONTRAST)
                        mEffect?.setParameter("contrast", 1.4f)
                    }
                    PhotoFilter.CROSS_PROCESS -> mEffect =
                        createEffect(EffectFactory.EFFECT_CROSSPROCESS)
                    PhotoFilter.DOCUMENTARY -> mEffect =
                        createEffect(EffectFactory.EFFECT_DOCUMENTARY)
                    PhotoFilter.DUE_TONE -> {
                        mEffect = createEffect(EffectFactory.EFFECT_DUOTONE)
                        mEffect?.setParameter("first_color", Color.YELLOW)
                        mEffect?.setParameter("second_color", Color.DKGRAY)
                    }
                    PhotoFilter.FILL_LIGHT -> {
                        mEffect = createEffect(EffectFactory.EFFECT_FILLLIGHT)
                        mEffect?.setParameter("strength", .8f)
                    }
                    PhotoFilter.FISH_EYE -> {
                        mEffect = createEffect(EffectFactory.EFFECT_FISHEYE)
                        mEffect?.setParameter("scale", .5f)
                    }
                    PhotoFilter.FLIP_HORIZONTAL -> {
                        mEffect = createEffect(EffectFactory.EFFECT_FLIP)
                        mEffect?.setParameter("horizontal", true)
                    }
                    PhotoFilter.FLIP_VERTICAL -> {
                        mEffect = createEffect(EffectFactory.EFFECT_FLIP)
                        mEffect?.setParameter("vertical", true)
                    }
                    PhotoFilter.GRAIN -> {
                        mEffect = createEffect(EffectFactory.EFFECT_GRAIN)
                        mEffect?.setParameter("strength", 1.0f)
                    }
                    PhotoFilter.GRAY_SCALE -> mEffect =
                        createEffect(EffectFactory.EFFECT_GRAYSCALE)
                    PhotoFilter.LOMISH -> mEffect =
                        createEffect(EffectFactory.EFFECT_LOMOISH)
                    PhotoFilter.NEGATIVE -> mEffect =
                        createEffect(EffectFactory.EFFECT_NEGATIVE)
                    PhotoFilter.NONE -> {}
                    PhotoFilter.POSTERIZE -> mEffect =
                        createEffect(EffectFactory.EFFECT_POSTERIZE)
                    PhotoFilter.ROTATE -> {
                        mEffect = createEffect(EffectFactory.EFFECT_ROTATE)
                        mEffect?.setParameter("angle", 180)
                    }
                    PhotoFilter.SATURATE -> {
                        mEffect = createEffect(EffectFactory.EFFECT_SATURATE)
                        mEffect?.setParameter("scale", .5f)
                    }
                    PhotoFilter.SEPIA -> mEffect =
                        createEffect(EffectFactory.EFFECT_SEPIA)
                    PhotoFilter.SHARPEN -> mEffect =
                        createEffect(EffectFactory.EFFECT_SHARPEN)
                    PhotoFilter.TEMPERATURE -> {
                        mEffect = createEffect(EffectFactory.EFFECT_TEMPERATURE)
                        mEffect?.setParameter("scale", .9f)
                    }
                    PhotoFilter.TINT -> {
                        mEffect = createEffect(EffectFactory.EFFECT_TINT)
                        mEffect?.setParameter("tint", Color.MAGENTA)
                    }
                    PhotoFilter.VIGNETTE -> {
                        mEffect = createEffect(EffectFactory.EFFECT_VIGNETTE)
                        mEffect?.setParameter("scale", .5f)
                    }
                }
            }
        }
    }

    private fun applyEffect() {
        mEffect?.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1])
    }

    private fun renderResult() {
        if (mCurrentEffect != PhotoFilter.NONE || mCustomEffect != null) {
            // if no effect is chosen, just render the original bitmap
            mTexRenderer.renderTexture(mTextures[1])
        } else {
            // render the result of applyEffect()
            mTexRenderer.renderTexture(mTextures[0])
        }
    }

    companion object {
        private const val TAG = "ImageFilterView"
    }
}