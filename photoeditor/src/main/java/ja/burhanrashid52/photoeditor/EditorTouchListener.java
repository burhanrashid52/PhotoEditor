package ja.burhanrashid52.photoeditor;

import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

/**
 * Touch listener for main editor. Used for resizing the main image, background,
 * and currently selected image.
 *
 * TODO(cheng): Collapse this logic with MultiTouchListener. Use callbacks or a static method call.
 *
 * @author Leylow
 */
class EditorTouchListener implements OnTouchListener {

    private static final String TAG = "EditorTouchListener";

    private static final int INVALID_POINTER_ID = -1;
    private final GestureDetector mGestureListener;
    private boolean isRotateEnabled = true;
    private boolean isTranslateEnabled = true;

    // NOTE(cheng): Appears inert, can be removed
    private boolean isScaleEnabled = true;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mPrevX, mPrevY;
    private ScaleGestureDetector mScaleGestureDetector;

    private RelativeLayout parentView;
    private RelativeLayout canvasView;

    private BoxHelper boxHelper;

    private OnPhotoEditorListener mOnPhotoEditorListener;

    private boolean isTouchMovable = false;
    private PhotoEditorViewState viewState;
    private float currentSelectedX, currentSelectedY;
    private boolean scalingInProgress = false;

    EditorTouchListener(final RelativeLayout parentView,
                        final RelativeLayout canvasView,
                        final PhotoEditorViewState viewState
    ) {
        mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());
        mGestureListener = new GestureDetector(new GestureListener());
        this.parentView = parentView;
        this.canvasView = canvasView;
        this.viewState = viewState;
        this.boxHelper =  new BoxHelper(canvasView, viewState);
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(view, event);
        mGestureListener.onTouchEvent(event);

        if (!isTranslateEnabled) {
            return true;
        }

        int action = event.getAction();

        switch (action & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouch: " + view.toString());
                if (viewState.getCurrentSelectedView() == null) {
                    return false;
                }

                isTouchMovable = true;

                mPrevX = event.getX();
                mPrevY = event.getY();
                currentSelectedX = viewState.getCurrentSelectedView().getX();
                currentSelectedY = viewState.getCurrentSelectedView().getY();
                mActivePointerId = event.getPointerId(0);
                if (mOnPhotoEditorListener != null)
                    mOnPhotoEditorListener.onStartViewChangeListener(((ViewType) viewState.getCurrentSelectedView().getTag()));

                // Determining if touch event is in a view reference
                // https://stackoverflow.com/a/11370966/6130890
                break;
            case MotionEvent.ACTION_MOVE:
                // Initiates a move on the currently selected sticker.
                // NOTE(cheng): This is *not* for moving the background.
                // NOTE(cheng): Only enable dragging on focused stickers.
                if (isTouchMovable && viewState.getCurrentSelectedView() != null) {
                    // TODO(cheng): There's a weird bug happening here where a user will
                    //              pinch, but the sticker will translate instead of resize.
                    //              It's likely (1) state between this and MultiTouchListener,
                    //              (2) incorrectly dealing with the event pointers, or (3)
                    //              incorrectly tracking scalingInProgress state.
                    int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                    if (pointerIndexMove != -1) {
                        float currX = event.getX(pointerIndexMove);
                        float currY = event.getY(pointerIndexMove);
                        if (!mScaleGestureDetector.isInProgress() && !scalingInProgress) {

                            // Translate the delta vector (movement) by the current orientation
                            // of the canvas view
                            Matrix deltaVectorTranslationMatrix = new Matrix();
                            // https://stackoverflow.com/a/25381660
                            deltaVectorTranslationMatrix.set(canvasView.getMatrix());
                            // Negate and reverse compensate any rotation the canvas view has so that
                            // the focused view follows user's finger.
                            deltaVectorTranslationMatrix.postRotate(-(canvasView.getRotation() * 2));

                            MultiTouchListener.adjustTranslation(
                                    viewState.getCurrentSelectedView(),
                                    deltaVectorTranslationMatrix,
                                    currX - mPrevX,
                                    currY - mPrevY,
                                    currentSelectedX,
                                    currentSelectedY
                            );
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_UP:
                // https://stackoverflow.com/a/25277069
                view.performClick();
                mActivePointerId = INVALID_POINTER_ID;

                isTouchMovable = false;

                // Unlock the view from translating when all fingers are lifted.
                scalingInProgress = false;

                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onStopViewChangeListener(ViewType.IMAGE);
                }
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

    // NOTE(cheng): Scaling logic used to scale *currently selected graphic*,
    //              *not* the background. Background scaling is handled by ZoomLayout.
    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private float mPivotX, mPivotY, initialScale, initialRotation;
        private final Vector2D mPrevSpanVector = new Vector2D();

        @Override
        public boolean onScaleBegin(View view, ScaleGestureDetector detector) {
            // Lock the view from translating.
            scalingInProgress = true;
            mPivotX = detector.getFocusX();
            mPivotY = detector.getFocusY();
            if (viewState.getCurrentSelectedView() != null) {
                initialScale = viewState.getCurrentSelectedView().getScaleX();
                initialRotation = viewState.getCurrentSelectedView().getRotation();
            }
            mPrevSpanVector.set(detector.getCurrentSpanVector());

            // Mark event as "handled / absorbed" to begin scaling action.
            return true;
        }

        @Override
        public boolean onScale(View view, ScaleGestureDetector detector) {
            final MultiTouchListener.TransformInfo info = new MultiTouchListener.TransformInfo();
            info.deltaScale = isScaleEnabled ? detector.getScaleFactor() : 1.0f;
            info.deltaAngle = isRotateEnabled ? Vector2D.getAngle(mPrevSpanVector, detector.getCurrentSpanVector()) : 0.0f;
            info.deltaX = isTranslateEnabled ? detector.getFocusX() - mPivotX : 0.0f;
            info.deltaY = isTranslateEnabled ? detector.getFocusY() - mPivotY : 0.0f;
            info.pivotX = mPivotX;
            info.pivotY = mPivotY;
            if (viewState.getCurrentSelectedView() != null) {
                MultiTouchListener.move(
                        viewState.getCurrentSelectedView(),
                        info,
                        parentView.getScaleX(),
                        initialScale,
                        initialRotation
                );
            }

            // Mark event as "unhandled" so scale continues to accumulate.
            // See OnScaleGestureListener.onScale()
            return false;
        }
    }

    void setOnPhotoEditorListener(OnPhotoEditorListener onPhotoEditorListener) {
        this.mOnPhotoEditorListener = onPhotoEditorListener;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            boxHelper.clearHelperBox();
            if (mOnPhotoEditorListener != null) {
                mOnPhotoEditorListener.onInFocusViewChangeListener(null);
            }
            return true;
        }
    }
}