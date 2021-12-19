package ja.burhanrashid52.photoeditor

import android.view.ViewGroup
import ja.burhanrashid52.photoeditor.MultiTouchListener
import ja.burhanrashid52.photoeditor.PhotoEditorViewState
import ja.burhanrashid52.photoeditor.GraphicManager
import android.graphics.Typeface
import ja.burhanrashid52.photoeditor.Graphic
import android.widget.TextView
import ja.burhanrashid52.photoeditor.MultiTouchListener.OnGestureControl
import ja.burhanrashid52.photoeditor.ViewType
import ja.burhanrashid52.photoeditor.R
import android.view.Gravity
import android.view.View

/**
 * Created by Burhanuddin Rashid on 14/05/21.
 *
 * @author <https:></https:>//github.com/burhanrashid52>
 */
internal class Emoji(
    private val mPhotoEditorView: ViewGroup,
    private val mMultiTouchListener: MultiTouchListener,
    private val mViewState: PhotoEditorViewState,
    graphicManager: GraphicManager?,
    private val mDefaultEmojiTypeface: Typeface?
) : Graphic(mPhotoEditorView.context, graphicManager) {
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

    public override fun getViewType(): ViewType {
        return ViewType.EMOJI
    }

    public override fun getLayoutId(): Int {
        return R.layout.view_photo_editor_text
    }

    public override fun setupView(rootView: View) {
        txtEmoji = rootView.findViewById(R.id.tvPhotoEditorText)
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