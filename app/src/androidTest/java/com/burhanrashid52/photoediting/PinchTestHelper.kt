package com.burhanrashid52.photoediting

import android.graphics.Point
import android.os.SystemClock
import android.view.MotionEvent
import android.view.MotionEvent.PointerProperties
import android.view.View
import androidx.test.espresso.InjectEventSecurityException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import java.lang.RuntimeException
import kotlin.Throws

/**
 * Helper used to test "pinch" actions.
 * Taken from https://stackoverflow.com/a/46443628
 */
object PinchTestHelper {
    fun pinchOut(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isEnabled()
            }

            override fun getDescription(): String {
                return "Pinch out"
            }

            override fun perform(uiController: UiController, view: View) {
                val middlePosition = getCenterPoint(view)

                // How far from the center point each finger should start
                // (note: If you make this 0, where the two fingers are on top of one another, the view may disappear)
                val startDelta = 10

                // How far from the center point each finger should end
                // (note: Be sure to have this large enough so that the gesture is recognized)
                val endDelta = 20
                val startPoint1 = Point(middlePosition.x - startDelta, middlePosition.y)
                val startPoint2 = Point(middlePosition.x + startDelta, middlePosition.y)
                val endPoint1 = Point(middlePosition.x - endDelta, middlePosition.y)
                val endPoint2 = Point(middlePosition.x + endDelta, middlePosition.y)
                performPinch(uiController, startPoint1, startPoint2, endPoint1, endPoint2)
            }
        }
    }

    fun pinchIn(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isEnabled()
            }

            override fun getDescription(): String {
                return "Pinch in"
            }

            override fun perform(uiController: UiController, view: View) {
                val middlePosition = getCenterPoint(view)
                val startDelta =
                    500 // How far from the center point each finger should start (note: Be sure to have this large enough so that the gesture is recognized!)
                val endDelta = 0 // How far from the center point each finger should end
                val startPoint1 = Point(middlePosition.x - startDelta, middlePosition.y)
                val startPoint2 = Point(middlePosition.x + startDelta, middlePosition.y)
                val endPoint1 = Point(middlePosition.x - endDelta, middlePosition.y)
                val endPoint2 = Point(middlePosition.x + endDelta, middlePosition.y)
                performPinch(uiController, startPoint1, startPoint2, endPoint1, endPoint2)
            }
        }
    }

    private fun getCenterPoint(view: View): Point {
        val locationOnScreen = IntArray(2)
        view.getLocationOnScreen(locationOnScreen)
        val viewHeight = view.height * view.scaleY
        val viewWidth = view.width * view.scaleX
        return Point(
            (locationOnScreen[0] + viewWidth / 2).toInt(),
            (locationOnScreen[1] + viewHeight / 2).toInt()
        )
    }

    private fun performPinch(
        uiController: UiController,
        startPoint1: Point,
        startPoint2: Point,
        endPoint1: Point,
        endPoint2: Point
    ) {
        val duration = 500
        val eventMinInterval: Long = 10
        val startTime = SystemClock.uptimeMillis()
        var eventTime = startTime
        var event: MotionEvent
        var eventX1: Float
        var eventY1: Float
        var eventX2: Float
        var eventY2: Float
        eventX1 = startPoint1.x.toFloat()
        eventY1 = startPoint1.y.toFloat()
        eventX2 = startPoint2.x.toFloat()
        eventY2 = startPoint2.y.toFloat()

        // Specify the property for the two touch points
        val properties = arrayOfNulls<PointerProperties>(2)
        val pp1 = PointerProperties()
        pp1.id = 0
        pp1.toolType = MotionEvent.TOOL_TYPE_FINGER
        val pp2 = PointerProperties()
        pp2.id = 1
        pp2.toolType = MotionEvent.TOOL_TYPE_FINGER
        properties[0] = pp1
        properties[1] = pp2

        // Specify the coordinations of the two touch points
        // NOTE: you MUST set the pressure and size value, or it doesn't work
        val pointerCoords = arrayOfNulls<MotionEvent.PointerCoords>(2)
        val pc1 = MotionEvent.PointerCoords()
        pc1.x = eventX1
        pc1.y = eventY1
        pc1.pressure = 1f
        pc1.size = 1f
        val pc2 = MotionEvent.PointerCoords()
        pc2.x = eventX2
        pc2.y = eventY2
        pc2.pressure = 1f
        pc2.size = 1f
        pointerCoords[0] = pc1
        pointerCoords[1] = pc2

        /*
         * Events sequence of zoom gesture:
         *
         * 1. Send ACTION_DOWN event of one start point
         * 2. Send ACTION_POINTER_DOWN of two start points
         * 3. Send ACTION_MOVE of two middle points
         * 4. Repeat step 3 with updated middle points (x,y), until reach the end points
         * 5. Send ACTION_POINTER_UP of two end points
         * 6. Send ACTION_UP of one end point
         */try {
            // Step 1
            event = MotionEvent.obtain(
                startTime, eventTime,
                MotionEvent.ACTION_DOWN, 1, properties,
                pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0
            )
            injectMotionEventToUiController(uiController, event)

            // Step 2
            event = MotionEvent.obtain(
                startTime,
                eventTime,
                MotionEvent.ACTION_POINTER_DOWN + (pp2.id shl MotionEvent.ACTION_POINTER_INDEX_SHIFT),
                2,
                properties,
                pointerCoords,
                0,
                0,
                1f,
                1f,
                0,
                0,
                0,
                0
            )
            injectMotionEventToUiController(uiController, event)

            // Step 3, 4
            val moveEventNumber = duration / eventMinInterval
            val stepX1: Float
            val stepY1: Float
            val stepX2: Float
            val stepY2: Float
            stepX1 = (endPoint1.x - startPoint1.x) / moveEventNumber.toFloat()
            stepY1 = (endPoint1.y - startPoint1.y) / moveEventNumber.toFloat()
            stepX2 = (endPoint2.x - startPoint2.x) / moveEventNumber.toFloat()
            stepY2 = (endPoint2.y - startPoint2.y) / moveEventNumber.toFloat()
            for (i in 0 until moveEventNumber) {
                // Update the move events
                eventTime += eventMinInterval
                eventX1 += stepX1
                eventY1 += stepY1
                eventX2 += stepX2
                eventY2 += stepY2
                pc1.x = eventX1
                pc1.y = eventY1
                pc2.x = eventX2
                pc2.y = eventY2
                pointerCoords[0] = pc1
                pointerCoords[1] = pc2
                event = MotionEvent.obtain(
                    startTime, eventTime,
                    MotionEvent.ACTION_MOVE, 2, properties,
                    pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0
                )
                injectMotionEventToUiController(uiController, event)
            }

            // Step 5
            pc1.x = endPoint1.x.toFloat()
            pc1.y = endPoint1.y.toFloat()
            pc2.x = endPoint2.x.toFloat()
            pc2.y = endPoint2.y.toFloat()
            pointerCoords[0] = pc1
            pointerCoords[1] = pc2
            eventTime += eventMinInterval
            event = MotionEvent.obtain(
                startTime,
                eventTime,
                MotionEvent.ACTION_POINTER_UP + (pp2.id shl MotionEvent.ACTION_POINTER_INDEX_SHIFT),
                2,
                properties,
                pointerCoords,
                0,
                0,
                1f,
                1f,
                0,
                0,
                0,
                0
            )
            injectMotionEventToUiController(uiController, event)

            // Step 6
            eventTime += eventMinInterval
            event = MotionEvent.obtain(
                startTime, eventTime,
                MotionEvent.ACTION_UP, 1, properties,
                pointerCoords, 0, 0, 1f, 1f, 0, 0, 0, 0
            )
            injectMotionEventToUiController(uiController, event)
        } catch (e: InjectEventSecurityException) {
            throw RuntimeException("Could not perform pinch", e)
        }
    }

    /**
     * Safely call uiController.injectMotionEvent(event): Detect any error and "convert" it to an
     * IllegalStateException
     */
    @Throws(InjectEventSecurityException::class)
    private fun injectMotionEventToUiController(uiController: UiController, event: MotionEvent) {
        val injectEventSucceeded = uiController.injectMotionEvent(event)
        check(injectEventSucceeded) { "Error performing event $event" }
    }
}