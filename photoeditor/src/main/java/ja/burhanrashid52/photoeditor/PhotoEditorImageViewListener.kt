package ja.burhanrashid52.photoeditor

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent

// A listener for the image view that helps with the focus view logic.
// i.e when you press on an empty space without stickers, it will de-select the focused sticker.
internal class PhotoEditorImageViewListener(
    private val viewState: PhotoEditorViewState,
    private val onSingleTapUpCallback: OnSingleTapUpCallback
) : SimpleOnGestureListener() {
    internal interface OnSingleTapUpCallback {
        fun onSingleTapUp()
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        onSingleTapUpCallback.onSingleTapUp()
        // Returning false when there is no in focus view will pass the
        // touch event to the zoom layout logic.
        return viewState.currentSelectedView != null
    }

    override fun onDown(e: MotionEvent) = viewState.currentSelectedView != null

    override fun onFling(
        event1: MotionEvent,
        event2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean = viewState.currentSelectedView != null

    override fun onScroll(
        event1: MotionEvent, event2: MotionEvent, distanceX: Float,
        distanceY: Float
    ) = viewState.currentSelectedView != null

    override fun onDoubleTap(event: MotionEvent) = viewState.currentSelectedView != null

    override fun onDoubleTapEvent(event: MotionEvent) = viewState.currentSelectedView != null

    override fun onSingleTapConfirmed(event: MotionEvent) = viewState.currentSelectedView != null
}