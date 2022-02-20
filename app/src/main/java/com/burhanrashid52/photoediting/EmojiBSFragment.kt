package com.burhanrashid52.photoediting

import com.burhanrashid52.photoediting.GraphicHelper.addTouchHandleCallbacks
import com.burhanrashid52.photoediting.Helper.realignNewGraphicToCanvas
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import com.burhanrashid52.photoediting.ColorPickerAdapter.OnColorPickerClickListener
import com.burhanrashid52.photoediting.ColorPickerAdapter
import android.view.ViewGroup
import com.burhanrashid52.photoediting.R
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.LayerDrawable
import androidx.core.content.ContextCompat
import com.burhanrashid52.photoediting.base.BaseActivity
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import com.burhanrashid52.photoediting.EmojiBSFragment.EmojiListener
import com.burhanrashid52.photoediting.StickerBSFragment.StickerListener
import com.burhanrashid52.photoediting.tools.EditingToolsAdapter.OnItemSelected
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import com.burhanrashid52.photoediting.PropertiesBSFragment
import com.burhanrashid52.photoediting.ShapeBSFragment
import com.burhanrashid52.photoediting.EmojiBSFragment
import com.burhanrashid52.photoediting.StickerBSFragment
import android.widget.TextView
import android.graphics.Typeface
import com.burhanrashid52.photoediting.tools.EditingToolsAdapter
import com.burhanrashid52.photoediting.filters.FilterViewAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.burhanrashid52.photoediting.FileSaveHelper
import com.burhanrashid52.photoediting.GraphicHelper
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.burhanrashid52.photoediting.EditImageActivity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import com.burhanrashid52.photoediting.TextEditorDialogFragment
import com.burhanrashid52.photoediting.TextEditorDialogFragment.TextEditor
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import ja.burhanrashid52.photoeditor.ViewType
import android.annotation.SuppressLint
import androidx.core.content.FileProvider
import android.content.pm.PackageManager
import com.burhanrashid52.photoediting.FileSaveHelper.OnFileCreateResult
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import android.app.Activity
import android.app.Dialog
import ja.burhanrashid52.photoeditor.shape.ShapeType
import ja.burhanrashid52.photoeditor.PhotoFilter
import com.burhanrashid52.photoediting.tools.ToolType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.animation.AnticipateOvershootInterpolator
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.recyclerview.widget.GridLayoutManager
import com.burhanrashid52.photoediting.EmojiBSFragment.EmojiAdapter
import android.content.ContentResolver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.burhanrashid52.photoediting.FileSaveHelper.FileMeta
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import android.content.ContentValues
import android.content.Context
import kotlin.Throws
import com.burhanrashid52.photoediting.PhotoApp
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.SeekBar
import android.widget.RadioGroup
import com.burhanrashid52.photoediting.StickerBSFragment.StickerAdapter
import android.graphics.BitmapFactory
import android.widget.EditText
import android.graphics.drawable.ColorDrawable
import android.view.View
import kotlin.jvm.JvmOverloads
import androidx.annotation.ColorInt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import java.lang.NumberFormatException
import java.util.ArrayList

class EmojiBSFragment constructor() : BottomSheetDialogFragment() {
    private var mEmojiListener: EmojiListener? = null

    open interface EmojiListener {
        fun onEmojiClick(emojiUnicode: String?)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        public override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        public override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    @SuppressLint("RestrictedApi")
    public override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView: View =
            View.inflate(getContext(), R.layout.fragment_bottom_sticker_emoji_dialog, null)
        dialog.setContentView(contentView)
        val params: CoordinatorLayout.LayoutParams =
            (contentView.getParent() as View).getLayoutParams() as CoordinatorLayout.LayoutParams
        val behavior: CoordinatorLayout.Behavior<*>? = params.getBehavior()
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        (contentView.getParent() as View).setBackgroundColor(getResources().getColor(android.R.color.transparent))
        val rvEmoji: RecyclerView = contentView.findViewById(R.id.rvEmoji)
        val gridLayoutManager: GridLayoutManager = GridLayoutManager(getActivity(), 5)
        rvEmoji.setLayoutManager(gridLayoutManager)
        val emojiAdapter: EmojiAdapter = EmojiAdapter()
        rvEmoji.setAdapter(emojiAdapter)
    }

    fun setEmojiListener(emojiListener: EmojiListener?) {
        mEmojiListener = emojiListener
    }

    inner class EmojiAdapter constructor() : RecyclerView.Adapter<EmojiAdapter.ViewHolder>() {
        var emojisList: ArrayList<String> = getEmojis(getActivity())
        public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.row_emoji, parent, false)
            return ViewHolder(view)
        }

        public override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.txtEmoji.setText(emojisList.get(position))
        }

        public override fun getItemCount(): Int {
            return emojisList.size
        }

        inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var txtEmoji: TextView

            init {
                txtEmoji = itemView.findViewById(R.id.txtEmoji)
                itemView.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        if (mEmojiListener != null) {
                            mEmojiListener!!.onEmojiClick(emojisList.get(getLayoutPosition()))
                        }
                        dismiss()
                    }
                })
            }
        }
    }

    companion object {
        /**
         * Provide the list of emoji in form of unicode string
         *
         * @param context context
         * @return list of emoji unicode
         */
        fun getEmojis(context: Context?): ArrayList<String> {
            val convertedEmojiList: ArrayList<String> = ArrayList()
            val emojiList: Array<String> =
                context!!.getResources().getStringArray(R.array.photo_editor_emoji)
            for (emojiUnicode: String in emojiList) {
                convertedEmojiList.add(convertEmoji(emojiUnicode))
            }
            return convertedEmojiList
        }

        private fun convertEmoji(emoji: String): String {
            var returnedEmoji: String
            try {
                val convertEmojiToInt: Int = emoji.substring(2).toInt(16)
                returnedEmoji = String(Character.toChars(convertEmojiToInt))
            } catch (e: NumberFormatException) {
                returnedEmoji = ""
            }
            return returnedEmoji
        }
    }
}