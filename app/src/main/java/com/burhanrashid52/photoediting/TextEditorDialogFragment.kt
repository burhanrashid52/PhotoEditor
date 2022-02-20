package com.burhanrashid52.photoediting

import com.burhanrashid52.photoediting.GraphicHelper.addTouchHandleCallbacks
import com.burhanrashid52.photoediting.Helper.realignNewGraphicToCanvas
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import com.burhanrashid52.photoediting.ColorPickerAdapter.OnColorPickerClickListener
import com.burhanrashid52.photoediting.ColorPickerAdapter
import android.view.ViewGroup
import com.burhanrashid52.photoediting.R
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.LayerDrawable
import androidx.core.content.ContextCompat
import com.burhanrashid52.photoediting.base.BaseActivity
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import com.burhanrashid52.photoediting.EmojiBSFragment.EmojiListener
import com.burhanrashid52.photoediting.StickerBSFragment.StickerListener
import com.burhanrashid52.photoediting.tools.EditingToolsAdapter.OnItemSelected
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import com.burhanrashid52.photoediting.PropertiesBSFragment
import com.burhanrashid52.photoediting.ShapeBSFragment
import com.burhanrashid52.photoediting.EmojiBSFragment
import com.burhanrashid52.photoediting.StickerBSFragment
import android.widget.TextView
import android.graphics.Typeface
import com.burhanrashid52.photoediting.tools.EditingToolsAdapter
import com.burhanrashid52.photoediting.filters.FilterViewAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.burhanrashid52.photoediting.FileSaveHelper
import com.burhanrashid52.photoediting.GraphicHelper
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.burhanrashid52.photoediting.EditImageActivity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import com.burhanrashid52.photoediting.TextEditorDialogFragment
import com.burhanrashid52.photoediting.TextEditorDialogFragment.TextEditor
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import ja.burhanrashid52.photoeditor.ViewType
import android.annotation.SuppressLint
import androidx.core.content.FileProvider
import android.content.pm.PackageManager
import com.burhanrashid52.photoediting.FileSaveHelper.OnFileCreateResult
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.PhotoEditor.OnSaveListener
import android.app.Activity
import android.app.Dialog
import ja.burhanrashid52.photoeditor.shape.ShapeType
import ja.burhanrashid52.photoeditor.PhotoFilter
import com.burhanrashid52.photoediting.tools.ToolType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.animation.AnticipateOvershootInterpolator
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.recyclerview.widget.GridLayoutManager
import com.burhanrashid52.photoediting.EmojiBSFragment.EmojiAdapter
import android.content.ContentResolver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.burhanrashid52.photoediting.FileSaveHelper.FileMeta
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import android.content.ContentValues
import android.content.Context
import kotlin.Throws
import com.burhanrashid52.photoediting.PhotoApp
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.SeekBar
import android.widget.RadioGroup
import com.burhanrashid52.photoediting.StickerBSFragment.StickerAdapter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.widget.EditText
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlin.jvm.JvmOverloads
import androidx.annotation.ColorInt
import androidx.fragment.app.DialogFragment

/**
 * Created by Burhanuddin Rashid on 1/16/2018.
 */
class TextEditorDialogFragment constructor() : DialogFragment() {
    private var mAddTextEditText: EditText? = null
    private var mAddTextDoneTextView: TextView? = null
    private var mInputMethodManager: InputMethodManager? = null
    private var mColorCode: Int = 0
    private var mTextEditor: TextEditor? = null

    open interface TextEditor {
        fun onDone(inputText: String?, colorCode: Int)
    }

    public override fun onStart() {
        super.onStart()
        val dialog: Dialog? = getDialog()
        //Make dialog full screen with transparent background
        if (dialog != null) {
            val width: Int = ViewGroup.LayoutParams.MATCH_PARENT
            val height: Int = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.getWindow()!!.setLayout(width, height)
            dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    public override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_text_dialog, container, false)
    }

    public override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAddTextEditText = view.findViewById(R.id.add_text_edit_text)
        mInputMethodManager =
            getActivity()!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        mAddTextDoneTextView = view.findViewById(R.id.add_text_done_tv)

        //Setup the color picker for text color
        val addTextColorPickerRecyclerView: RecyclerView =
            view.findViewById(R.id.add_text_color_picker_recycler_view)
        val layoutManager: LinearLayoutManager =
            LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)
        addTextColorPickerRecyclerView.setLayoutManager(layoutManager)
        addTextColorPickerRecyclerView.setHasFixedSize(true)
        val colorPickerAdapter: ColorPickerAdapter = ColorPickerAdapter((getActivity())!!)
        //This listener will change the text color when clicked on any color from picker
        colorPickerAdapter.setOnColorPickerClickListener(object : OnColorPickerClickListener {
            public override fun onColorPickerClickListener(colorCode: Int) {
                mColorCode = colorCode
                mAddTextEditText.setTextColor(colorCode)
            }
        })
        addTextColorPickerRecyclerView.setAdapter(colorPickerAdapter)
        mAddTextEditText.setText(getArguments()!!.getString(EXTRA_INPUT_TEXT))
        mColorCode = getArguments()!!.getInt(EXTRA_COLOR_CODE)
        mAddTextEditText.setTextColor(mColorCode)
        mInputMethodManager!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        //Make a callback on activity when user is done with text editing
        mAddTextDoneTextView.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(view: View) {
                mInputMethodManager!!.hideSoftInputFromWindow(view.getWindowToken(), 0)
                dismiss()
                val inputText: String = mAddTextEditText.getText().toString()
                if (!TextUtils.isEmpty(inputText) && mTextEditor != null) {
                    mTextEditor!!.onDone(inputText, mColorCode)
                }
            }
        })
    }

    //Callback to listener if user is done with text editing
    fun setOnTextEditorListener(textEditor: TextEditor?) {
        mTextEditor = textEditor
    }

    companion object {
        val TAG: String = TextEditorDialogFragment::class.java.getSimpleName()
        val EXTRA_INPUT_TEXT: String = "extra_input_text"
        val EXTRA_COLOR_CODE: String = "extra_color_code"

        //Show dialog with provide text and text color
        //Show dialog with default text input as empty and text color white
        @JvmOverloads
        fun show(
            appCompatActivity: AppCompatActivity,
            inputText: String =
                "",
            @ColorInt colorCode: Int = ContextCompat.getColor(appCompatActivity, R.color.white)
        ): TextEditorDialogFragment {
            val args: Bundle = Bundle()
            args.putString(EXTRA_INPUT_TEXT, inputText)
            args.putInt(EXTRA_COLOR_CODE, colorCode)
            val fragment: TextEditorDialogFragment = TextEditorDialogFragment()
            fragment.setArguments(args)
            fragment.show(appCompatActivity.getSupportFragmentManager(), TAG)
            return fragment
        }
    }
}