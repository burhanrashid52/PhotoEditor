package com.burhanrashid52.photoediting.tools

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.burhanrashid52.photoediting.AddTextIcon
import com.burhanrashid52.photoediting.R
import ja.burhanrashid52.photoeditor.shape.ShapeType

/**
 * Created by Burhanuddin Rashid on 16/07/23.
 * @author  <https://github.com/burhanrashid52>
 */
private val shapeModel = Tool("Shape", R.drawable.ic_oval, ToolType.SHAPE, R.string.label_shape)
private val textModel = Tool("Text", R.drawable.ic_text, ToolType.TEXT, R.string.label_text)
private val eraserModel =
    Tool("Eraser", R.drawable.ic_eraser, ToolType.ERASER, R.string.label_eraser_mode)
private val filterModel =
    Tool("Filter", R.drawable.ic_photo_filter, ToolType.FILTER, R.string.label_filter)
private val emojiModel =
    Tool("Emoji", R.drawable.ic_insert_emoticon, ToolType.EMOJI, R.string.label_emoji)
private val stickerModel =
    Tool("Sticker", R.drawable.ic_sticker, ToolType.STICKER, R.string.label_sticker)

data class Tool(
    val name: String, val icon: Int, val type: ToolType, val label: Int,
)

@ExperimentalMaterial3Api
@Composable
fun EditingToolList(
    onSelect: (tool: Tool) -> Unit,
    onShapePicked: (shape: ShapeType) -> Unit,
    onShapeSizeChange: (size: Int) -> Unit,
    onOpacityChange: (size: Int) -> Unit,
    onColorChange: (color: Color) -> Unit,
    onEmojiSelect: (size: String) -> Unit,
    onStickerSelect: (bitmap: Bitmap) -> Unit,
    onTextAdd: (text: String, color: Color) -> Unit,
) {
    LazyRow {
        item {
            ShapeToolIcon(
                icon = { toggle ->
                    ToolIcon(shapeModel) {
                        onSelect(shapeModel)
                        toggle()
                    }
                },
                onShapePicked = onShapePicked,
                onShapeSizeChange = onShapeSizeChange,
                onOpacityChange = onOpacityChange,
                onColorChange = onColorChange,
            )
            AddTextIcon(
                icon = { toggle ->
                    ToolIcon(textModel) {
                        toggle()
                        onSelect(textModel)
                    }
                },
                onTextAdd = onTextAdd,
            )
            ToolIcon(eraserModel) {
                onSelect(eraserModel)
            }
            ToolIcon(filterModel) {
                onSelect(filterModel)
            }
            EmojiToolIcon(
                icon = { toggle ->
                    ToolIcon(emojiModel) {
                        toggle()
                        onSelect(emojiModel)
                    }
                },
                onEmojiSelect = onEmojiSelect,
            )
            StickerToolIcon(
                icon = { toggle ->
                    ToolIcon(stickerModel) {
                        toggle()
                        onSelect(stickerModel)
                    }
                },
                onStickerSelect = onStickerSelect,
            )
        }
    }
}

@Composable
private fun ToolIcon(
    tool: Tool,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(12.dp)
            .clickable(onClick = onClick)
            .testTag("tool_${tool.name}")
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

