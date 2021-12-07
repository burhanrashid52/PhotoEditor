package com.burhanrashid52.photoediting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PropertiesBSFragment : BottomSheetDialogFragment(), OnSeekBarChangeListener {
    private var mProperties: Properties? = null

    interface Properties {
        fun onColorChanged(colorCode: Int)
        fun onOpacityChanged(opacity: Int)
        fun onShapeSizeChanged(shapeSize: Int)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_properties_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvColor: RecyclerView = view.findViewById(R.id.rvColors)
        val sbOpacity = view.findViewById<SeekBar>(R.id.sbOpacity)
        val sbBrushSize = view.findViewById<SeekBar>(R.id.sbSize)
        sbOpacity.setOnSeekBarChangeListener(this)
        sbBrushSize.setOnSeekBarChangeListener(this)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rvColor.layoutManager = layoutManager
        rvColor.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(activity!!)
        colorPickerAdapter.setOnColorPickerClickListener(object: ColorPickerAdapter.OnColorPickerClickListener {
            override fun onColorPickerClickListener(colorCode: Int) {
                mProperties?.let {
                    dismiss()
                    it.onColorChanged(colorCode)
                }
            }

        })
        rvColor.adapter = colorPickerAdapter
    }

    fun setPropertiesChangeListener(properties: Properties?) {
        mProperties = properties
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        when (seekBar.id) {
            R.id.sbOpacity -> mProperties?.onOpacityChanged(i)
            R.id.sbSize -> mProperties?.onShapeSizeChanged(i)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}