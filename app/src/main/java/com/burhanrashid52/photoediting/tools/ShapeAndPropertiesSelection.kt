package com.burhanrashid52.photoediting.tools

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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ja.burhanrashid52.photoeditor.shape.ShapeType

private val shapes = listOf(
    ShapeType.Brush,
    ShapeType.Line,
    ShapeType.Arrow(),
    ShapeType.Oval,
    ShapeType.Rectangle,
)

@Composable
fun ShapeAndPropertiesSelection(
    brushValue: Float,
    opacityValue: Float,
    onShapePicked: (shape: ShapeType) -> Unit,
    onShapeSizeChange: (size: Float) -> Unit,
    onOpacityChange: (size: Float) -> Unit,
    onColorChange: (size: Int) -> Unit,
) {
    Surface {
        val brushSize = remember { mutableStateOf(brushValue) }
        val opacitySize = remember { mutableStateOf(opacityValue) }
        Column(Modifier.padding(8.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Shape", style = MaterialTheme.typography.body1, fontWeight = FontWeight.Medium
            )
            ShapeSelection(ShapeType.Brush, onShapePicked)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Brush", style = MaterialTheme.typography.body1, fontWeight = FontWeight.Medium
            )
            Slider(
                value = brushSize.value,
                onValueChange = {
                    brushSize.value = it
                    onShapeSizeChange(it)
                },
                valueRange = 0f..100f,
                steps = 100,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Opacity", style = MaterialTheme.typography.body1, fontWeight = FontWeight.Medium
            )
            Slider(
                value = opacitySize.value,
                onValueChange = {
                    opacitySize.value = it
                    onOpacityChange(it);
                },
                valueRange = 0f..100f,
                steps = 100,
            )
            Spacer(modifier = Modifier.height(16.dp))
            ColorPickerList(onColorChange)
        }
    }
}

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