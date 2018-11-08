package ja.burhanrashid52.photoeditor;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created on 18/01/2017.
 *
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * <p></p>
 */
class MultiTouchListener implements OnTouchListener {

    private static final int INVALID_POINTER_ID = -1;
    private final GestureDetector mGestureListener;
    private boolean isRotateEnabled = true;
    private boolean isTranslateEnabled = true;
    private boolean isScaleEnabled = true;
    private float minimumScale = 0.5f;
    private float maximumScale = 10.0f;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mPrevX, mPrevY, mPrevRawX, mPrevRawY;
    private ScaleGestureDetector mScaleGestureDetector;

    private int[] location = new int[2];
    private Rect outRect;
    private View deleteView;
    private ImageView photoEditImageView;
    private RelativeLayout parentView;

    private OnMultiTouchListener onMultiTouchListener;
    private OnGestureControl mOnGestureControl;
    private boolean mIsTextPinchZoomable;
    private boolean mShouldClickThroughTransparentPixels;
    private int mVariableTransparentPixelsClickThroughRadius;
    private int mVariableTransparentPixelsClickThroughRadiusAt45Degrees;
    private OnPhotoEditorListener mOnPhotoEditorListener;

    MultiTouchListener(@Nullable View deleteView, RelativeLayout parentView,
                       ImageView photoEditImageView, boolean isTextPinchZoomable,
                       boolean shouldClickThroughTransparentPixels,
                       int transparentPixelsClickThroughRadius,
                               OnPhotoEditorListener onPhotoEditorListener) {
        mIsTextPinchZoomable = isTextPinchZoomable;
        mShouldClickThroughTransparentPixels = shouldClickThroughTransparentPixels;
        mVariableTransparentPixelsClickThroughRadius = transparentPixelsClickThroughRadius;
        mVariableTransparentPixelsClickThroughRadiusAt45Degrees = (int) Math.round(mVariableTransparentPixelsClickThroughRadius * Math.cos(Math.toRadians(45)));
        mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
        mGestureListener = new GestureDetector(new GestureListener());
        this.deleteView = deleteView;
        this.parentView = parentView;
        this.photoEditImageView = photoEditImageView;
        this.mOnPhotoEditorListener = onPhotoEditorListener;
        if (deleteView != null) {
            outRect = new Rect(deleteView.getLeft(), deleteView.getTop(),
                    deleteView.getRight(), deleteView.getBottom());
        } else {
            outRect = new Rect(0, 0, 0, 0);
        }
    }

    private static float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            degrees -= 360.0f;
        } else if (degrees < -180.0f) {
            degrees += 360.0f;
        }

        return degrees;
    }

    private static void move(View view, TransformInfo info) {
        computeRenderOffset(view, info.pivotX, info.pivotY);
        adjustTranslation(view, info.deltaX, info.deltaY);

        float scale = view.getScaleX() * info.deltaScale;
        scale = Math.max(info.minimumScale, Math.min(info.maximumScale, scale));
        view.setScaleX(scale);
        view.setScaleY(scale);
        ((ViewInfo)view.getTag(R.id.viewInfoTag)).setDefaultScaleX(scale);
        ((ViewInfo)view.getTag(R.id.viewInfoTag)).setDefaultScaleY(scale);

        float rotation = adjustAngle(view.getRotation() + info.deltaAngle);
        view.setRotation(rotation);
    }

    private static void adjustTranslation(View view, float deltaX, float deltaY) {
        float[] deltaVector = {deltaX, deltaY};
        view.getMatrix().mapVectors(deltaVector);
        view.setTranslationX(view.getTranslationX() + deltaVector[0]);
        view.setTranslationY(view.getTranslationY() + deltaVector[1]);
    }

    private static void computeRenderOffset(View view, float pivotX, float pivotY) {
        if (view.getPivotX() == pivotX && view.getPivotY() == pivotY) {
            return;
        }

        float[] prevPoint = {0.0f, 0.0f};
        view.getMatrix().mapPoints(prevPoint);

        view.setPivotX(pivotX);
        view.setPivotY(pivotY);

        float[] currPoint = {0.0f, 0.0f};
        view.getMatrix().mapPoints(currPoint);

        float offsetX = currPoint[0] - prevPoint[0];
        float offsetY = currPoint[1] - prevPoint[1];

        view.setTranslationX(view.getTranslationX() - offsetX);
        view.setTranslationY(view.getTranslationY() - offsetY);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(view, event);
        mGestureListener.onTouchEvent(event);

        onMultiTouchListener.onViewSelectedListener(view);

        if (!isTranslateEnabled) {
            return true;
        }

        int action = event.getAction();

        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (action & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mShouldClickThroughTransparentPixels) {
                    if (isImageWithBitmapDrawable(view)) {

                        //If user clicks on an transparent pixel we return but not absorbing the event
                        if(!isOpaquePixelClicked(view, event)){
                            return false;
                        }
                    }
                }

                mPrevX = event.getX();
                mPrevY = event.getY();
                mPrevRawX = event.getRawX();
                mPrevRawY = event.getRawY();
                mActivePointerId = event.getPointerId(0);
                if (deleteView != null) {
                    deleteView.setAlpha(0);
                    deleteView.setVisibility(View.VISIBLE);
                    deleteView.animate().setStartDelay(200).setDuration(400).alpha(1.0f);
                }
                view.bringToFront();
                firePhotoEditorSDKListener(view, true);
                break;
            case MotionEvent.ACTION_MOVE:
                ViewInfo viewInfo = (ViewInfo) view.getTag(R.id.viewInfoTag);
                if (deleteView != null  && isViewInBounds(deleteView, x, y) && !viewInfo.isDeleting()) {
                    viewInfo.setDeleting(true);

                    view.setPivotX(view.getMeasuredWidth()/2);
                    view.setPivotY(view.getMeasuredHeight()/2);
                    view.animate()
                            //Set x,y to delete view center - view.width/height
                            .x(((deleteView.getX() + deleteView.getWidth()/2) - view.getWidth()/2))
                            .y(((deleteView.getY() + deleteView.getHeight()/2) - view.getHeight()/2))
                            .scaleX(viewInfo.getScaledDownX())
                            .scaleY(viewInfo.getScaledDownY())
                            .setDuration(500)
                            .start();
                } else if (deleteView != null && !isViewInBounds(deleteView, x, y) && viewInfo.isDeleting()) {
                    viewInfo.setDeleting(false);
                    view.animate().cancel();
                    view.animate()
                            .scaleX(viewInfo.getDefaultScaleX())
                            .scaleY(viewInfo.getDefaultScaleY())
                            .setDuration(300)
                            .start();

                } else if(deleteView != null && !isViewInBounds(deleteView, x, y) && !viewInfo.isDeleting()){
                    int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                    if (pointerIndexMove != -1) {
                        float currX = event.getX(pointerIndexMove);
                        float currY = event.getY(pointerIndexMove);
                        if (!mScaleGestureDetector.isInProgress()) {
                            adjustTranslation(view, currX - mPrevX, currY - mPrevY);
                        }
                    }
                } else if (deleteView == null){
                    int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                    if (pointerIndexMove != -1) {
                        float currX = event.getX(pointerIndexMove);
                        float currY = event.getY(pointerIndexMove);
                        if (!mScaleGestureDetector.isInProgress()) {
                            adjustTranslation(view, currX - mPrevX, currY - mPrevY);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_UP:
                mActivePointerId = INVALID_POINTER_ID;
                if (deleteView != null && isViewInBounds(deleteView, x, y)) {
                    if (onMultiTouchListener != null)
                        onMultiTouchListener.onRemoveViewListener(view);
                } else if (!isViewInBounds(photoEditImageView, x, y)) {
                    view.animate().translationY(0).translationY(0);
                }
                if (deleteView != null) {
                    deleteView.setVisibility(View.GONE);
                }
                firePhotoEditorSDKListener(view, false);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                int pointerIndexPointerUp = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                int pointerId = event.getPointerId(pointerIndexPointerUp);
                if (pointerId == mActivePointerId) {
                    int newPointerIndex = pointerIndexPointerUp == 0 ? 1 : 0;
                    mPrevX = event.getX(newPointerIndex);
                    mPrevY = event.getY(newPointerIndex);
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
        }
        return true;
    }

    /**
     * @return False if the click is on an fully transparent pixel (and if no opaque pixel is found inside the given radius), true otherwise.
     */
    private boolean isOpaquePixelClicked(View view, MotionEvent event) {
        ImageView image = view.findViewById(R.id.imgPhotoEditorImage);
        FrameLayout border = view.findViewById(R.id.frmBorder);
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
//        Bitmap drawableBitmap = bitmap.copy(bitmap.getConfig(), true);

        int eventX = (int)event.getX();
        int eventY = (int)event.getY();

        Rect imageRect = new Rect();
        image.getHitRect(imageRect);

        //Get rect of border
        Rect borderRect = new Rect();
        border.getHitRect(borderRect);

        //Enable to enabled radius to scale together with scaling
//        mVariableTransparentPixelsClickThroughRadius = (int)(mTransparentPixelsClickThroughRadius * view.getScaleX());

        //The eventX and Y for the actual image (discluding the frame and any views around it)
        int imageEventX = eventX - (imageRect.left + borderRect.left);
        int imageEventY = eventY - (imageRect.top + borderRect.top);

        float scaleFactorX = (float) bitmap.getWidth() / (float) image.getWidth();
        float scaleFactorY = (float) bitmap.getHeight()/ (float)image.getHeight();

        //To get the coordinates of the click relative to the bitmap we use an scaled image matrix
        float[] imageEventXY = new float[]{imageEventX, imageEventY};
        Matrix invertMatrix = new Matrix();
        ((ImageView) image).getImageMatrix().invert(invertMatrix);
        invertMatrix.setScale(scaleFactorX, scaleFactorY);
        invertMatrix.mapPoints(imageEventXY);

        int bitmapX = (int) imageEventXY[0];
        int bitmapY = (int) imageEventXY[1];

        //Limit x, y range within bitmap
        if (bitmapX < 0) {
            bitmapX = 0;
        } else if (bitmapX > bitmap.getWidth() - 1) {
            bitmapX = bitmap.getWidth() - 1;
        }

        if (bitmapY < 0) {
            bitmapY = 0;
        } else if (bitmapY > bitmap.getHeight() - 1) {
            bitmapY = bitmap.getHeight() - 1;
        }

        return isPixelsInRadiusOpaque(bitmap, bitmapX, bitmapY);
    }

    /**
     *
     * @param bitmap
     * @param x
     * @param y
     * @return True if it finds any Opaque pixels on the given bitmap within the given radius of a given point. False otherwise.
     */
    private boolean isPixelsInRadiusOpaque(Bitmap bitmap, int x, int y){
        if (isPixelOpaque(bitmap, x, y)) return true;

        /*
                                   |
                                   |
                                   |
                                   |
                           --------|--------
                                   |
                                   |
                                   |
                                   |
         */
        for (int i = mVariableTransparentPixelsClickThroughRadius; i > 0; i--) {
            if (isPixelOpaque(bitmap,x + i, y)) return true;
            if (isPixelOpaque(bitmap, x,y + i)) return true;
            if (isPixelOpaque(bitmap,x - i, y)) return true;
            if (isPixelOpaque(bitmap, x,y - i)) return true;
        }
        /*
                               \       /
                                \     /
                                 \   /
                                  \ /
                                   *
                                  / \
                                 /   \
                                /     \
                               /       \
         */
        for (int i = mVariableTransparentPixelsClickThroughRadiusAt45Degrees; i > 0; i--) {
            if (isPixelOpaque(bitmap, x + i, y + i)) return true;
            if (isPixelOpaque(bitmap, x - i, y + i)) return true;
            if (isPixelOpaque(bitmap, x + i, y - i)) return true;
            if (isPixelOpaque(bitmap, x - i, y - i)) return true;
        }
        /*
                               /████████\
                              /██████████\
                             /██        ██\
                             |██        ██|
                             |██        ██|
                             |██        ██|
                             \██        ██/
                              \██████████/
                               \████████/
         */
        float delta = (float)mVariableTransparentPixelsClickThroughRadiusAt45Degrees /
                (float)(mVariableTransparentPixelsClickThroughRadius -
                        mVariableTransparentPixelsClickThroughRadiusAt45Degrees);
        for (int i = mVariableTransparentPixelsClickThroughRadius - 1;
             i > mVariableTransparentPixelsClickThroughRadiusAt45Degrees;
             i--) {
            for (int j = 1; j < delta * (mVariableTransparentPixelsClickThroughRadius - i); j++) {
                if (isPixelOpaque(bitmap, x + j, y - i)) return true;
                if (isPixelOpaque(bitmap, x + i, y - j)) return true;
                if (isPixelOpaque(bitmap, x + i, y + j)) return true;
                if (isPixelOpaque(bitmap, x + j, y + i)) return true;

                if (isPixelOpaque(bitmap, x - j, y - i)) return true;
                if (isPixelOpaque(bitmap, x - i, y - j)) return true;
                if (isPixelOpaque(bitmap, x - i, y + j)) return true;
                if (isPixelOpaque(bitmap, x - j, y + i)) return true;
            }
        }
        /*


                                ████████
                                ████████
                                ████████
                                ████████
                                ████████



         */
        for (int i = 1; i < mVariableTransparentPixelsClickThroughRadiusAt45Degrees; i++) {
            for (int j = 1; j < mVariableTransparentPixelsClickThroughRadiusAt45Degrees; j ++ ) {
                if (isPixelOpaque(bitmap, x + i, y - j)) return true;
                if (isPixelOpaque(bitmap, x + i, y + j)) return true;
                if (isPixelOpaque(bitmap, x - i, y - j)) return true;
                if (isPixelOpaque(bitmap, x - i, y + j)) return true;
            }
        }
        return false;
    }

    private static boolean isPixelOpaque(Bitmap bitmap, int x, int y) {
        if (x >= bitmap.getWidth() || y >= bitmap.getHeight()) return false;
        try {
            int pixelRGB = bitmap.getPixel(x, y);
            return Color.alpha(pixelRGB) != 0; /* 0% transparent. Full opacity */
        } catch (IllegalArgumentException | IllegalStateException e) {
            return false;
        }
    }

    private void firePhotoEditorSDKListener(View view, boolean isStart) {
        Object viewTag = view.getTag();
        if (mOnPhotoEditorListener != null && viewTag != null && viewTag instanceof ViewType) {
            if (isStart)
                mOnPhotoEditorListener.onStartViewChangeListener(((ViewType) view.getTag()));
            else
                mOnPhotoEditorListener.onStopViewChangeListener(((ViewType) view.getTag()));
        }
    }

    private boolean isViewInBounds(View view, int x, int y) {
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    /**
     *    Method that converts the motion event x,y to actually image coordinates
     *    Takes into consideration scale and rotation.
     *
     *    NB!: This method is custom made for this PhotoEditor as it takes into consideration the border around the imageView
     *    If you want to use this method in a scenario where the view parameter is the ImageView itself you need to remove all the "border" handling.
     * @param view
     * @param event
     */
    private Point getBitmapHitXYForMotionEvent(View view, MotionEvent event) {
        ImageView image = view.findViewById(R.id.imgPhotoEditorImage);
        FrameLayout border = view.findViewById(R.id.frmBorder);
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

        int eventX = (int) event.getX();
        int eventY = (int) event.getY();

        Rect imageRect = new Rect();
        image.getHitRect(imageRect);

        //Get rect of border
        Rect borderRect = new Rect();
        border.getHitRect(borderRect);

        //The eventX and Y for the actual image (discluding the frame and any views around it)
        int imageEventX = eventX - (imageRect.left + borderRect.left);
        int imageEventY = eventY - (imageRect.top + borderRect.top);

        float scaleFactorX = (float) bitmap.getWidth() / (float) image.getWidth();
        float scaleFactorY = (float) bitmap.getHeight() / (float) image.getHeight();

        //To get the coordinates of the click relative to the bitmap we use an scaled image matrix
        float[] imageEventXY = new float[]{imageEventX, imageEventY};
        Matrix invertMatrix = new Matrix();
        ((ImageView) image).getImageMatrix().invert(invertMatrix);
        invertMatrix.setScale(scaleFactorX, scaleFactorY);
        invertMatrix.mapPoints(imageEventXY);

        int bitmapX = (int) imageEventXY[0];
        int bitmapY = (int) imageEventXY[1];

        //Limit x, y range within bitmap
        if (bitmapX < 0) {
            bitmapX = 0;
        } else if (bitmapX > bitmap.getWidth() - 1) {
            bitmapX = bitmap.getWidth() - 1;
        }

        if (bitmapY < 0) {
            bitmapY = 0;
        } else if (bitmapY > bitmap.getHeight() - 1) {
            bitmapY = bitmap.getHeight() - 1;
        }

        Log.d("MTL", "BITMAP X:" + bitmapX + " ,Y:" + bitmapY);
        return new Point(bitmapX, bitmapY);
    }

    private boolean isImageWithBitmapDrawable(View view){
        return view.findViewById(R.id.imgPhotoEditorImage) != null && ((BitmapDrawable) ((ImageView)view.findViewById(R.id.imgPhotoEditorImage)).getDrawable()).getBitmap() != null;
    }

    void setOnMultiTouchListener(OnMultiTouchListener onMultiTouchListener) {
        this.onMultiTouchListener = onMultiTouchListener;
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private float mPivotX;
        private float mPivotY;
        private Vector2D mPrevSpanVector = new Vector2D();

        @Override
        public boolean onScaleBegin(View view, ScaleGestureDetector detector) {
            mPivotX = detector.getFocusX();
            mPivotY = detector.getFocusY();
            mPrevSpanVector.set(detector.getCurrentSpanVector());
            onMultiTouchListener.onViewSelectedListener(view);
            return mIsTextPinchZoomable;
        }

        @Override
        public boolean onScale(View view, ScaleGestureDetector detector) {
            TransformInfo info = new TransformInfo();
            info.deltaScale = isScaleEnabled ? detector.getScaleFactor() : 1.0f;
            info.deltaAngle = isRotateEnabled ? Vector2D.getAngle(mPrevSpanVector, detector.getCurrentSpanVector()) : 0.0f;
            info.deltaX = isTranslateEnabled ? detector.getFocusX() - mPivotX : 0.0f;
            info.deltaY = isTranslateEnabled ? detector.getFocusY() - mPivotY : 0.0f;
            info.pivotX = mPivotX;
            info.pivotY = mPivotY;
            info.minimumScale = minimumScale;
            info.maximumScale = maximumScale;
            move(view, info);
            onMultiTouchListener.onViewSelectedListener(view);
            return !mIsTextPinchZoomable;
        }
    }

    private class TransformInfo {
        float deltaX;
        float deltaY;
        float deltaScale;
        float deltaAngle;
        float pivotX;
        float pivotY;
        float minimumScale;
        float maximumScale;
    }

    interface OnMultiTouchListener {
        void onEditTextClickListener(String text, int colorCode);

        void onViewSelectedListener(View selectedView);

        void onRemoveViewListener(View removedView);
    }

    interface OnGestureControl {
        void onClick();

        void onLongClick();

        void onDoubleTap();
    }

    void setOnGestureControl(OnGestureControl onGestureControl) {
        mOnGestureControl = onGestureControl;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mOnGestureControl != null) {
                mOnGestureControl.onClick();
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            if (mOnGestureControl != null) {
                mOnGestureControl.onLongClick();
            }
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mOnGestureControl != null) {
                mOnGestureControl.onDoubleTap();
            }
            return true;
        }
    }
}