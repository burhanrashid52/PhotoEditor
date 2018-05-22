package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.AttributeSet;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.media.effect.EffectFactory.*;
import static ja.burhanrashid52.photoeditor.PhotoFilter.*;

/**
 * <p>
 * Filter Images using ImageFilterView
 * </p>
 *
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.2
 * @since 2/14/2018
 */
class ImageFilterView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private static final String TAG = "GLFilterView";
    private int[] mTextures = new int[2];
    private EffectContext mEffectContext;
    private Effect mEffect;
    private TextureRenderer mTexRenderer = new TextureRenderer();
    private int mImageWidth;
    private int mImageHeight;
    private boolean mInitialized = false;
    private PhotoFilter mCurrentEffect;
    private Bitmap mSourceBitmap;
    private Bitmap mFilterBitmap;
    private CustomEffect mCustomEffect;

    public ImageFilterView(Context context) {
        super(context);
        init();
    }

    public ImageFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setCurrentEffect(NONE);
    }

    void setSourceBitmap(Bitmap sourceBitmap) {
        //  if (mSourceBitmap != null && mSourceBitmap.sameAs(sourceBitmap)) return;
        // mCurrentEffect = NONE;
        mSourceBitmap = sourceBitmap;
        mInitialized = false;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mTexRenderer != null) {
            mTexRenderer.updateViewSize(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        if (!mInitialized) {
            //Only need to do this once
            mEffectContext = EffectContext.createWithCurrentGlContext();
            mTexRenderer.init();
            loadTextures();
            mInitialized = true;
        }
        if (mCurrentEffect != NONE) {
            //if an effect is chosen initialize it and apply it to the texture
            initEffect();
            applyEffect();
        }
        renderResult();
        mFilterBitmap = createBitmapFromGLSurface(getWidth(), getHeight(), gl);
    }

    void setCurrentEffect(PhotoFilter effect) {
        mCurrentEffect = effect;
        mCustomEffect = null;
        requestRender();
    }

    void setCustomEffect(CustomEffect customEffect) {
        mCustomEffect = customEffect;
        requestRender();
    }

    Bitmap getFilterBitmap() {
        return mFilterBitmap;
    }

    private void loadTextures() {
        // Generate textures
        GLES20.glGenTextures(2, mTextures, 0);

        // Load input bitmap
        if (mSourceBitmap != null) {
            mImageWidth = mSourceBitmap.getWidth();
            mImageHeight = mSourceBitmap.getHeight();
            mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

            // Upload to texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mSourceBitmap, 0);

            // Set texture parameters
            GLToolbox.initTexParams();
        }
    }

    private void initEffect() {
        EffectFactory effectFactory = mEffectContext.getFactory();
        if (mEffect != null) {
            mEffect.release();
        }
        if (mCustomEffect != null) {
            mEffect = effectFactory.createEffect(mCustomEffect.getEffectFactoryType());
            Map<String, Object> parameters = mCustomEffect.getParameters();
            for (Map.Entry<String, Object> param : parameters.entrySet()) {
                mEffect.setParameter(param.getKey(), param.getValue());
            }
        } else {
            // Initialize the correct effect based on the selected menu/action item
            switch (mCurrentEffect) {

                case AUTO_FIX:
                    mEffect = effectFactory.createEffect(EFFECT_AUTOFIX);
                    mEffect.setParameter("scale", 0.5f);
                    break;
                case BLACK_WHITE:
                    mEffect = effectFactory.createEffect(EFFECT_BLACKWHITE);
                    mEffect.setParameter("black", .1f);
                    mEffect.setParameter("white", .7f);
                    break;
                case BRIGHTNESS:
                    mEffect = effectFactory.createEffect(EFFECT_BRIGHTNESS);
                    mEffect.setParameter("brightness", 2.0f);
                    break;
                case CONTRAST:
                    mEffect = effectFactory.createEffect(EFFECT_CONTRAST);
                    mEffect.setParameter("contrast", 1.4f);
                    break;
                case CROSS_PROCESS:
                    mEffect = effectFactory.createEffect(EFFECT_CROSSPROCESS);
                    break;
                case DOCUMENTARY:
                    mEffect = effectFactory.createEffect(EFFECT_DOCUMENTARY);
                    break;
                case DUE_TONE:
                    mEffect = effectFactory.createEffect(EFFECT_DUOTONE);
                    mEffect.setParameter("first_color", Color.YELLOW);
                    mEffect.setParameter("second_color", Color.DKGRAY);
                    break;
                case FILL_LIGHT:
                    mEffect = effectFactory.createEffect(EFFECT_FILLLIGHT);
                    mEffect.setParameter("strength", .8f);
                    break;
                case FISH_EYE:
                    mEffect = effectFactory.createEffect(EFFECT_FISHEYE);
                    mEffect.setParameter("scale", .5f);
                    break;
                case FLIPHOR:
                    mEffect = effectFactory.createEffect(EFFECT_FLIP);
                    mEffect.setParameter("horizontal", true);
                    break;
                case FLIPVERT:
                    mEffect = effectFactory.createEffect(EFFECT_FLIP);
                    mEffect.setParameter("vertical", true);
                    break;
                case GRAIN:
                    mEffect = effectFactory.createEffect(EFFECT_GRAIN);
                    mEffect.setParameter("strength", 1.0f);
                    break;
                case GRAY_SCALE:
                    mEffect = effectFactory.createEffect(EFFECT_GRAYSCALE);
                    break;
                case LOMISH:
                    mEffect = effectFactory.createEffect(EFFECT_LOMOISH);
                    break;
                case NEGATIVE:
                    mEffect = effectFactory.createEffect(EFFECT_NEGATIVE);
                    break;
                case NONE:
                    break;
                case POSTERIZE:
                    mEffect = effectFactory.createEffect(EFFECT_POSTERIZE);
                    break;
                case ROTATE:
                    mEffect = effectFactory.createEffect(EFFECT_ROTATE);
                    mEffect.setParameter("angle", 180);
                    break;
                case SATURATE:
                    mEffect = effectFactory.createEffect(EFFECT_SATURATE);
                    mEffect.setParameter("scale", .5f);
                    break;
                case SEPIA:
                    mEffect = effectFactory.createEffect(EFFECT_SEPIA);
                    break;
                case SHARPEN:
                    mEffect = effectFactory.createEffect(EFFECT_SHARPEN);
                    break;
                case TEMPERATURE:
                    mEffect = effectFactory.createEffect(EFFECT_TEMPERATURE);
                    mEffect.setParameter("scale", .9f);
                    break;
                case TINT:
                    mEffect = effectFactory.createEffect(EFFECT_TINT);
                    mEffect.setParameter("tint", Color.MAGENTA);
                    break;
                case VIGNETTE:
                    mEffect = effectFactory.createEffect(EFFECT_VIGNETTE);
                    mEffect.setParameter("scale", .5f);
                    break;
            }
        }
    }

    private void applyEffect() {
        mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
    }

    private void renderResult() {
        if (mCurrentEffect != NONE) {
            // if no effect is chosen, just render the original bitmap
            mTexRenderer.renderTexture(mTextures[1]);
        } else {
            // render the result of applyEffect()
            mTexRenderer.renderTexture(mTextures[0]);
        }
    }

    //Save filter in bitmap
    private Bitmap createBitmapFromGLSurface(int w, int h, GL10 gl) throws OutOfMemoryError {
        int x = 0, y = 0;
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

}
