package com.burhanrashid52.photoediting

import android.view.View
import ja.burhanrashid52.photoeditor.MultiTouchListener
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.ZoomLayout

object Helper {

    fun realignNewGraphicToCanvas(mPhotoEditorView: PhotoEditorView, graphicRootView: View) {
        val info = MultiTouchListener.TransformInfo()

        val parentView: View = mPhotoEditorView.parentLayout
        val canvasView: View = mPhotoEditorView.canvasLayout

        // Set the initial scale change to be relative to the zoom of the editor
        info.deltaScale = Math.max(
            0.2f,
            1 - 1 * (parentView.scaleX - 1) / (ZoomLayout.getMaxZoom() - 1)
        )

        // Offset any rotation to the editor view so the sticker is right side up
        // when not placed using MLkit.
        info.deltaAngle = -canvasView.rotation
        info.deltaX = 0.0f
        info.deltaY = 0.0f
        info.pivotX = null
        info.pivotY = null
        MultiTouchListener.move(
            graphicRootView,
            info,
            parentView.scaleX,
            graphicRootView.scaleX,
            graphicRootView.rotation
        )

        // NOTE(cheng): Doing translation here since the translation method
        //              of the MultiTouchListener.move() function does not
        //              currently work
        graphicRootView.translationX =
            -parentView.translationX / parentView.scaleX +
                    350 / 2f * (graphicRootView.scaleX - 1)
        graphicRootView.translationY =
            -parentView.translationY / parentView.scaleY +
                    350 / 2f * (graphicRootView.scaleY - 1)
    }
}