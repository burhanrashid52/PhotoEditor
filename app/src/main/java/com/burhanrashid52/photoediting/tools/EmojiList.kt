package com.burhanrashid52.photoediting.tools

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.burhanrashid52.photoediting.R
import com.burhanrashid52.photoediting.base.BaseBottomSheetDialog

/**
 * Created by Burhanuddin Rashid on 18/07/23.
 * @author  <https://github.com/burhanrashid52>
 */
internal fun getEmojis(context: Context?): List<String> {
    val convertedEmojiList = ArrayList<String>()
    val emojiList = context!!.resources.getStringArray(R.array.photo_editor_emoji)
    for (emojiUnicode in emojiList) {
        convertedEmojiList.add(convertEmoji(emojiUnicode))
    }
    return convertedEmojiList
}

private fun convertEmoji(emoji: String): String {
    return try {
        val convertEmojiToInt = emoji.substring(2).toInt(16)
        String(Character.toChars(convertEmojiToInt))
    } catch (e: NumberFormatException) {
        ""
    }
}

@ExperimentalMaterial3Api
@Composable
fun EmojiToolIcon(
    modifier: Modifier = Modifier,
    onEmojiSelect: (size: String) -> Unit,
    icon: @Composable (toggle: () -> Unit) -> Unit,
) {
    BaseBottomSheetDialog(sheetContent = { close ->
        EmojiList(
            onSelect = {
                onEmojiSelect(it)
                close()
            },
        )
    }) { toggle ->
        icon(toggle)
    }
}

@Composable
fun EmojiList(onSelect: (String) -> Unit) {
    val emojiList = getEmojis(LocalContext.current)
    LazyVerticalGrid(
        columns = GridCells.Fixed(5)
    ) {
        itemsIndexed(emojiList) { index, emoji ->
            Text(
                emoji,
                fontSize = 35.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(4.dp)
                    .testTag("emoji_$index")
                    .clickable {
                        onSelect(emoji)
                    },
            )
        }
    }
}