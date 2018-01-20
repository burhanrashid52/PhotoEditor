package ja.burhanrashid52.photoeditor;

import android.content.Context;
import android.os.Build;
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
    private View rootView;

    public PhotoEditorView(Context context) {
        super(context);
        init();
    }

    public PhotoEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        rootView = inflate(getContext(), R.layout.photo_editor_view, null);
        mParentLayout = rootView.findViewById(R.id.parentImgSource);
        mImgSource = rootView.findViewById(R.id.imgSource);
        mBrushDrawingView = rootView.findViewById(R.id.brushDrawing);
        addView(rootView);
    }

    //Get Imageview
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
