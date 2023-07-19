package com.burhanrashid52.photoediting.tools

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.burhanrashid52.photoediting.R

/**
 * Created by Burhanuddin Rashid on 16/07/23.
 * @author  <https://github.com/burhanrashid52>
 */
private val toolList = listOf(
    ToolModel("Shape", R.drawable.ic_oval, ToolType.SHAPE),
    ToolModel("Text", R.drawable.ic_text, ToolType.TEXT),
    ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER),
    ToolModel("Filter", R.drawable.ic_photo_filter, ToolType.FILTER),
    ToolModel("Emoji", R.drawable.ic_insert_emoticon, ToolType.EMOJI),
    ToolModel("Sticker", R.drawable.ic_sticker, ToolType.STICKER),
)

private data class ToolModel(
    val name: String, val icon: Int, val type: ToolType
)

@Composable
fun EditingToolList(onSelect: (toolType: ToolType) -> Unit) {
    LazyRow {
        item {
            for (tool in toolList) {
                Column(modifier = Modifier
                    .padding(12.dp)
                    .clickable {
                        onSelect(tool.type)
                    }) {
                    Image(
                        painterResource(tool.icon),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(35.dp, 35.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        tool.name,
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp),
                    )
                }
            }

        }
    }
}