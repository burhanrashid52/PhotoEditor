package com.burhanrashid52.photoediting

import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import ja.burhanrashid52.photoeditor.*
import ja.burhanrashid52.photoeditor.R

class GraphicHelper(
    private val photoEditor: PhotoEditor,
    private val photoEditorListener: OnPhotoEditorListener
) {

    enum class OnTouchHandleType {
        TOP_RIGHT,
        TOP_LEFT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT
    }

    fun addTouchHandleCallbacks(
        stickerImageRootView: View
    ) {

        // TODO(cheng): Turn this into a map / loop rather than doing all 4 manually.
        val topLeftHandleView: ImageView =
            stickerImageRootView.findViewById(R.id.imgHandleTopLeft)
        topLeftHandleView.setOnTouchListener { _, touchEvent ->
            handleOnTouchListenerHandler(
                OnTouchHandleType.TOP_LEFT,
                topLeftHandleView,
                stickerImageRootView,
                touchEvent
            )
        }

        val topRightHandleView: ImageView =
            stickerImageRootView.findViewById(R.id.imgHandleTopRight)
        topRightHandleView.setOnTouchListener { _, touchEvent ->
            handleOnTouchListenerHandler(
                OnTouchHandleType.TOP_RIGHT,
                topRightHandleView,
                stickerImageRootView,
                touchEvent
            )
        }

        val bottomLeftHandleView: ImageView =
            stickerImageRootView.findViewById(R.id.imgHandleBottomLeft)
        bottomLeftHandleView.setOnTouchListener { _, touchEvent ->
            handleOnTouchListenerHandler(
                OnTouchHandleType.BOTTOM_LEFT,
                bottomLeftHandleView,
                stickerImageRootView,
                touchEvent
            )
        }

        val bottomRightHandleView: ImageView =
            stickerImageRootView.findViewById(R.id.imgHandleBottomRight)
        bottomRightHandleView.setOnTouchListener { _, touchEvent ->
            handleOnTouchListenerHandler(
                OnTouchHandleType.BOTTOM_RIGHT,
                bottomRightHandleView,
                stickerImageRootView,
                touchEvent
            )
        }
    }

    fun handleOnTouchListenerHandler(
        onTouchHandleType: OnTouchHandleType,
        targetHandleView: ImageView,
        stickerImageRootView: View,
        touchEvent: MotionEvent
    ): Boolean {

        // TODO(cheng): Write exception if this returns false
        val multiTouchListener: MultiTouchListener = photoEditor
            .viewState
            .multiTouchListenerByView[stickerImageRootView]
            ?: return false

        when(touchEvent.action and touchEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

                // Do not absorb event if handle is not visible
                if (targetHandleView.visibility != View.VISIBLE) {
                    return false
                }

                // NOTE(cheng): By design, the entire handle object is clickable, including the
                //              transparent part.  This makes for a larger target for touching.
                multiTouchListener.isCornerMovable = true
                multiTouchListener.isTouchMovable = false

                multiTouchListener.editorScale = multiTouchListener.parentView.scaleX

                multiTouchListener.centerX = (multiTouchListener.itemRootFrameView.left + multiTouchListener.itemRootFrameView.right) / 2f
                multiTouchListener.centerY = (multiTouchListener.itemRootFrameView.top + multiTouchListener.itemRootFrameView.bottom) / 2f

                // Adjust width and height values to the scale of the editor.
                multiTouchListener.adjustedScaledHeight = multiTouchListener.itemRootFrameView.height * multiTouchListener.editorScale
                multiTouchListener.adjustedScaledWidth = multiTouchListener.itemRootFrameView.width * multiTouchListener.editorScale

                // Calculate the X,Y coordinates, and handle visibility, based on the handle that is being "dragged"
                when (onTouchHandleType) {
                    OnTouchHandleType.TOP_LEFT -> {
                        stickerImageRootView.findViewById<ImageView>(R.id.imgHandleTopRight)?.visibility = View.INVISIBLE
                        stickerImageRootView.findViewById<ImageView>(R.id.imgHandleBottomLeft)?.visibility = View.INVISIBLE
                        stickerImageRootView.findViewById<ImageView>(R.id.imgHandleBottomRight)?.visibility = View.INVISIBLE
                        multiTouchListener.coordinateX = (multiTouchListener.centerX - (Math.hypot(multiTouchListener.adjustedScaledWidth.toDouble(), multiTouchListener.adjustedScaledHeight.toDouble()) / 2f * multiTouchListener.itemRootFrameView.scaleX
                                * Math.cos(Math.toRadians((multiTouchListener.itemRootFrameView.getRotation() + multiTouchListener.canvasView.getRotation()).toDouble()) + Math.atan2(multiTouchListener.adjustedScaledHeight.toDouble(), multiTouchListener.adjustedScaledWidth.toDouble())))).toFloat()
                        multiTouchListener.coordinateY = (multiTouchListener.centerY - (Math.hypot(multiTouchListener.adjustedScaledWidth.toDouble(), multiTouchListener.adjustedScaledHeight.toDouble()) / 2f * multiTouchListener.itemRootFrameView.getScaleX()
                                * Math.sin(Math.toRadians((multiTouchListener.itemRootFrameView.getRotation() + multiTouchListener.canvasView.getRotation()).toDouble()) + Math.atan2(multiTouchListener.adjustedScaledHeight.toDouble(), multiTouchListener.adjustedScaledWidth.toDouble())))).toFloat()
                    }
                    OnTouchHandleType.TOP_RIGHT -> {
                        stickerImageRootView.findViewById<ImageView>(R.id.imgHandleTopLeft)?.visibility = View.INVISIBLE
                        stickerImageRootView.findViewById<ImageView>(R.id.imgHandleBottomLeft)?.visibility = View.INVISIBLE
                        stickerImageRootView.findViewById<ImageView>(R.id.imgHandleBottomRight)?.visibility = View.INVISIBLE
                        multiTouchListener.coordinateX = (multiTouchListener.centerX + (Math.hypot(multiTouchListener.adjustedScaledWidth.toDouble(), multiTouchListener.adjustedScaledHeight.toDouble()) / 2f * multiTouchListener.itemRootFrameView.scaleX
                                * Math.sin(Math.toRadians((multiTouchListener.itemRootFrameView.getRotation() + multiTouchListener.canvasView.getRotation()).toDouble()) + Math.atan2(multiTouchListener.adjustedScaledHeight.toDouble(), multiTouchListener.adjustedScaledWidth.toDouble())))).toFloat()
                        multiTouchListener.coordinateY = (multiTouchListener.centerY - (Math.hypot(multiTouchListener.adjustedScaledWidth.toDouble(), multiTouchListener.adjustedScaledHeight.toDouble()) / 2f * multiTouchListener.itemRootFrameView.scaleX
                                * Math.cos(Math.toRadians((multiTouchListener.itemRootFrameView.getRotation() + multiTouchListener.canvasView.getRotation()).toDouble()) + Math.atan2(multiTouchListener.adjustedScaledHeight.toDouble(), multiTouchListener.adjustedScaledWidth.toDouble())))).toFloat()
                    }
                    OnTouchHandleType.BOTTOM_LEFT -> {
                        stickerImageRootView.findViewById<ImageView>(R.id.imgHandleTopLeft)?.visibility = View.INVISIBLE
                        stickerImageRootView.findViewById<ImageView>(R.id.imgHandleTopRight)?.visibility = View.INVISIBLE
                        stickerImageRootView.findViewById<ImageView>(R.id.imgHandleBottomRight)?.visibility = View.INVISIBLE
                        multiTouchListener.coordinateX = (multiTouchListener.centerX - (Math.hypot(multiTouchListener.adjustedScaledWidth.toDouble(), multiTouchListener.adjustedScaledHeight.toDouble()) / 2f * multiTouchListener.itemRootFrameView.getScaleX()
                                * Math.sin(Math.toRadians((multiTouchListener.itemRootFrameView.getRotation() + multiTouchListener.canvasView.getRotation()).toDouble()) + Math.atan2(multiTouchListener.adjustedScaledHeight.toDouble(), multiTouchListener.adjustedScaledWidth.toDouble())))).toFloat()
                        multiTouchListener.coordinateY = (multiTouchListener.centerY + (Math.hypot(multiTouchListener.adjustedScaledWidth.toDouble(), multiTouchListener.adjustedScaledHeight.toDouble()) / 2f * multiTouchListener.itemRootFrameView.getScaleX()
                                * Math.cos(Math.toRadians((multiTouchListener.itemRootFrameView.getRotation() + multiTouchListener.canvasView.getRotation()).toDouble()) + Math.atan2(multiTouchListener.adjustedScaledHeight.toDouble(), multiTouchListener.adjustedScaledWidth.toDouble())))).toFloat()
                    }
                    OnTouchHandleType.BOTTOM_RIGHT -> {
                        stickerImageRootView.findViewById<ImageView>(R.id.imgHandleTopLeft)?.visibility = View.INVISIBLE
                        stickerImageRootView.findViewById<ImageView>(R.id.imgHandleTopRight)?.visibility = View.INVISIBLE
                        stickerImageRootView.findViewById<ImageView>(R.id.imgHandleBottomLeft)?.visibility = View.INVISIBLE
                        multiTouchListener.coordinateX = (multiTouchListener.centerX + (Math.hypot(multiTouchListener.adjustedScaledWidth.toDouble(), multiTouchListener.adjustedScaledHeight.toDouble()) / 2f * multiTouchListener.itemRootFrameView.getScaleX()
                                * Math.cos(Math.toRadians((multiTouchListener.itemRootFrameView.getRotation() + multiTouchListener.canvasView.getRotation()).toDouble()) + Math.atan2(multiTouchListener.adjustedScaledHeight.toDouble(), multiTouchListener.adjustedScaledWidth.toDouble())))).toFloat()
                        multiTouchListener.coordinateY = (multiTouchListener.centerY + (Math.hypot(multiTouchListener.adjustedScaledWidth.toDouble(), multiTouchListener.adjustedScaledHeight.toDouble()) / 2f * multiTouchListener.itemRootFrameView.getScaleX()
                                * Math.sin(Math.toRadians((multiTouchListener.itemRootFrameView.getRotation() + multiTouchListener.canvasView.getRotation()).toDouble()) + Math.atan2(multiTouchListener.adjustedScaledHeight.toDouble(), multiTouchListener.adjustedScaledWidth.toDouble())))).toFloat()
                    }
                }

                multiTouchListener.startX = touchEvent.rawX - multiTouchListener.coordinateX + multiTouchListener.centerX
                multiTouchListener.startY = touchEvent.rawY - multiTouchListener.coordinateY + multiTouchListener.centerY

                multiTouchListener.startR = Math.hypot(
                    (touchEvent.rawX -  multiTouchListener.startX).toDouble(),
                    (touchEvent.rawY - multiTouchListener.startY).toDouble()
                ).toFloat()
                multiTouchListener.startA = Math.toDegrees(
                    Math.atan2(
                        (touchEvent.rawY - multiTouchListener.startY).toDouble(),
                        (touchEvent.rawX - multiTouchListener.startX).toDouble()
                    )
                ).toFloat()

                multiTouchListener.startScale = multiTouchListener.itemRootFrameView.getScaleX()
                multiTouchListener.startRotation = multiTouchListener.itemRootFrameView.getRotation()

                // NOTE(cheng): Disabling this until we can find a way to make it
                //              so the drag handle isn't disabled (which happens on
                //              on a normal translation drag vs a handle drag)
                // onStartViewChangeListener.invoke(
                //      (stickerImageRootView.tag as ViewType)
                // )

                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val newRadius = Math.hypot(
                    (touchEvent.rawX - multiTouchListener.startX).toDouble(),
                    (touchEvent.rawY - multiTouchListener.startY).toDouble()
                ).toFloat()
                val newAngle = Math.toDegrees(
                    Math.atan2(
                        (touchEvent.rawY - multiTouchListener.startY).toDouble(),
                        (touchEvent.rawX - multiTouchListener.startX).toDouble()
                    )
                ).toFloat()
                val newDeltaScale: Float = newRadius / multiTouchListener.startR

                val info = MultiTouchListener.TransformInfo()
                info.deltaScale = newDeltaScale
                info.deltaAngle = newAngle - multiTouchListener.startA
                info.deltaX = 0.0f
                info.deltaY = 0.0f
                info.pivotX = null
                info.pivotY = null
                MultiTouchListener.move(
                    multiTouchListener.itemRootFrameView,
                    info,
                    multiTouchListener.parentView.scaleX,
                    multiTouchListener.startScale,
                    multiTouchListener.startRotation
                )

                photoEditorListener.onMoveViewChangeListener((stickerImageRootView.tag as ViewType))

                return true
            }
            MotionEvent.ACTION_UP -> {
                stickerImageRootView.findViewById<ImageView>(R.id.imgHandleTopRight)?.visibility = View.VISIBLE
                stickerImageRootView.findViewById<ImageView>(R.id.imgHandleBottomLeft)?.visibility = View.VISIBLE
                stickerImageRootView.findViewById<ImageView>(R.id.imgHandleBottomRight)?.visibility = View.VISIBLE
                stickerImageRootView.findViewById<ImageView>(R.id.imgHandleTopLeft)?.visibility = View.VISIBLE
                photoEditorListener.onStopViewChangeListener((stickerImageRootView.tag as ViewType))
                return true
            }
            else -> {
                return false
            }
        }
    }
}