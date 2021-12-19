package ja.burhanrashid52.photoeditor

import android.content.Context
import ja.burhanrashid52.photoeditor.BoxHelper.clearHelperBox
import ja.burhanrashid52.photoeditor.GraphicManager
import ja.burhanrashid52.photoeditor.ViewType
import android.view.LayoutInflater
import android.view.View
import ja.burhanrashid52.photoeditor.R
import android.view.ViewGroup
import android.widget.ImageView
import ja.burhanrashid52.photoeditor.PhotoEditorViewState
import ja.burhanrashid52.photoeditor.MultiTouchListener.OnGestureControl
import ja.burhanrashid52.photoeditor.BoxHelper
import java.lang.UnsupportedOperationException

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https:></https:>//github.com/burhanrashid52>
 */
internal abstract class Graphic {
    val rootView: View
    private val mGraphicManager: GraphicManager
    abstract val viewType: ViewType
    abstract val layoutId: Int
    abstract fun setupView(rootView: View?)
    open fun updateView(view: View?) {
        //Optional for subclass to override
    }

    constructor(context: Context, graphicManager: GraphicManager) {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (layoutId == 0) {
            throw UnsupportedOperationException("Layout id cannot be zero. Please define a layout")
        }
        rootView = layoutInflater.inflate(layoutId, null)
        mGraphicManager = graphicManager
        setupView(rootView)
        setupRemoveView(rootView)
    }

    constructor(rootView: View, graphicManager: GraphicManager) {
        this.rootView = rootView
        mGraphicManager = graphicManager
        setupView(this.rootView)
        setupRemoveView(this.rootView)
    }

    private fun setupRemoveView(rootView: View) {
        //We are setting tag as ViewType to identify what type of the view it is
        //when we remove the view from stack i.e onRemoveViewListener(ViewType viewType, int numberOfAddedViews);
        val viewType = viewType
        rootView.tag = viewType
        val imgClose = rootView.findViewById<ImageView>(R.id.imgPhotoEditorClose)
        imgClose?.setOnClickListener { mGraphicManager.removeView(this@Graphic) }
    }

    protected fun toggleSelection() {
        val frmBorder = rootView.findViewById<View>(R.id.frmBorder)
        val imgClose = rootView.findViewById<View>(R.id.imgPhotoEditorClose)
        if (frmBorder != null) {
            frmBorder.setBackgroundResource(R.drawable.rounded_border_tv)
            frmBorder.tag = true
        }
        if (imgClose != null) {
            imgClose.visibility = View.VISIBLE
        }
    }

    protected fun buildGestureController(
        viewGroup: ViewGroup?,
        viewState: PhotoEditorViewState
    ): OnGestureControl {
        val boxHelper = BoxHelper(viewGroup!!, viewState)
        return object : OnGestureControl {
            override fun onClick() {
                boxHelper.clearHelperBox()
                toggleSelection()
                // Change the in-focus view
                viewState.currentSelectedView = rootView
            }

            override fun onLongClick() {
                updateView(rootView)
            }
        }
    }
}