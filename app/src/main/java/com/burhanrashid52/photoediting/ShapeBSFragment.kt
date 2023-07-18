package com.burhanrashid52.photoediting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.burhanrashid52.photoediting.tools.ColorPickerList
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ja.burhanrashid52.photoeditor.shape.ShapeType

class ShapeBSFragment : BottomSheetDialogFragment() {
    private var mProperties: Properties? = null

    interface Properties {
        fun onColorChanged(colorCode: Int)
        fun onOpacityChanged(opacity: Int)
        fun onShapeSizeChanged(shapeSize: Int)
        fun onShapePicked(shapeType: ShapeType)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_shapes_dialog, container, false)
    }

    var brushValue = 25f
    var opacityValue = 100f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeShapeColor: ComposeView = view.findViewById(R.id.composeShareColors)
        composeShapeColor.setContent {
            MaterialTheme {
                val brushSize = remember { mutableStateOf(brushValue) }
                val opacitySize = remember { mutableStateOf(opacityValue) }
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Shape",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Medium
                    )
                    ShapeSelection(ShapeType.Brush) {
                        mProperties?.run {
                            onShapePicked(it)
                            dismiss()
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Brush",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = brushSize.value,
                        onValueChange = {
                            brushSize.value = it
                            brushValue = it
                            mProperties?.onShapeSizeChanged(it.toInt())
                        },
                        valueRange = 0f..100f,
                        steps = 100,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Opacity",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = opacitySize.value,
                        onValueChange = {
                            opacitySize.value = it
                            opacityValue = it
                            mProperties?.onOpacityChanged(it.toInt())
                        },
                        valueRange = 0f..100f,
                        steps = 100,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ColorPickerList {
                        mProperties?.run {
                            onColorChanged(it)
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    fun setPropertiesChangeListener(properties: Properties?) {
        mProperties = properties
    }
}

private val shapes = listOf(
    ShapeType.Brush,
    ShapeType.Line,
    ShapeType.Arrow(),
    ShapeType.Oval,
    ShapeType.Rectangle,
)

@Composable
fun ShapeSelection(filledShape: ShapeType, onSelect: (shape: ShapeType) -> Unit) {
    val selectedShape = remember { mutableStateOf(filledShape) }
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        for (shape in shapes) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(48.dp)
            ) {
                RadioButton(
                    selected = selectedShape.value == shape, onClick = {
                        selectedShape.value = shape
                        onSelect(shape)
                    }, Modifier.size(20.dp)
                )
                Text(
                    text = shape::class.java.simpleName,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

        }
    }
}