package ja.burhanrashid52.photoeditor

import android.view.MotionEvent
import android.view.View

/**
 * @author [Burhanuddin Rashid](https://github.com/burhanrashid52)
 * @version 0.1.1
 * @since 18/01/2017
 *
 *
 * This are the callbacks when any changes happens while editing the photo to make and custimization
 * on client side
 *
 */
interface OnPhotoEditorListener {
    /**
     * When user long press the existing text this event will trigger implying that user want to
     * edit the current [android.widget.TextView]
     *
     * @param rootView  view on which the long press occurs
     * @param text      current text set on the view
     * @param colorCode current color value set on view
     */
    fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int)

    /**
     * This is a callback when user adds any view on the [PhotoEditorView] it can be
     * brush,text or sticker i.e bitmap on parent view
     *
     * @param viewType           enum which define type of view is added
     * @param numberOfAddedViews number of views currently added
     * @see ViewType
     */
    fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int)

    /**
     * This is a callback when user remove any view on the [PhotoEditorView] it happens when usually
     * undo and redo happens or text is removed
     *
     * @param viewType           enum which define type of view is added
     * @param numberOfAddedViews number of views currently added
     */
    fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int)

    /**
     * A callback when user start dragging a view which can be
     * any of [ViewType]
     *
     * @param viewType enum which define type of view is added
     */
    fun onStartViewChangeListener(viewType: ViewType?)

    /**
     * A callback when user stop/up touching a view which can be
     * any of [ViewType]
     *
     * @param viewType enum which define type of view is added
     */
    fun onStopViewChangeListener(viewType: ViewType?)

    /**
     * A callback when the user touches the screen.
     *
     * @param event the MotionEvent associated to the touch.
     */
    fun onTouchSourceImage(event: MotionEvent?)
}