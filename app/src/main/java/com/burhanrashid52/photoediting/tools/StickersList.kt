package com.burhanrashid52.photoediting.tools

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.burhanrashid52.photoediting.base.BaseBottomSheetDialog

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

@ExperimentalMaterial3Api
@Composable
fun StickerToolIcon(
    icon: @Composable (toggle: () -> Unit) -> Unit,
    onStickerSelect: (bitmap: Bitmap) -> Unit,
) {
    BaseBottomSheetDialog(sheetContent = { close ->
        StickerList(
            onSelect = {
                onStickerSelect(it)
                close()
            },
        )
    }) { toggle ->
        icon(toggle)
    }
}

@Composable
fun StickerList(onSelect: (Bitmap) -> Unit) {
    val stickers = stickerPathList
    val context = LocalContext.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(3)
    ) {
        itemsIndexed(stickers) { index, sticker ->
            AsyncImage(model = sticker,
                contentDescription = null,
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
                                    resource: Bitmap, transition: Transition<in Bitmap?>?
                                ) {
                                    onSelect(resource)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {}
                            })
                    }
                    .testTag("sticker_$index"))
        }
    }
}