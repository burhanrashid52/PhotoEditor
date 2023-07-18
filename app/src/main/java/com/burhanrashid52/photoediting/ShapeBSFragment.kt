package com.burhanrashid52.photoediting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ja.burhanrashid52.photoeditor.shape.ShapeType

class ShapeBSFragment : BottomSheetDialogFragment(), SeekBar.OnSeekBarChangeListener {
    private var mProperties: Properties? = null

    interface Properties {
        fun onColorChanged(colorCode: Int)
        fun onOpacityChanged(opacity: Int)
        fun onShapeSizeChanged(shapeSize: Int)
        fun onShapePicked(shapeType: ShapeType)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_shapes_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeShapeColor: ComposeView = view.findViewById(R.id.composeShareColors)
        val sbOpacity = view.findViewById<SeekBar>(R.id.shapeOpacity)
        val sbBrushSize = view.findViewById<SeekBar>(R.id.shapeSize)
        val shapeGroup = view.findViewById<RadioGroup>(R.id.shapeRadioGroup)

        // shape picker
        shapeGroup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.lineRadioButton -> {
                    mProperties!!.onShapePicked(ShapeType.Line)
                }

                R.id.arrowRadioButton -> {
                    mProperties!!.onShapePicked(ShapeType.Arrow())
                }

                R.id.ovalRadioButton -> {
                    mProperties!!.onShapePicked(ShapeType.Oval)
                }

                R.id.rectRadioButton -> {
                    mProperties!!.onShapePicked(ShapeType.Rectangle)
                }

                else -> {
                    mProperties!!.onShapePicked(ShapeType.Brush)
                }
            }
        }
        sbOpacity.setOnSeekBarChangeListener(this)
        sbBrushSize.setOnSeekBarChangeListener(this)

        composeShapeColor.setContent {
            MaterialTheme {
                ColorPickerList {
                    mProperties?.run {
                        dismiss()
                        onColorChanged(it)
                    }
                }
            }
        }
    }

    fun setPropertiesChangeListener(properties: Properties?) {
        mProperties = properties
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        when (seekBar.id) {
            R.id.shapeOpacity -> if (mProperties != null) {
                mProperties!!.onOpacityChanged(i)
            }

            R.id.shapeSize -> if (mProperties != null) {
                mProperties!!.onShapeSizeChanged(i)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}