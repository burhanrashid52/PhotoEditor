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
import kotlin.Throws
import com.burhanrashid52.photoediting.PhotoApp
import android.widget.SeekBar.OnSeekBarChangeListener
import com.burhanrashid52.photoediting.StickerBSFragment.StickerAdapter
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.*
import kotlin.jvm.JvmOverloads
import androidx.annotation.ColorInt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import java.lang.NumberFormatException

class StickerBSFragment constructor() : BottomSheetDialogFragment() {
    private var mStickerListener: StickerListener? = null
    fun setStickerListener(stickerListener: StickerListener?) {
        mStickerListener = stickerListener
    }

    open interface StickerListener {
        fun onStickerClick(bitmap: Bitmap?)
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
        val gridLayoutManager: GridLayoutManager = GridLayoutManager(getActivity(), 3)
        rvEmoji.setLayoutManager(gridLayoutManager)
        val stickerAdapter: StickerAdapter = StickerAdapter()
        rvEmoji.setAdapter(stickerAdapter)
    }

    public override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    inner class StickerAdapter constructor() : RecyclerView.Adapter<StickerAdapter.ViewHolder>() {
        var stickerList: IntArray = intArrayOf(R.drawable.aa, R.drawable.bb)
        public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_sticker, parent, false)
            return ViewHolder(view)
        }

        public override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.imgSticker.setImageResource(stickerList.get(position))
        }

        public override fun getItemCount(): Int {
            return stickerList.size
        }

        inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var imgSticker: ImageView

            init {
                imgSticker = itemView.findViewById(R.id.imgSticker)
                itemView.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        if (mStickerListener != null) {
                            mStickerListener!!.onStickerClick(
                                BitmapFactory.decodeResource(
                                    getResources(),
                                    stickerList.get(getLayoutPosition())
                                )
                            )
                        }
                        dismiss()
                    }
                })
            }
        }
    }

    private fun convertEmoji(emoji: String): String {
        var returnedEmoji: String = ""
        try {
            val convertEmojiToInt: Int = emoji.substring(2).toInt(16)
            returnedEmoji = getEmojiByUnicode(convertEmojiToInt)
        } catch (e: NumberFormatException) {
            returnedEmoji = ""
        }
        return returnedEmoji
    }

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }
}