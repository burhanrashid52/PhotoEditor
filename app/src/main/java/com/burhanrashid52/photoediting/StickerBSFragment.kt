package com.burhanrashid52.photoediting

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class StickerBSFragment : BottomSheetDialogFragment() {
    private var mStickerListener: StickerListener? = null
    fun setStickerListener(stickerListener: StickerListener?) {
        mStickerListener = stickerListener
    }

    interface StickerListener {
        fun onStickerClick(bitmap: Bitmap)
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
        val composeEmoji: ComposeView = contentView.findViewById(R.id.compose)
        composeEmoji.setContent {
            MaterialTheme {
                StickerList {
                    mStickerListener?.run {
                        onStickerClick(it)
                        dismiss()
                    }
                }
            }
        }
    }
}

// Image Urls from flaticon(https://www.flaticon.com/stickers-pack/food-289)
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun StickerList(onSelect: (Bitmap) -> Unit) {
    val stickers = stickerPathList
    val context = LocalContext.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(3)
    ) {
        itemsIndexed(stickers) { index, sticker ->
            GlideImage(
                model = sticker,
                contentDescription = "",
                modifier = Modifier
                    .padding(12.dp)
                    .size(75.dp)
                    .clickable {
                        Glide
                            .with(context)
                            .asBitmap()
                            .load(sticker)
                            .into(object : CustomTarget<Bitmap?>(256, 256) {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap?>?
                                ) {
                                    onSelect(resource)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {}
                            })
                    }
                    .testTag("sticker_$index")
            )
        }
    }
}