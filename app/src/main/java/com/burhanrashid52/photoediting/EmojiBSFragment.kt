package com.burhanrashid52.photoediting

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmojiBSFragment : BottomSheetDialogFragment() {
    private var mEmojiListener: EmojiListener? = null

    interface EmojiListener {
        fun onEmojiClick(emojiUnicode: String)
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
        val composeEmoji: ComposeView = contentView.findViewById(R.id.composeEmoji)
        composeEmoji.isVisible = true
        composeEmoji.setContent {
            MaterialTheme {
                EmojiList {
                    mEmojiListener?.onEmojiClick(it)
                    dismiss()
                }
            }
        }
    }

    fun setEmojiListener(emojiListener: EmojiListener?) {
        mEmojiListener = emojiListener
    }
}

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