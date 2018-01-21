package ja.burhanrashid52.photoeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * Created by Burhanuddin Rashid on 1/18/2018.
 */

public class PhotoEditorView extends FrameLayout {
    private ImageView mImgSource;
    private BrushDrawingView mBrushDrawingView;
    private RelativeLayout mParentLayout;

    public PhotoEditorView(Context context) {
        super(context);
        init(null);
    }

    public PhotoEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @SuppressLint("Recycle")
    private void init(@Nullable AttributeSet attrs) {
        View rootView = inflate(getContext(), R.layout.photo_editor_view, null);
        mParentLayout = rootView.findViewById(R.id.parentImgSource);
        mImgSource = rootView.findViewById(R.id.imgSource);
        mBrushDrawingView = rootView.findViewById(R.id.brushDrawing);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PhotoEditorView);
            Drawable imgSrcDrawable = a.getDrawable(R.styleable.PhotoEditorView_src);
            if (imgSrcDrawable != null) {
                mImgSource.setImageDrawable(imgSrcDrawable);
            }
        }
        addView(rootView);
    }

    public ImageView getImageSource() {
        return mImgSource;
    }

    BrushDrawingView getBrushDrawingView() {
        return mBrushDrawingView;
    }

    RelativeLayout getParentLayout() {
        return mParentLayout;
    }
}
