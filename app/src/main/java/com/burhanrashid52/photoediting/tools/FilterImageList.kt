package com.burhanrashid52.photoediting.tools

import android.graphics.BitmapFactory
import android.util.Pair
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ja.burhanrashid52.photoeditor.PhotoFilter

/**
 * Created by Burhanuddin Rashid on 15/07/23.
 * @author  <https://github.com/burhanrashid52>
 */

private val imagePairList = listOf(
    Pair("filters/original.jpg", PhotoFilter.NONE),
    Pair("filters/auto_fix.png", PhotoFilter.AUTO_FIX),
    Pair("filters/brightness.png", PhotoFilter.BRIGHTNESS),
    Pair("filters/contrast.png", PhotoFilter.CONTRAST),
    Pair("filters/documentary.png", PhotoFilter.DOCUMENTARY),
    Pair("filters/dual_tone.png", PhotoFilter.DUE_TONE),
    Pair("filters/fill_light.png", PhotoFilter.FILL_LIGHT),
    Pair("filters/fish_eye.png", PhotoFilter.FISH_EYE),
    Pair("filters/grain.png", PhotoFilter.GRAIN),
    Pair("filters/gray_scale.png", PhotoFilter.GRAY_SCALE),
    Pair("filters/lomish.png", PhotoFilter.LOMISH),
    Pair("filters/negative.png", PhotoFilter.NEGATIVE),
    Pair("filters/posterize.png", PhotoFilter.POSTERIZE),
    Pair("filters/saturate.png", PhotoFilter.SATURATE),
    Pair("filters/sepia.png", PhotoFilter.SEPIA),
    Pair("filters/sharpen.png", PhotoFilter.SHARPEN),
    Pair("filters/temprature.png", PhotoFilter.TEMPERATURE),
    Pair("filters/tint.png", PhotoFilter.TINT),
    Pair("filters/vignette.png", PhotoFilter.VIGNETTE),
    Pair("filters/cross_process.png", PhotoFilter.CROSS_PROCESS),
    Pair("filters/b_n_w.png", PhotoFilter.BLACK_WHITE),
    Pair("filters/flip_horizental.png", PhotoFilter.FLIP_HORIZONTAL),
    Pair("filters/flip_vertical.png", PhotoFilter.FLIP_VERTICAL),
    Pair("filters/rotate.png", PhotoFilter.ROTATE),
)

@Composable
fun FilerImageList(onSelect: (photoFilter: PhotoFilter) -> Unit) {
    val assetManager = LocalContext.current.assets
    LazyRow {
        item {
            for (imagePair in imagePairList) {
                val inputStream = assetManager.open(imagePair.first)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }
                Box(modifier = Modifier
                    .aspectRatio(1f)
                    .padding(horizontal = 2.dp, vertical = 4.dp)
                    .clickable {
                        onSelect(imagePair.second)
                    }) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        imagePair.second.name.replace("_", " "),
                        fontSize = 10.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color(0xFF90000000))
                            .padding(2.dp),
                    )
                }
            }
        }
    }
}