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
import android.app.Application
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
import android.widget.EditText
import android.graphics.drawable.ColorDrawable
import kotlin.jvm.JvmOverloads
import androidx.annotation.ColorInt

/**
 * Created by Burhanuddin Rashid on 1/23/2018.
 */
class PhotoApp constructor() : Application() {
    public override fun onCreate() {
        super.onCreate()
        photoApp = this
        /*   FontRequest fontRequest = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs);

        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this, fontRequest)
                .setReplaceAll(true)
                //    .setEmojiSpanIndicatorEnabled(true)
                //     .setEmojiSpanIndicatorColor(Color.GREEN)
                .registerInitCallback(new EmojiCompat.InitCallback() {
                    @Override
                    public void onInitialized() {
                        super.onInitialized();
                        Log.e(TAG, "Success");
                    }

                    @Override
                    public void onFailed(@Nullable Throwable throwable) {
                        super.onFailed(throwable);
                        Log.e(TAG, "onFailed: " + throwable.getMessage());
                    }
                });

     //   BundledEmojiCompatConfig bundledEmojiCompatConfig = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);*/
    }

    val context: Context
        get() {
            return photoApp!!.context
        }

    companion object {
        var photoApp: PhotoApp? = null
            private set
        private val TAG: String = PhotoApp::class.java.getSimpleName()
    }
}