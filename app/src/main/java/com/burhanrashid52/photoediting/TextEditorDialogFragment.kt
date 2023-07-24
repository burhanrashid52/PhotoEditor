package com.burhanrashid52.photoediting

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.burhanrashid52.photoediting.base.BaseBottomSheetDialog
import com.burhanrashid52.photoediting.tools.ColorPickerList

/**
 * Created by Burhanuddin Rashid on 1/16/2018.
 */
class TextEditorDialogFragment : DialogFragment() {

    private var mTextEditorListener: TextEditorListener? = null

    interface TextEditorListener {
        fun onDone(inputText: String, colorCode: Color)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        //Make dialog full screen with transparent background
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.compose_view_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val intentText = arguments?.getString(EXTRA_INPUT_TEXT) ?: ""
        val intentColorCode: Int = arguments?.getInt(EXTRA_COLOR_CODE) ?: Color.White.value.toInt()

        //Setup the color picker for text color
        val composeColors: ComposeView = view.findViewById(R.id.composeView)
        composeColors.setContent {
            MaterialTheme {
                TextUpdateView(intentText, Color(intentColorCode)) { text, color ->
                    dismiss()
                    mTextEditorListener?.run {
                        onDone(text, color)
                    }
                }
            }
        }
    }

    //Callback to listener if user is done with text editing
    fun setOnTextEditorListener(textEditorListener: TextEditorListener) {
        mTextEditorListener = textEditorListener
    }

    companion object {
        private val TAG: String = TextEditorDialogFragment::class.java.simpleName
        const val EXTRA_INPUT_TEXT = "extra_input_text"
        const val EXTRA_COLOR_CODE = "extra_color_code"

        //Show dialog with provide text and text color
        //Show dialog with default text input as empty and text color white
        @JvmOverloads
        fun show(
            appCompatActivity: AppCompatActivity,
            inputText: String = "",
            @ColorInt colorCode: Int = ContextCompat.getColor(appCompatActivity, R.color.white)
        ): TextEditorDialogFragment {
            val args = Bundle()
            args.putString(EXTRA_INPUT_TEXT, inputText)
            args.putInt(EXTRA_COLOR_CODE, colorCode)
            val fragment = TextEditorDialogFragment()
            fragment.arguments = args
            fragment.show(appCompatActivity.supportFragmentManager, TAG)
            return fragment
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun AddTextIcon(
    icon: @Composable (toggle: () -> Unit) -> Unit,
    text: String = "",
    color: Color = Color.White,
    onTextAdd: (text: String, color: Color) -> Unit,
) {
    BaseBottomSheetDialog(
        skipPartiallyExpanded = true,
        sheetContent = { close ->
            TextUpdateView(text, color) { text, color ->
                onTextAdd(text, color)
                //To avoid the screen stuck. Not sure why this happens
                Handler(Looper.getMainLooper()).postDelayed(close, 250)
            }
        },
    ) { toggle ->
        icon(toggle)
    }
}

@Preview
@Composable
fun AddText() {
    MaterialTheme {
        TextUpdateView(
            defaultText = "Hello",
            defaultColor = Color.Red,
        ) { text, color ->
        }
    }
}

@Composable
private fun TextUpdateView(
    defaultText: String, defaultColor: Color, onDone: (text: String, color: Color) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val text = remember { mutableStateOf(defaultText) }
    val colorCode = remember { mutableStateOf(defaultColor) }
    Box(Modifier.fillMaxSize()) {
        TextField(
            value = text.value,
            onValueChange = {
                text.value = it
            },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 40.sp,
                textAlign = TextAlign.Center,
                color = colorCode.value,
            ),
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .testTag("add_text_edit_text"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Black.copy(0.5f),
                unfocusedContainerColor = Color.Black.copy(0.5f),
                focusedTextColor = colorCode.value,
            ),
        )
        ColorPickerList(modifier = Modifier.align(Alignment.BottomCenter)) {
            colorCode.value = it
        }
        OutlinedButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            onClick = {
                focusManager.clearFocus()
                if (text.value.isNotEmpty()) {
                    onDone(text.value, colorCode.value)
                }
            },
        ) {
            Text("Done")
        }
    }
}