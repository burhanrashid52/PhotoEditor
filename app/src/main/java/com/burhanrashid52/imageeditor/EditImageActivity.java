package com.burhanrashid52.imageeditor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ahmedadeltito.photoeditorsdk.BrushDrawingView;
import com.ahmedadeltito.photoeditorsdk.OnPhotoEditorListener;
import com.ahmedadeltito.photoeditorsdk.PhotoEditor;
import com.ahmedadeltito.photoeditorsdk.ViewType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditImageActivity extends BaseActivity implements OnPhotoEditorListener, View.OnClickListener {

    public static final String EXTRA_IMAGE_PATHS = "extra_image_paths";
    private PhotoEditor mPhotoEditor;
    private RelativeLayout mParentImgSource, mDeleteLayout;
    private BrushDrawingView mBrushDrawingView;
    private ImageView mSourceImage;
    private RecyclerView mRvColor;
    private Toolbar mToolbar;
    private Button btnPencil, btnEraser, btnHighlighter, btnUndo, btnRedo, btnText;


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
        setContentView(R.layout.activity_edit_image);
        initViews();
        setSupportActionBar(mToolbar);

        mPhotoEditor = new PhotoEditor.PhotoEditorBuilder(this)
                .setParentView(mParentImgSource) // add parent image view
                .setChildView(mSourceImage) // add the desired image view
                //.setDeleteView(mDeleteLayout) // add the deleted view that will appear during the movement of the views
                .setBrushDrawingView(mBrushDrawingView) // add the brush drawing view that is responsible for drawing on the image view
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
                if (isEnabled) {
                    mPhotoEditor.setOpacity(100);
                    mPhotoEditor.setBrushSize(25);
                }
                updateBrushDrawingView(isEnabled);
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

            case R.id.btnHighlighter:
                if (isEnabled) {
                    mPhotoEditor.setOpacity(50);
                    mPhotoEditor.setBrushSize(25);
                }
                updateBrushDrawingView(isEnabled);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageName = "IMG_" + timeStamp + ".jpg";
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("imagePath", mPhotoEditor.saveImage("PhotoEditor", imageName));
                    setResult(Activity.RESULT_OK, returnIntent);
                    //  finish();
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
}
