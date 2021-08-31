package ja.burhanrashid52.photoeditor;

import android.graphics.Matrix;
import android.graphics.Rect;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Date;

/**
 * Touch listener for stickers, emoji, text, etc.
 *
 * Created on 18/01/2017.
 *
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * <p></p>
 */
public class MultiTouchListener implements OnTouchListener {

    private static final String TAG = "MultiTouchListener";
    private static final float ABSOLUTE_MINIMUM_SCALE = 0.2f;
    private static final float EDITOR_RELATIVE_MINIMUM_SCALE = 0.4f;
    private static final float EDITOR_RELATIVE_MAXIMUM_SCALE = 3.0f;

    private static final int INVALID_POINTER_ID = -1;
    private final GestureDetector mGestureListener;

    // NOTE(cheng): Appears inert, can be removed
    private boolean isRotateEnabled = true;
    private boolean isTranslateEnabled = true;

    // NOTE(cheng): Appears inert, can be removed
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
    public RelativeLayout parentView;
    public RelativeLayout canvasView;

    private OnMultiTouchListener onMultiTouchListener;
    private OnGestureControl mOnGestureControl;
    private boolean mIsPinchScalable;
    private OnPhotoEditorListener mOnPhotoEditorListener;

    private PhotoEditorViewState viewState;

    public View itemRootFrameView;
    public boolean isCornerMovable = false;
    public boolean isTouchMovable = false;

    // Selection state
    public boolean lastImageTouchMouseDownEventWasOpaque = false;
    public int[] scaledImageIntersectionPixelMap;
    public int scaledImageBitmapWidth;
    public int scaledImageBitmapHeight;
    public Date scaledImageIntersectionPixelMapTimestamp;

    public float centerX, centerY, startR, startScale, startX, startY, startRotation, startA, coordinateX, coordinateY, adjustedScaledWidth, adjustedScaledHeight, editorScale;
    private boolean scalingInProgress = false;

    // itemRootFrameView = The actual item (image, text, emoji)
    MultiTouchListener(@Nullable View deleteView,
                       RelativeLayout parentView,
                       RelativeLayout canvasView,
                       ImageView photoEditImageView,
                       boolean isPinchScalable,
                       OnPhotoEditorListener onPhotoEditorListener,
                       PhotoEditorViewState viewState
    ) {
        mIsPinchScalable = isPinchScalable;
        mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
        mGestureListener = new GestureDetector(new GestureListener());
        this.deleteView = deleteView;
        this.parentView = parentView;
        this.canvasView = canvasView;
        this.photoEditImageView = photoEditImageView;
        this.mOnPhotoEditorListener = onPhotoEditorListener;
        if (deleteView != null) {
            outRect = new Rect(deleteView.getLeft(), deleteView.getTop(),
                    deleteView.getRight(), deleteView.getBottom());
        } else {
            outRect = new Rect(0, 0, 0, 0);
        }
        this.viewState = viewState;
    }

    void setItemRootFrameView(final View itemRootFrameView) {
        this.itemRootFrameView = itemRootFrameView;
    }

    private static float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            degrees -= 360.0f;
        } else if (degrees < -180.0f) {
            degrees += 360.0f;
        }

        return degrees;
    }

    public static void move(
            final View view,
            final TransformInfo info,
            final float editorScaleX,
            final float initialStickerScale,
            final float initialStickerRotation
    ) {
        // NOTE(kleyow): No idea what this is for but it's messing with the pivot points
        //               will screw up one touch.
        //               I saw no observable difference as to what this was accomplishing.
        // TODO(cheng): I believe this is for moving a sticker with two fingers.
        //              When pivotX and pivotY exist, they represent the focal point between
        //              multiple touches in a pinch (aka the point directly in the middle).
        //              Need to confirm this, but best way to fix this would be to skip this
        //              step if pivotX / pivotY is null due to not existing when a user is using
        //              single-touch.
        //computeRenderOffset(view, info.pivotX, info.pivotY);

        // NOTE(kleyow): No idea what this is for since translation is handled in
        //               MotionEvent.ACTION_MOVE.
        //               I saw no observable difference as to what this was accomplishing.
        // TODO(cheng): This is for placing items programatically, such as right after
        //              adding them.  It doesn't matter for scale, but it can matter for
        //              others.  Turn this back on in the future after testing.
        //adjustTranslation(view, info.deltaX, info.deltaY);

        // NOTE(cheng): Since `ZoomLayout.getMaxZoom()` is a constant value of 4,
        //              this being NaN should be impossible.  However, this appears to be
        //              the likely candidate causing a NaN issue.
        final float baseZoomAmount = ZoomLayout.getMaxZoom() - 1;
        if (Float.isNaN(baseZoomAmount)) {
            Log.e(TAG, "NaN scale value in MultiTouchListener:151");
            return;
        }

        // Lower the floor by the same percentage of scale on the editor until a minimum.
        final float adaptiveMinimumScale = Math.max(ABSOLUTE_MINIMUM_SCALE,
                EDITOR_RELATIVE_MINIMUM_SCALE - (EDITOR_RELATIVE_MINIMUM_SCALE * (editorScaleX - 1) / baseZoomAmount));

        float scale = Math.max(
                adaptiveMinimumScale,
                Math.min(EDITOR_RELATIVE_MAXIMUM_SCALE, initialStickerScale * info.deltaScale));

        // NOTE(cheng): Since `ZoomLayout.getMaxZoom()` is a constant value of 4,
        //              this being NaN should be impossible.  However, this appears to be
        //              the likely candidate causing a NaN issue.
        if (Float.isNaN(scale)) {
            Log.e(TAG, "NaN scale value in MultiTouchListener:166");
            return;
        }

        // NOTE(cheng): When the image border is scaling, the margins
        //              do not scale with it.  This means when you are
        //              zoomed in on the main image, the margins can appear large.
        // NOTE(cheng): We only scale the inner image when it is getting scaled down (to help
        //              users make images smaller).  When it's getting scaled up (scale > 1.0),
        //              users don't need help, so we keep the scale of the inner image constant
        //              and scale only the outer border view.
        final FrameLayout imageBorderView = view.findViewById(R.id.frmBorder);
        imageBorderView.setScaleX((float) Math.min(scale, 1.0));
        imageBorderView.setScaleY((float) Math.min(scale, 1.0));

        // Scale the actual outer view
        // TODO(cheng): Change 'view' to 'graphicView'
        view.setScaleX(scale);
        view.setScaleY(scale);

        // Rotate the actual outer view.
        float rotation = adjustAngle(initialStickerRotation + info.deltaAngle);
        view.setRotation(rotation);
    }

    static void adjustTranslation(
            View graphicView,
            Matrix deltaVectorTranslationMatrix,
            float deltaX,
            float deltaY,
            float currentSelectedX,
            float currentSelectedY
    ) {
        final float[] deltaVector = {deltaX, deltaY};
        deltaVectorTranslationMatrix.mapVectors(deltaVector);
        graphicView.setTranslationX(currentSelectedX + deltaVector[0]);
        graphicView.setTranslationY(currentSelectedY + deltaVector[1]);
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

        // NOTE(cheng): This view is the root view.  E.g., the imageRootView
        if (view == viewState.getCurrentSelectedView()) {
            mScaleGestureDetector.onTouchEvent(view, event);
        }
        mGestureListener.onTouchEvent(event);

        if (!isTranslateEnabled) {
            return true;
        }

        int action = event.getAction();

        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        final Rect frmBorderHitRectangle = new Rect();
        view.findViewById(R.id.frmBorder).getHitRect(frmBorderHitRectangle);

        switch (action & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (
                        // NOTE(cheng): Disable lastImageTouchMouseDownEventWasOpaque below for testing.
                        frmBorderHitRectangle.contains((int) event.getX(), (int) event.getY())
                        && lastImageTouchMouseDownEventWasOpaque
                ) {
                    // NOTE(cheng): Use this flag to handle the case where the sticker is
                    // inside the image border frame, and also the event was prior handled by
                    // the listener attached to the image (in PhotoEditor.java)
                    // We have to do it this way so the sticker can handle it's own "pointer intersection"
                    // math to determine if the touched pixel was opaque.
                    lastImageTouchMouseDownEventWasOpaque = false;

                    // NOTE(cheng): Always handle this case (returning "true") for non-stickers (text),
                    // NOTE(cheng): It is also important to return "true" here so the GestureListener
                    //              gets triggered, which allows for selection to take place
                    //              in the case of a "fling".
                    isTouchMovable = true;
                    isCornerMovable = false;
                    mPrevX = event.getX();
                    mPrevY = event.getY();
                    mActivePointerId = event.getPointerId(0);
                    if (deleteView != null) {
                        deleteView.setVisibility(View.VISIBLE);
                    }

                    // NOTE(cheng): Re-enabling since there was a uninstall spike / ranking dropoff
                    //              after disabling this.
                    // TODO(cheng): Turn this off once we have the "bringToFront()" button existing.
                    view.bringToFront();
                    firePhotoEditorSDKListener(
                            view,
                            PhotoEditorSDKListenerMode.START_VIEW_CHANGE
                    );
                } else {
                    // If event is not on a handleView, and not within the rect of the imageView,
                    // it is within the space between the handleViews on the borders and should be ignored.
                    // In this case, do not absorb the event.
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isTouchMovable) {
                    break;
                }
                // Only enable dragging on focused stickers.
                if (view == viewState.getCurrentSelectedView()) {
                    int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                    if (pointerIndexMove != -1) {
                        float currX = event.getX(pointerIndexMove);
                        float currY = event.getY(pointerIndexMove);
                        if (!mScaleGestureDetector.isInProgress() && !scalingInProgress) {
                            adjustTranslation(
                                    view,
                                    view.getMatrix(),
                                    currX - mPrevX,
                                    currY - mPrevY,
                                    view.getX(),
                                    view.getY()
                            );
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
                    // NOTE(kleyow): Disabling this sticker out-of-bounds logic until it works with photo rotation.
                    // view.animate().translationY(0).translationY(0);
                }
                if (!isViewInBounds(photoEditImageView, x, y)) {
                    // NOTE(kleyow): Disabling this sticker out-of-bounds logic until it works with photo rotation.
                    // view.animate().translationY(0).translationY(0);
                }
                if (deleteView != null) {
                    deleteView.setVisibility(View.GONE);
                }

                isCornerMovable = false;

                // Unlock the view from translating when all fingers are lifted.
                scalingInProgress = false;

                firePhotoEditorSDKListener(
                        view,
                        PhotoEditorSDKListenerMode.STOP_VIEW_CHANGE
                );

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

    private enum PhotoEditorSDKListenerMode {
        START_VIEW_CHANGE,
        MOVE_VIEW_CHANGE,
        STOP_VIEW_CHANGE
    }

    // TODO(cheng): add 'onMoveViewChangeListener(...)' and make it fire whenever a move / resize happens
    //              hook this up to a callback in EditImageActivity that adjusts the handles.
    private void firePhotoEditorSDKListener(View view, PhotoEditorSDKListenerMode listenerMode) {
        Object viewTag = view.getTag();
        if (mOnPhotoEditorListener != null && viewTag != null && viewTag instanceof ViewType) {
            if (listenerMode == PhotoEditorSDKListenerMode.START_VIEW_CHANGE)
                mOnPhotoEditorListener.onStartViewChangeListener(((ViewType) view.getTag()));
            else if (listenerMode == PhotoEditorSDKListenerMode.STOP_VIEW_CHANGE)
                mOnPhotoEditorListener.onStopViewChangeListener(((ViewType) view.getTag()));
            else if (listenerMode == PhotoEditorSDKListenerMode.MOVE_VIEW_CHANGE)
                mOnPhotoEditorListener.onMoveViewChangeListener(((ViewType) view.getTag()));
        }
    }

    private boolean isViewInBounds(View view, int x, int y) {
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
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
            // Lock the view from translating.
            scalingInProgress = true;
            mPivotX = detector.getFocusX();
            mPivotY = detector.getFocusY();
            mPrevSpanVector.set(detector.getCurrentSpanVector());
            return mIsPinchScalable;
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
            MultiTouchListener.move(
                    view,
                    info,
                    parentView.getScaleX(),
                    view.getScaleX(),
                    view.getRotation()

            );
            return !mIsPinchScalable;
        }
    }

    public static class TransformInfo {
        public float deltaX;
        public float deltaY;
        public float deltaScale;
        public float deltaAngle;
        public Float pivotX;
        public Float pivotY;
    }

    interface OnMultiTouchListener {
        void onEditTextClickListener(String text, int colorCode);

        void onRemoveViewListener(View removedView);
    }

    interface OnGestureControl {
        void onClick();

        void onLongClick();

        void onDown();

        void onFling();
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
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocity1, float velocity2) {
            if (mOnGestureControl != null) {
                mOnGestureControl.onFling();
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (mOnGestureControl != null) {
                mOnGestureControl.onDown();
            }
            return true;
        }
    }
}