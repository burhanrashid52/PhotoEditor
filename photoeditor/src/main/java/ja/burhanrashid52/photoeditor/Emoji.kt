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
internal class Emoji(private val mPhotoEditorView: ViewGroup,
                     private val mMultiTouchListener: MultiTouchListener,
                     private val mViewState: PhotoEditorViewState,
                     graphicManager: GraphicManager?,
                     private val mDefaultEmojiTypeface: Typeface?
) : Graphic(mPhotoEditorView.context, graphicManager!!) {
    private var txtEmoji: TextView? = null
    fun buildView(emojiTypeface: Typeface?, emojiName: String?) {
        if (emojiTypeface != null) {
            txtEmoji!!.typeface = emojiTypeface
        }
        txtEmoji!!.textSize = 56f
        txtEmoji!!.text = emojiName
    }

    private fun setupGesture() {
        val onGestureControl = buildGestureController(mPhotoEditorView, mViewState)
        mMultiTouchListener.setOnGestureControl(onGestureControl)
        val rootView = rootView
        rootView.setOnTouchListener(mMultiTouchListener)
    }

    override val viewType: ViewType
        get() = ViewType.EMOJI
    override val layoutId: Int
        get() = R.layout.view_photo_editor_text

    override fun setupView(rootView: View?) {
        txtEmoji = rootView!!.findViewById(R.id.tvPhotoEditorText)
        if (txtEmoji != null) {
            if (mDefaultEmojiTypeface != null) {
                txtEmoji!!.typeface = mDefaultEmojiTypeface
            }
            txtEmoji!!.gravity = Gravity.CENTER
            txtEmoji!!.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
    }

    init {
        setupGesture()
    }
}