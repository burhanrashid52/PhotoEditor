package com.burhanrashid52.photoediting.tools

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.burhanrashid52.photoediting.R

fun getDefaultColors(context: Context): List<Int> {
    return listOf(
        R.color.blue_color_picker,
        R.color.brown_color_picker,
        R.color.green_color_picker,
        R.color.orange_color_picker,
        R.color.red_color_picker,
        R.color.black,
        R.color.red_orange_color_picker,
        R.color.sky_blue_color_picker,
        R.color.violet_color_picker,
        R.color.white,
        R.color.yellow_color_picker,
        R.color.yellow_green_color_picker,
    ).map { colorId ->
        ContextCompat.getColor(context, colorId)
    }
}

@Composable
fun ColorPickerList(modifier: Modifier = Modifier, onSelect: (colorCode: Color) -> Unit) {
    val colors = getDefaultColors(LocalContext.current)
    LazyRow(modifier) {
        item {
            for (color in colors) {
                Box(
                    modifier = Modifier
                        .size(40.dp, 50.dp)
                        .background(Color(color))
                        .clickable {
                            onSelect(Color(color))
                        },
                )
            }
        }
    }
}