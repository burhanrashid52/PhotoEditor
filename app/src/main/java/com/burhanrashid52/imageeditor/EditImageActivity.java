package com.burhanrashid52.imageeditor;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ahmedadeltito.photoeditorsdk.BrushDrawingView;
import com.ahmedadeltito.photoeditorsdk.OnPhotoEditorSDKListener;
import com.ahmedadeltito.photoeditorsdk.PhotoEditorSDK;
import com.ahmedadeltito.photoeditorsdk.ViewType;

public class EditImageActivity extends AppCompatActivity implements OnPhotoEditorSDKListener, View.OnClickListener {

    private PhotoEditorSDK mPhotoEditorSDK;
    private RelativeLayout mParentImgSource;
    private RelativeLayout mDeleteLayout;
    private BrushDrawingView mBrushDrawingView;
    private ImageView mSourceImage;
    private RecyclerView mRvColor;
    private Toolbar mToolbar;
    private Button btnPencil, btnEraser, btnHighlighter, btnUndo, btnRedo, btnText;
    private int mColorCodeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        initViews();
        setSupportActionBar(mToolbar);

        mPhotoEditorSDK = new PhotoEditorSDK.PhotoEditorSDKBuilder(this)
                .setParentView(mParentImgSource) // add parent image view
                .setChildView(mSourceImage) // add the desired image view
          //      .setDeleteView(mDeleteLayout) // add the deleted view that will appear during the movement of the views
                .setBrushDrawingView(mBrushDrawingView) // add the brush drawing view that is responsible for drawing on the image view
                .setPinchTextScalable(false) // set flag to make text scalable when pinch
                .build(); // build photo editor sdk

        mPhotoEditorSDK.setOnPhotoEditorSDKListener(this);
    }

    private void initViews() {
        mToolbar = findViewById(R.id.toolbar);
        mParentImgSource = findViewById(R.id.parentImgSource);
        mDeleteLayout = findViewById(R.id.delete_rl);
        mBrushDrawingView = findViewById(R.id.brushDrawing);
        mSourceImage = findViewById(R.id.imgSource);

        mRvColor = findViewById(R.id.rvColors);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvColor.setLayoutManager(layoutManager);
        mRvColor.setHasFixedSize(true);

        btnPencil = findViewById(R.id.btnPencil);
        btnPencil.setOnClickListener(this);

        btnHighlighter = findViewById(R.id.btnHighlighter);
        btnHighlighter.setOnClickListener(this);

        btnText = findViewById(R.id.btnText);
        btnText.setOnClickListener(this);

        btnEraser = findViewById(R.id.btnEraser);
        btnEraser.setOnClickListener(this);

        btnUndo = findViewById(R.id.btnUndo);
        btnUndo.setOnClickListener(this);

        btnRedo = findViewById(R.id.btnRedo);
        btnRedo.setOnClickListener(this);
    }

    @Override
    public void onEditTextChangeListener(String text, int colorCode) {

    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {

    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onClick(View view) {
        boolean isEnabled = !mPhotoEditorSDK.getBrushDrawableMode();
        switch (view.getId()) {
            case R.id.btnPencil:
                if (isEnabled) {
                    mPhotoEditorSDK.setOpacity(100);
                    mPhotoEditorSDK.setBrushSize(25);
                }
                updateBrushDrawingView(isEnabled);
                break;
            case R.id.btnEraser:
                mPhotoEditorSDK.brushEraser();
                break;

            case R.id.btnText:
                TextEditorDialogFragment textEditorDialogFragment =
                        TextEditorDialogFragment.show(this,
                                "Hello",
                                ContextCompat.getColor(this, R.color.white));
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        mPhotoEditorSDK.addText(inputText, colorCode);
                    }
                });
                break;

            case R.id.btnUndo:
                mPhotoEditorSDK.undo();
                break;

            case R.id.btnRedo:
                mPhotoEditorSDK.redo();
                break;

            case R.id.btnHighlighter:
                if (isEnabled) {
                    mPhotoEditorSDK.setOpacity(50);
                    mPhotoEditorSDK.setBrushSize(25);
                }
                updateBrushDrawingView(isEnabled);
                break;
        }
    }

    private void updateBrushDrawingView(boolean brushDrawingMode) {
        mPhotoEditorSDK.setBrushDrawingMode(brushDrawingMode);
        mRvColor.setVisibility(brushDrawingMode ? View.VISIBLE : View.GONE);
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(this);
        colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
            @Override
            public void onColorPickerClickListener(int colorCode) {
                mPhotoEditorSDK.setBrushColor(colorCode);
            }
        });
        mRvColor.setAdapter(colorPickerAdapter);
    }
}
