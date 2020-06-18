package com.burhanrashid52.photoeditor;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Objects;

/**
 * Created by Burhanuddin Rashid on 1/16/2018.
 */

public class TextEditorDialogFragment extends DialogFragment {

    public static final String TAG = TextEditorDialogFragment.class.getSimpleName();
    public static final String EXTRA_INPUT_TEXT = "extra_input_text";
    public static final String EXTRA_COLOR_CODE = "extra_color_code";
    private EditText mAddTextEditText;
    private TextView mAddTextDoneTextView;
    private InputMethodManager mInputMethodManager;
    private int mColorCode;
    private TextEditor mTextEditor;
    private boolean boldB = false;
    private boolean italicB = false;
    private Typeface font = Typeface.defaultFromStyle(Typeface.NORMAL);
    private int textStyleInt = Typeface.NORMAL;
    private boolean shadowOn;
    private int shadowColor;
    private int fontInt= 0;


    public interface TextEditor {
        void onDone(String inputText, int mColorCode, int textStyleType, boolean shadowOn, int shadowColor, Typeface font);
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
                "", ContextCompat.getColor(appCompatActivity, R.color.white));
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
        Button mBoldB = view.findViewById(R.id.bold);
        Button mItalicB = view.findViewById(R.id.italic);
        Button mShadow = view.findViewById(R.id.shadow);
        Button mFont = view.findViewById(R.id.fontCycle);

        //Setup the color picker for text color
        RecyclerView addTextColorPickerRecyclerView = view.findViewById(R.id.add_text_color_picker_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        addTextColorPickerRecyclerView.setLayoutManager(layoutManager);
        addTextColorPickerRecyclerView.setHasFixedSize(true);
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(getActivity());
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
                if (!TextUtils.isEmpty(inputText) && mTextEditor != null) {
                    mTextEditor.onDone(inputText, mColorCode, textStyleInt, shadowOn, shadowColor, font);
                }
            }
        });
        // Listen to Bold / Italic Buttons
        mBoldB.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                boldB = !boldB;
                if         (boldB && italicB ) { textStyleInt = Typeface.BOLD_ITALIC; } //BOLD_ITALIC =3
                else if (boldB) { textStyleInt = Typeface.BOLD; }   //BOLD=1
                else if (italicB) {textStyleInt = Typeface.ITALIC; } //ITALIC =2
                else  { textStyleInt = Typeface.NORMAL; } //NORMAL =0
                mAddTextEditText.setTypeface(font, textStyleInt);
            }
        });
        mItalicB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                italicB = !italicB;
                if (italicB && boldB) {
                    textStyleInt = Typeface.BOLD_ITALIC;
                } else if (italicB) {
                    textStyleInt = Typeface.ITALIC;
                } else if (boldB) {
                    textStyleInt = Typeface.BOLD;
                } else {
                    textStyleInt = Typeface.NORMAL;
                }
                mAddTextEditText.setTypeface(font, textStyleInt);
            }
        });
        mShadow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shadowOn = !shadowOn;
                shadowColor = mColorCode;
                if (shadowOn) {
                    mAddTextEditText.setShadowLayer(1, 2, 2, shadowColor);
                } else {
                    mAddTextEditText.setShadowLayer(0, 0, 0, 0);
                }

            }
        });

        mFont.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                if (fontInt==0){ font =ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.playfairdisplay_regular);}
                if (fontInt==1){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.roboto_medium));}
                if (fontInt==2){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.abrilfatface_regular));}
                if (fontInt==3){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.architectsdaughter_regular));}
                if (fontInt==4){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.lacuna));}
                if (fontInt==5){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.unifrakturmaguntia));}
                if (fontInt==6){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.unicaone_regular));}
                if (fontInt==7){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.bangers_regular));}
                if (fontInt==8){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.fredokaone_regular));}
                if (fontInt==9){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.josefinsans_regular));}
                if (fontInt==10){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.lobster_regular));}
                if (fontInt==11){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.monoton_regular));}
                if (fontInt==12){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.pressstart2p_regular));}
                if (fontInt==13){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.sacramento_regular));}
                if (fontInt==14){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.specialelite_regular));}
                if (fontInt==15){ font =(ResourcesCompat.getFont(Objects.requireNonNull(getContext()), R.font.staatliches_regular));}


                if (italicB && boldB) {
                    textStyleInt = Typeface.BOLD_ITALIC;
                } else if (italicB) {
                    textStyleInt = Typeface.ITALIC;
                } else if (boldB) {
                    textStyleInt = Typeface.BOLD;
                } else {
                    textStyleInt = Typeface.NORMAL;
                }
                mAddTextEditText.setTypeface(font, textStyleInt);
fontInt ++;
if (fontInt==16){fontInt = 0;}
            }
        });
    }


    //Callback to listener if user is done with text editing
    public void setOnTextEditorListener(TextEditor textEditor) {
        mTextEditor = textEditor;
    }
}
