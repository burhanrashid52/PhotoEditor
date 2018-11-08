package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.2
 * @since 5/21/2018
 */
class FilterImageView extends AppCompatImageView {

    private OnImageChangedListener mOnImageChangedListener;
    private Rect imageHitRect = new Rect();
    private Point eventXY = new Point();
    private Point eventXY2 = new Point();
    private Rect viewRect = new Rect();
    public FilterImageView(Context context) {
        super(context);
    }

    public void drawImageHitRect(Rect imageHitRect){
        this.imageHitRect = imageHitRect;
    }

    public void drawViewRect(Rect viewRect){
        this.viewRect = viewRect;
    }

    public void drawEventXY(int x, int y ){
        this.eventXY = new Point(x,y);
    }

    public void drawEventXY2(int x, int y ){
        this.eventXY2 = new Point(x,y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawRect(viewRect, paint);
        paint.setColor(Color.GREEN);
        canvas.drawRect(imageHitRect, paint);
        paint.setColor(Color.WHITE);
        canvas.drawRect(eventXY.x, eventXY.y, eventXY.x+5, eventXY.y+5, paint);
        paint.setColor(Color.BLACK);
        canvas.drawRect(eventXY2.x, eventXY2.y, eventXY2.x+5, eventXY2.y+5, paint);
    }

    public FilterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FilterImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnImageChangedListener(OnImageChangedListener onImageChangedListener) {
        mOnImageChangedListener = onImageChangedListener;
    }

    interface OnImageChangedListener {
        void onBitmapLoaded(@Nullable Bitmap sourceBitmap);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageIcon(@Nullable Icon icon) {
        super.setImageIcon(icon);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageState(int[] state, boolean merge) {
        super.setImageState(state, merge);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageTintList(@Nullable ColorStateList tint) {
        super.setImageTintList(tint);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageTintMode(@Nullable PorterDuff.Mode tintMode) {
        super.setImageTintMode(tintMode);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageLevel(int level) {
        super.setImageLevel(level);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Nullable
    Bitmap getBitmap() {
        if (getDrawable() != null) {
            return ((BitmapDrawable) getDrawable()).getBitmap();
        }
        return null;
    }
}
