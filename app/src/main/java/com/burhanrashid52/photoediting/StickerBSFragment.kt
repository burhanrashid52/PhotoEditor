package com.burhanrashid52.photoediting

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.burhanrashid52.photoediting.databinding.FragmentBottomStickerEmojiDialogBinding
import com.burhanrashid52.photoediting.databinding.RowStickerBinding

class StickerBSFragment : BottomSheetDialogFragment() {
    private var mStickerListener: StickerListener? = null
    private lateinit var rowStickerBinding : RowStickerBinding
    private var fragmentBottomStickerEmojiDialogBinding: FragmentBottomStickerEmojiDialogBinding? =null
    fun setStickerListener(stickerListener: StickerListener?) {
        mStickerListener = stickerListener
    }

    interface StickerListener {
        fun onStickerClick(bitmap: Bitmap?)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_bottom_sticker_emoji_dialog, null)
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        fragmentBottomStickerEmojiDialogBinding=DataBindingUtil.bind(contentView)
        val rvEmoji: RecyclerView= fragmentBottomStickerEmojiDialogBinding!!.rvEmoji
        val gridLayoutManager = GridLayoutManager(activity, 3)
        rvEmoji.layoutManager = gridLayoutManager
        val stickerAdapter = StickerAdapter()
        rvEmoji.adapter = stickerAdapter
        rvEmoji.setHasFixedSize(true)
        rvEmoji.setItemViewCacheSize(stickerPathList.size)
    }

    inner class StickerAdapter : RecyclerView.Adapter<StickerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            rowStickerBinding = RowStickerBinding.inflate(inflater)
            return ViewHolder(rowStickerBinding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(requireContext())
                    .asBitmap()
                    .load(stickerPathList[position])
                    .into(holder.rowStickerBinding.imgSticker)
        }

        override fun getItemCount(): Int {
            return stickerPathList.size
        }

        inner class ViewHolder(val rowStickerBinding: RowStickerBinding) : RecyclerView.ViewHolder(rowStickerBinding.root) {
            val imgStickerBinding=rowStickerBinding.imgSticker
            init {
                itemView.setOnClickListener {
                    if (mStickerListener != null) {
                        Glide.with(requireContext())
                                .asBitmap()
                                .load(stickerPathList[layoutPosition])
                                .into(object : CustomTarget<Bitmap?>(256, 256) {
                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                                        mStickerListener!!.onStickerClick(resource)
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                })
                    }
                    dismiss()
                }
            }
        }
    }

    companion object {
        private val stickerPathList = arrayOf(
                "https://cdn-icons-png.flaticon.com/256/4392/4392452.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392455.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392459.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392462.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392465.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392467.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392469.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392471.png",
                "https://cdn-icons-png.flaticon.com/256/4392/4392522.png",
        )
    }
}