package ja.burhanrashid52.photoeditor

import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https:></https:>//github.com/burhanrashid52>
 */
internal class Text(private val mPhotoEditorView: ViewGroup,
                    private val mMultiTouchListener: MultiTouchListener,
                    private val mViewState: PhotoEditorViewState,
                    private val mDefaultTextTypeface: Typeface?,
                    private val mGraphicManager: GraphicManager
) : Graphic(mPhotoEditorView.context, mGraphicManager) {
    private var mTextView: TextView? = null
    fun buildView(text: String?, styleBuilder: TextStyleBuilder?) {
        mTextView!!.text = text
        styleBuilder?.applyStyle(mTextView!!)
    }

    private fun setupGesture() {
        val onGestureControl = buildGestureController(mPhotoEditorView, mViewState)
        mMultiTouchListener.setOnGestureControl(onGestureControl)
        val rootView = rootView
        rootView.setOnTouchListener(mMultiTouchListener)
    }

    override val viewType: ViewType
        get() = ViewType.TEXT
    override val layoutId: Int
        get() = R.layout.view_photo_editor_text

    override fun setupView(rootView: View?) {
        mTextView = rootView!!.findViewById(R.id.tvPhotoEditorText)
        if (mTextView != null && mDefaultTextTypeface != null) {
            mTextView!!.gravity = Gravity.CENTER
            mTextView!!.typeface = mDefaultTextTypeface
        }
    }

    override fun updateView(view: View?) {
        val textInput = mTextView!!.text.toString()
        val currentTextColor = mTextView!!.currentTextColor
        val photoEditorListener = mGraphicManager.onPhotoEditorListener
        photoEditorListener?.onEditTextChangeListener(view, textInput, currentTextColor)
    }

    init {
        setupGesture()
    }
}