package com.burhanrashid52.photoeditor;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import static com.burhanrashid52.photoeditor.R.color.white;

/**
 * Created by Burhanuddin Rashid on 1/16/2018.
 */

public class TextEditorDialogFragment extends DialogFragment {

    public static final String TAG = TextEditorDialogFragment.class.getSimpleName();
    public static final String EXTRA_INPUT_TEXT = "extra_input_text";
    public static final String EXTRA_COLOR_CODE = "extra_color_code";
    private EditText mAddTextEditText;
    private TextView mAddTextDoneTextView;
    private TextView mAddTextBoldTextView;
    private TextView mAddItalicTextView;
    private TextView shadow_color;
    private InputMethodManager mInputMethodManager;
    private int mColorCode;
    private TextEditor mTextEditor;
    boolean italicison  = false;
    boolean boldison = false;
    boolean isdone = false;

    public interface TextEditor {
        void onDone(String inputText, int colorCode);
    }


    //Show dialog with provide text and text color
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity,
                                                @NonNull String inputText,
                                                @ColorInt int colorCode) {
        Bundle args = new Bundle();
        args.putString(EXTRA_INPUT_TEXT, inputText);
        args.putInt(EXTRA_COLOR_CODE, colorCode);
        TextEditorDialogFragment fragment = new TextEditorDialogFragment();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return fragment;
    }

    //Show dialog with default text input as empty and text color white
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity) {
        return show(appCompatActivity,
                "", ContextCompat.getColor(appCompatActivity, white));
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        //Make dialog full screen with transparent background
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_text_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        mAddTextEditText = view.findViewById(R.id.add_text_edit_text);
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mAddTextDoneTextView = view.findViewById(R.id.add_text_done_tv);
        mAddTextBoldTextView = view.findViewById(R.id.make_bold);
        mAddItalicTextView = view.findViewById(R.id.make_italic);
        shadow_color = view.findViewById(R.id.color_picker_shadow);
        //Setup the color picker for text color
        RecyclerView addTextColorPickerRecyclerView = view.findViewById(R.id.add_text_color_picker_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        addTextColorPickerRecyclerView.setLayoutManager(layoutManager);
        addTextColorPickerRecyclerView.setHasFixedSize(true);
        final ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(getActivity());
        //This listener will change the text color when clicked on any color from picker
        colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
            @Override
            public void onColorPickerClickListener(int colorCode) {
                mColorCode = colorCode;
                mAddTextEditText.setTextColor(colorCode);
            }
        });
        addTextColorPickerRecyclerView.setAdapter(colorPickerAdapter);
        mAddTextEditText.setText(getArguments().getString(EXTRA_INPUT_TEXT));
        mColorCode = getArguments().getInt(EXTRA_COLOR_CODE);
        mAddTextEditText.setTextColor(mColorCode);
        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //Make a callback on activity when user is done with text editing
        mAddTextDoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                dismiss();
                String inputText = mAddTextEditText.getText().toString();
                if (!TextUtils.isEmpty(inputText) && mTextEditor != null){
                    if(!isdone)
                         mTextEditor.onDone(inputText, mColorCode);
                }
            }
        });
        mAddItalicTextView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {

                String inputText = mAddTextEditText.getText().toString();
                if (!italicison)
                {
                    mAddTextEditText.setTypeface(null, Typeface.ITALIC);
                    mAddItalicTextView.setTextColor(Color.GREEN);
                    italicison =true;

                }
                else{
                    mAddTextEditText.setTypeface(null, Typeface.NORMAL);
                    mAddItalicTextView.setTextColor(Color.WHITE);
                    italicison=false;

                }
                if (!TextUtils.isEmpty(inputText) && mTextEditor != null) {
                    mTextEditor.onDone(inputText, mColorCode);
                }
            }
        });
        mAddTextBoldTextView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"ResourceAsColor", "LongLogTag"})
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {

                String inputText = mAddTextEditText.getText().toString();
                if (!boldison)
                {
                    mAddTextEditText.setTypeface(null, Typeface.BOLD);
                    mAddTextBoldTextView.setTextColor(Color.GREEN);
                    boldison =true;


                }
                else{
                    mAddTextEditText.setTypeface(null, Typeface.NORMAL);
                    mAddTextBoldTextView.setTextColor(Color.WHITE);
                    boldison=false;

                }
                if (!TextUtils.isEmpty(inputText) && mTextEditor != null) {
                    mTextEditor.onDone(inputText, mColorCode);
                    isdone = true;
                }
            }
        });



        shadow_color.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {

                opencolorpicker();

            }
        });


    }
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void opencolorpicker()
        {

            ColorPickerDialogBuilder
                    .with(getContext())
                    .setTitle("Choose color")
                    .initialColor(getActivity().getApplicationContext().getColor(R.color.colorPrimary))
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorSelectedListener(new OnColorSelectedListener() {
                        @Override
                        public void onColorSelected(int selectedColor) {

                        }
                    })
                    .setPositiveButton("ok", new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                            mAddTextEditText.setTextColor(selectedColor);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .build()
                    .show();
        }


    //Callback to listener if user is done with text editing
    public void setOnTextEditorListener(TextEditor textEditor) {
        mTextEditor = textEditor;
    }
}
