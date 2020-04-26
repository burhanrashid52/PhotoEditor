package ja.burhanrashid52.photoeditor;

import android.view.GestureDetector;
import android.view.MotionEvent;

// A listener for the image view that helps with the focus view logic.
// i.e when you press on an empty space without stickers, it will de-select the focused sticker.
class PhotoEditorImageViewListener extends GestureDetector.SimpleOnGestureListener {

    interface OnSingleTapUpCallback {
        void onSingleTapUp();
    }

    private final OnSingleTapUpCallback onSingleTapUpCallback;
    private final PhotoEditorViewState viewState;

    PhotoEditorImageViewListener(
            final PhotoEditorViewState viewState,
            final OnSingleTapUpCallback onSingleTapUpCallback) {
        this.viewState = viewState;
        this.onSingleTapUpCallback = onSingleTapUpCallback;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        onSingleTapUpCallback.onSingleTapUp();
        // Returning false when there is no in focus view will pass the
        // touch event to the zoom layout logic.
        return !(viewState.getCurrentSelectedView() == null);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return !(viewState.getCurrentSelectedView() == null);
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        return !(viewState.getCurrentSelectedView() == null);
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        return !(viewState.getCurrentSelectedView() == null);
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        return !(viewState.getCurrentSelectedView() == null);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        return !(viewState.getCurrentSelectedView() == null);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        return !(viewState.getCurrentSelectedView() == null);
    }
}