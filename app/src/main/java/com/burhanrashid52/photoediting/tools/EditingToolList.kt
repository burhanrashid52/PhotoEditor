package com.burhanrashid52.photoediting.tools

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.burhanrashid52.photoediting.R
import com.burhanrashid52.photoediting.base.BaseBottomSheetDialog
import ja.burhanrashid52.photoeditor.shape.ShapeType

/**
 * Created by Burhanuddin Rashid on 16/07/23.
 * @author  <https://github.com/burhanrashid52>
 */
private val toolList = listOf(
    ToolModel("Text", R.drawable.ic_text, ToolType.TEXT),
    ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER),
    ToolModel("Filter", R.drawable.ic_photo_filter, ToolType.FILTER),
    ToolModel("Sticker", R.drawable.ic_sticker, ToolType.STICKER),
)
private val shapeModel = ToolModel("Shape", R.drawable.ic_oval, ToolType.SHAPE)
private val emojiModel = ToolModel("Emoji", R.drawable.ic_insert_emoticon, ToolType.EMOJI)

private data class ToolModel(
    val name: String, val icon: Int, val type: ToolType
)

@ExperimentalMaterial3Api
@Composable
fun EditingToolList(
    onSelect: (toolType: ToolType) -> Unit,
    onShapePicked: (shape: ShapeType) -> Unit,
    onShapeSizeChange: (size: Int) -> Unit,
    onOpacityChange: (size: Int) -> Unit,
    onColorChange: (size: Int) -> Unit,
    onEmojiSelect: (size: String) -> Unit,
) {
    LazyRow {
        item {
            ShapeToolIcon(
                icon = { toggle ->
                    ToolIcon(shapeModel) {
                        toggle()
                        onSelect(shapeModel.type)
                    }
                },
                onShapePicked = onShapePicked,
                onShapeSizeChange = onShapeSizeChange,
                onOpacityChange = onOpacityChange,
                onColorChange = onColorChange,
            )
            for (tool in toolList) {
                ToolIcon(tool) {
                    onSelect(tool.type)
                }
            }
            EmojiToolIcon(
                icon = { toggle ->
                    ToolIcon(emojiModel) {
                        toggle()
                        onSelect(shapeModel.type)
                    }
                },
                onEmojiSelect = onEmojiSelect,
            )
        }
    }
}

@Composable
private fun ToolIcon(
    tool: ToolModel,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(12.dp)
            .clickable(onClick = onClick)
    ) {
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