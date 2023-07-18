package com.burhanrashid52.photoediting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Ahmed Adel on 5/8/17.
 */
class ColorPickerAdapter internal constructor(
    private var context: Context,
    colorPickerColors: List<Int>
) : RecyclerView.Adapter<ColorPickerAdapter.ViewHolder>() {
    private var inflater: LayoutInflater
    private val colorPickerColors: List<Int>
    private lateinit var onColorPickerClickListener: OnColorPickerClickListener

    internal constructor(context: Context) : this(context, getDefaultColors(context)) {
        this.context = context
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.color_picker_item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.colorPickerView.setBackgroundColor(colorPickerColors[position])
    }

    override fun getItemCount(): Int {
        return colorPickerColors.size
    }

    fun setOnColorPickerClickListener(onColorPickerClickListener: OnColorPickerClickListener) {
        this.onColorPickerClickListener = onColorPickerClickListener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var colorPickerView: View = itemView.findViewById(R.id.color_picker_view)

        init {
            itemView.setOnClickListener {
                onColorPickerClickListener.onColorPickerClickListener(
                    colorPickerColors[adapterPosition]
                )
            }
        }
    }

    interface OnColorPickerClickListener {
        fun onColorPickerClickListener(colorCode: Int)
    }

    init {
        inflater = LayoutInflater.from(context)
        this.colorPickerColors = colorPickerColors
    }
}

fun getDefaultColors(context: Context): List<Int> {
    val colorPickerColors = ArrayList<Int>()
    colorPickerColors.add(ContextCompat.getColor((context), R.color.blue_color_picker))
    colorPickerColors.add(ContextCompat.getColor((context), R.color.brown_color_picker))
    colorPickerColors.add(ContextCompat.getColor((context), R.color.green_color_picker))
    colorPickerColors.add(ContextCompat.getColor((context), R.color.orange_color_picker))
    colorPickerColors.add(ContextCompat.getColor((context), R.color.red_color_picker))
    colorPickerColors.add(ContextCompat.getColor((context), R.color.black))
    colorPickerColors.add(
        ContextCompat.getColor(
            (context),
            R.color.red_orange_color_picker
        )
    )
    colorPickerColors.add(
        ContextCompat.getColor(
            (context),
            R.color.sky_blue_color_picker
        )
    )
    colorPickerColors.add(ContextCompat.getColor((context), R.color.violet_color_picker))
    colorPickerColors.add(ContextCompat.getColor((context), R.color.white))
    colorPickerColors.add(ContextCompat.getColor((context), R.color.yellow_color_picker))
    colorPickerColors.add(
        ContextCompat.getColor(
            (context),
            R.color.yellow_green_color_picker
        )
    )
    return colorPickerColors
}

@Composable
fun ColorPickerList(onSelect: (colorCode: Int) -> Unit) {
    val colors = getDefaultColors(LocalContext.current)
    LazyRow {
        item {
            for (color in colors) {
                Box(
                    modifier = Modifier
                        .size(40.dp, 50.dp)
                        .background(Color(color))
                        .clickable {
                            onSelect(color)
                        },
                )
            }
        }
    }
}