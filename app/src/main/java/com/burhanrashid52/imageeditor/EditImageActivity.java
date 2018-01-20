package com.burhanrashid52.imageeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import ja.burhanrashid52.photoeditor.BrushDrawingView;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;

import java.util.ArrayList;

public class EditImageActivity extends BaseActivity implements OnPhotoEditorListener, View.OnClickListener, Properties {

    public static final String EXTRA_IMAGE_PATHS = "extra_image_paths";
    private PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView, mDeleteLayout;
    private BrushDrawingView mBrushDrawingView;
    private ImageView mSourceImage;
    private RecyclerView mRvColor;
    private Button btnPencil, btnEraser, btnUndo, btnRedo, btnText;
    private PropertiesBSFragment mPropertiesBSFragment;


    /**
     * launch editor with multiple image
     *
     * @param context
     * @param imagesPath
     */
    public static void launch(Context context, ArrayList<String> imagesPath) {
        Intent starter = new Intent(context, EditImageActivity.class);
        starter.putExtra(EXTRA_IMAGE_PATHS, imagesPath);
        context.startActivity(starter);
    }

    /**
     * launch editor with single image
     *
     * @param context
     * @param imagePath
     */
    public static void launch(Context context, String imagePath) {
        ArrayList<String> imagePaths = new ArrayList<>();
        imagePaths.add(imagePath);
        launch(context, imagePaths);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_edit_image);
        initViews();
        mPropertiesBSFragment = new PropertiesBSFragment();
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        mPhotoEditorView.getImageSource().setImageResource(R.drawable.got);

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(false) // set flag to make text scalable when pinch
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

        //For testing only
        /*List<Integer> defaultProvidedColors = ColorPickerAdapter.getDefaultColors(this);
        for (int i = 0; i < 4; i++) {
            mPhotoEditor.addText("Text " + i, defaultProvidedColors.get(i + 1));
        }*/
    }

    private void initViews() {
        mPhotoEditorView = findViewById(R.id.photoEditorView);
        //  mDeleteLayout = findViewById(R.id.delete_rl);
        mBrushDrawingView = findViewById(R.id.brushDrawing);
        mSourceImage = findViewById(R.id.imgSource);

        mRvColor = findViewById(R.id.rvColors);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvColor.setLayoutManager(layoutManager);
        mRvColor.setHasFixedSize(true);

        btnPencil = findViewById(R.id.btnPencil);
        btnPencil.setOnClickListener(this);

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
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this,
                        text,
                        colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                mPhotoEditor.editText(rootView, inputText, colorCode);
            }
        });
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
        boolean isEnabled = !mPhotoEditor.getBrushDrawableMode();
        switch (view.getId()) {
            case R.id.btnPencil:
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                break;
            case R.id.btnEraser:
                mPhotoEditor.brushEraser();
                break;

            case R.id.btnText:
                TextEditorDialogFragment textEditorDialogFragment =
                        TextEditorDialogFragment.show(this,
                                "Burhanuddin",
                                ContextCompat.getColor(this, R.color.white));
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        mPhotoEditor.addText(inputText, colorCode);
                    }
                });
                break;

            case R.id.btnUndo:
                mPhotoEditor.undo();
                break;

            case R.id.btnRedo:
                mPhotoEditor.redo();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    mPhotoEditor.saveImage("");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateBrushDrawingView(boolean brushDrawingMode) {
        mPhotoEditor.setBrushDrawingMode(brushDrawingMode);
        mRvColor.setVisibility(brushDrawingMode ? View.VISIBLE : View.GONE);
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(this);
        colorPickerAdapter.setOnColorPickerClickListener(new ColorPickerAdapter.OnColorPickerClickListener() {
            @Override
            public void onColorPickerClickListener(int colorCode) {
                mPhotoEditor.setBrushColor(colorCode);
            }
        });
        mRvColor.setAdapter(colorPickerAdapter);
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
    }
}
