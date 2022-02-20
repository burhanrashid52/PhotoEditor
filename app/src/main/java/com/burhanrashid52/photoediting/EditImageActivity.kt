package com.burhanrashid52.photoediting

import android.Manifest
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
import android.content.DialogInterface
import kotlin.Throws
import com.burhanrashid52.photoediting.PhotoApp
import android.widget.SeekBar.OnSeekBarChangeListener
import com.burhanrashid52.photoediting.StickerBSFragment.StickerAdapter
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import kotlin.jvm.JvmOverloads
import androidx.annotation.ColorInt
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.burhanrashid52.photoediting.filters.FilterListener
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import java.io.File
import java.io.IOException
import java.lang.Exception

class EditImageActivity constructor() : BaseActivity(), OnPhotoEditorListener, View.OnClickListener,
    PropertiesBSFragment.Properties, ShapeBSFragment.Properties, EmojiListener, StickerListener,
    OnItemSelected, FilterListener {
    var mPhotoEditor: PhotoEditor? = null
    private var mPhotoEditorView: PhotoEditorView? = null
    private var mPropertiesBSFragment: PropertiesBSFragment? = null
    private var mShapeBSFragment: ShapeBSFragment? = null
    private var mShapeBuilder: ShapeBuilder? = null
    private var mEmojiBSFragment: EmojiBSFragment? = null
    private var mStickerBSFragment: StickerBSFragment? = null
    private var mTxtCurrentTool: TextView? = null
    private var mWonderFont: Typeface? = null
    private var mRvTools: RecyclerView? = null
    private var mRvFilters: RecyclerView? = null
    private val mEditingToolsAdapter: EditingToolsAdapter = EditingToolsAdapter(this)
    private val mFilterViewAdapter: FilterViewAdapter = FilterViewAdapter(this)
    private var mRootView: ConstraintLayout? = null
    private val mConstraintSet: ConstraintSet = ConstraintSet()
    private var mIsFilterVisible: Boolean = false

    @VisibleForTesting
    var mSaveImageUri: Uri? = null
    private var mSaveFileHelper: FileSaveHelper? = null
    private var mGraphicHelper: GraphicHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_edit_image)
        initViews()
        handleIntentImage(mPhotoEditorView!!.getSource())
        mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf")
        mPropertiesBSFragment = PropertiesBSFragment()
        mEmojiBSFragment = EmojiBSFragment()
        mStickerBSFragment = StickerBSFragment()
        mShapeBSFragment = ShapeBSFragment()
        mStickerBSFragment!!.setStickerListener(this)
        mEmojiBSFragment!!.setEmojiListener(this)
        mPropertiesBSFragment!!.setPropertiesChangeListener(this)
        mShapeBSFragment!!.setPropertiesChangeListener(this)
        val llmTools: LinearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvTools!!.setLayoutManager(llmTools)
        mRvTools!!.setAdapter(mEditingToolsAdapter)
        val llmFilters: LinearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRvFilters!!.setLayoutManager(llmFilters)
        mRvFilters!!.setAdapter(mFilterViewAdapter)

        // NOTE(lucianocheng): Used to set integration testing parameters to PhotoEditor
        val pinchTextScalable: Boolean = getIntent().getBooleanExtra(
            PINCH_TEXT_SCALABLE_INTENT_KEY, true
        )

        //Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        //Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");
        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView)
            .setPinchTextScalable(pinchTextScalable) // set flag to make text scalable when pinch
            //.setDefaultTextTypeface(mTextRobotoTf)
            //.setDefaultEmojiTypeface(mEmojiTypeFace)
            .build() // build photo editor sdk
        mPhotoEditor.setOnPhotoEditorListener(this)

        //Set Image Dynamically
        mPhotoEditorView!!.getSource().setImageResource(R.drawable.paris_tower)
        mSaveFileHelper = FileSaveHelper(this)
        mGraphicHelper = GraphicHelper(
            mPhotoEditor,
            this
        )
    }

    private fun handleIntentImage(source: ImageView) {
        val intent: Intent? = getIntent()
        if (intent != null) {
            // NOTE(lucianocheng): Using "yoda conditions" here to guard against
            //                     a null Action in the Intent.
            if ((Intent.ACTION_EDIT == intent.getAction()) || (ACTION_NEXTGEN_EDIT == intent.getAction())) {
                try {
                    val uri: Uri? = intent.getData()
                    val bitmap: Bitmap =
                        MediaStore.Images.Media.getBitmap(getContentResolver(), uri)
                    source.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                val intentType: String? = intent.getType()
                if (intentType != null && intentType.startsWith("image/")) {
                    val imageUri: Uri? = intent.getData()
                    if (imageUri != null) {
                        source.setImageURI(imageUri)
                    }
                }
            }
        }
    }

    private fun initViews() {
        val imgUndo: ImageView
        val imgRedo: ImageView
        val imgCamera: ImageView
        val imgGallery: ImageView
        val imgSave: ImageView
        val imgClose: ImageView
        val imgShare: ImageView
        mPhotoEditorView = findViewById(R.id.photoEditorView)
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool)
        mRvTools = findViewById(R.id.rvConstraintTools)
        mRvFilters = findViewById(R.id.rvFilterView)
        mRootView = findViewById(R.id.rootView)
        imgUndo = findViewById(R.id.imgUndo)
        imgUndo.setOnClickListener(this)
        imgRedo = findViewById(R.id.imgRedo)
        imgRedo.setOnClickListener(this)
        imgCamera = findViewById(R.id.imgCamera)
        imgCamera.setOnClickListener(this)
        imgGallery = findViewById(R.id.imgGallery)
        imgGallery.setOnClickListener(this)
        imgSave = findViewById(R.id.imgSave)
        imgSave.setOnClickListener(this)
        imgClose = findViewById(R.id.imgClose)
        imgClose.setOnClickListener(this)
        imgShare = findViewById(R.id.imgShare)
        imgShare.setOnClickListener(this)
    }

    public override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
        val textEditorDialogFragment: TextEditorDialogFragment =
            TextEditorDialogFragment.Companion.show(this, text, colorCode)
        textEditorDialogFragment.setOnTextEditorListener(TextEditor({ inputText: String?, newColorCode: Int ->
            val styleBuilder: TextStyleBuilder = TextStyleBuilder()
            styleBuilder.withTextColor(newColorCode)
            mPhotoEditor!!.editText(rootView, inputText, styleBuilder)
            mTxtCurrentTool!!.setText(R.string.label_text)
        }))
    }

    public override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]"
        )
    }

    public override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]"
        )
    }

    public override fun onStartViewChangeListener(viewType: ViewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]")
    }

    public override fun onMoveViewChangeListener(viewType: ViewType) {}
    public override fun onStopViewChangeListener(viewType: ViewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]")
    }

    public override fun onTouchSourceImage(event: MotionEvent) {
        Log.d(TAG, "onTouchView() called with: event = [" + event + "]")
    }

    public override fun onRotateViewListener() {}
    public override fun onMirrorViewListener() {}
    public override fun onInFocusViewChangeListener(view: View) {}
    @SuppressLint("NonConstantResourceId")
    public override fun onClick(view: View) {
        when (view.getId()) {
            R.id.imgUndo -> mPhotoEditor!!.undo()
            R.id.imgRedo -> mPhotoEditor!!.redo()
            R.id.imgSave -> saveImage()
            R.id.imgClose -> onBackPressed()
            R.id.imgShare -> shareImage()
            R.id.imgCamera -> {
                val cameraIntent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
            R.id.imgGallery -> {
                val intent: Intent = Intent()
                intent.setType("image/*")
                intent.setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST)
            }
        }
    }

    private fun shareImage() {
        if (mSaveImageUri == null) {
            showSnackbar(getString(R.string.msg_save_image_to_share))
            return
        }
        val intent: Intent = Intent(Intent.ACTION_SEND)
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_STREAM, buildFileProviderUri(mSaveImageUri!!))
        startActivity(Intent.createChooser(intent, getString(R.string.msg_share_image)))
    }

    private fun buildFileProviderUri(uri: Uri): Uri {
        return FileProvider.getUriForFile(
            this,
            FILE_PROVIDER_AUTHORITY,
            File(uri.getPath())
        )
    }

    private fun saveImage() {
        val fileName: String = System.currentTimeMillis().toString() + ".png"
        val hasStoragePermission: Boolean = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (hasStoragePermission || FileSaveHelper.Companion.isSdkHigherThan28()) {
            showLoading("Saving...")
            mSaveFileHelper!!.createFile(
                fileName,
                OnFileCreateResult({ fileCreated: Boolean, filePath: String?, error: String?, uri: Uri? ->
                    if (fileCreated) {
                        val saveSettings: SaveSettings = SaveSettings.Builder()
                            .setClearViewsEnabled(true)
                            .setTransparencyEnabled(true)
                            .build()
                        mPhotoEditor!!.saveAsFile(
                            (filePath)!!,
                            saveSettings,
                            object : OnSaveListener {
                                public override fun onSuccess(imagePath: String) {
                                    mSaveFileHelper!!.notifyThatFileIsNowPubliclyAvailable(
                                        getContentResolver()
                                    )
                                    hideLoading()
                                    showSnackbar("Image Saved Successfully")
                                    mSaveImageUri = uri
                                    mPhotoEditorView!!.getSource().setImageURI(mSaveImageUri)
                                }

                                public override fun onFailure(exception: Exception) {
                                    hideLoading()
                                    showSnackbar("Failed to save Image")
                                }
                            })
                    } else {
                        hideLoading()
                        showSnackbar((error)!!)
                    }
                })
            )
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    mPhotoEditor!!.clearAllViews()
                    val photo: Bitmap? = data!!.getExtras()!!.get("data") as Bitmap?
                    mPhotoEditorView!!.getSource().setImageBitmap(photo)
                }
                PICK_REQUEST -> try {
                    mPhotoEditor!!.clearAllViews()
                    val uri: Uri? = data!!.getData()
                    val bitmap: Bitmap =
                        MediaStore.Images.Media.getBitmap(getContentResolver(), uri)
                    mPhotoEditorView!!.getSource().setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    public override fun onColorChanged(colorCode: Int) {
        mPhotoEditor!!.setShape(mShapeBuilder!!.withShapeColor(colorCode))
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    public override fun onOpacityChanged(opacity: Int) {
        mPhotoEditor!!.setShape(mShapeBuilder!!.withShapeOpacity(opacity))
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    public override fun onShapeSizeChanged(shapeSize: Int) {
        mPhotoEditor!!.setShape(mShapeBuilder!!.withShapeSize(shapeSize.toFloat()))
        mTxtCurrentTool!!.setText(R.string.label_brush)
    }

    public override fun onShapePicked(shapeType: ShapeType?) {
        mPhotoEditor!!.setShape(mShapeBuilder!!.withShapeType(shapeType))
    }

    public override fun onEmojiClick(emojiUnicode: String?) {
        mPhotoEditor!!.addEmoji(emojiUnicode)
        mTxtCurrentTool!!.setText(R.string.label_emoji)
    }

    public override fun onStickerClick(bitmap: Bitmap?) {
        val view: View = mPhotoEditor!!.addImage(bitmap)
        mTxtCurrentTool!!.setText(R.string.label_sticker)
        mGraphicHelper!!.addTouchHandleCallbacks(view)
        realignNewGraphicToCanvas((mPhotoEditorView)!!, view)
    }

    public override fun isPermissionGranted(isGranted: Boolean, permission: String) {
        if (isGranted) {
            saveImage()
        }
    }

    private fun showSaveDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.msg_save_image))
        builder.setPositiveButton(
            "Save",
            DialogInterface.OnClickListener({ dialog: DialogInterface?, which: Int -> saveImage() })
        )
        builder.setNegativeButton(
            "Cancel",
            DialogInterface.OnClickListener({ dialog: DialogInterface, which: Int -> dialog.dismiss() })
        )
        builder.setNeutralButton(
            "Discard",
            DialogInterface.OnClickListener({ dialog: DialogInterface?, which: Int -> finish() })
        )
        builder.create().show()
    }

    public override fun onFilterSelected(photoFilter: PhotoFilter) {
        mPhotoEditor!!.setFilterEffect(photoFilter)
    }

    public override fun onToolSelected(toolType: ToolType) {
        when (toolType) {
            ToolType.SHAPE -> {
                mPhotoEditor!!.setBrushDrawingMode(true)
                mShapeBuilder = ShapeBuilder()
                mPhotoEditor!!.setShape(mShapeBuilder)
                mTxtCurrentTool!!.setText(R.string.label_shape)
                showBottomSheetDialogFragment(mShapeBSFragment)
            }
            ToolType.TEXT -> {
                val textEditorDialogFragment: TextEditorDialogFragment = show(this)
                textEditorDialogFragment.setOnTextEditorListener(TextEditor({ inputText: String?, colorCode: Int ->
                    val styleBuilder: TextStyleBuilder = TextStyleBuilder()
                    styleBuilder.withTextColor(colorCode)
                    mPhotoEditor!!.addText(inputText, styleBuilder)
                    mTxtCurrentTool!!.setText(R.string.label_text)
                }))
            }
            ToolType.ERASER -> {
                mPhotoEditor!!.brushEraser()
                mTxtCurrentTool!!.setText(R.string.label_eraser_mode)
            }
            ToolType.FILTER -> {
                mTxtCurrentTool!!.setText(R.string.label_filter)
                showFilter(true)
            }
            ToolType.EMOJI -> showBottomSheetDialogFragment(mEmojiBSFragment)
            ToolType.STICKER -> showBottomSheetDialogFragment(mStickerBSFragment)
        }
    }

    private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
        if (fragment == null || fragment.isAdded()) {
            return
        }
        fragment.show(getSupportFragmentManager(), fragment.getTag())
    }

    fun showFilter(isVisible: Boolean) {
        mIsFilterVisible = isVisible
        mConstraintSet.clone(mRootView)
        if (isVisible) {
            mConstraintSet.clear(mRvFilters!!.getId(), ConstraintSet.START)
            mConstraintSet.connect(
                mRvFilters!!.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START
            )
            mConstraintSet.connect(
                mRvFilters!!.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        } else {
            mConstraintSet.connect(
                mRvFilters!!.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
            mConstraintSet.clear(mRvFilters!!.getId(), ConstraintSet.END)
        }
        val changeBounds: ChangeBounds = ChangeBounds()
        changeBounds.setDuration(350)
        changeBounds.setInterpolator(AnticipateOvershootInterpolator(1.0f))
        TransitionManager.beginDelayedTransition((mRootView)!!, changeBounds)
        mConstraintSet.applyTo(mRootView)
    }

    public override fun onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false)
            mTxtCurrentTool!!.setText(R.string.app_name)
        } else if (!mPhotoEditor!!.isCacheEmpty()) {
            showSaveDialog()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private val TAG: String = EditImageActivity::class.java.getSimpleName()
        val FILE_PROVIDER_AUTHORITY: String = "com.burhanrashid52.photoeditor.fileprovider"
        private val CAMERA_REQUEST: Int = 52
        private val PICK_REQUEST: Int = 53
        val ACTION_NEXTGEN_EDIT: String = "action_nextgen_edit"
        val PINCH_TEXT_SCALABLE_INTENT_KEY: String = "PINCH_TEXT_SCALABLE"
    }
}