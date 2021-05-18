package ja.burhanrashid52.photoeditor;

import android.view.View;

/**
 * Created by Burhanuddin Rashid on 17/05/21.
 *
 * @author <https://github.com/burhanrashid52>
 */
public class BrushAdapter extends Graphic implements BrushViewChangeListener {
    private final GraphicManager mGraphicManager;

    BrushAdapter(BrushDrawingView brushDrawingView, GraphicManager graphicManager) {
        super(brushDrawingView, graphicManager);
        mGraphicManager = graphicManager;
        brushDrawingView.setBrushViewChangeListener(this);
    }

    @Override
    ViewType getViewType() {
        return ViewType.BRUSH_DRAWING;
    }

    @Override
    int getLayoutId() {
        return 0;
    }

    @Override
    void setupView(View rootView) {

    }

    @Override
    public void onViewAdd(BrushDrawingView brushDrawingView) {
        mGraphicManager.addView(this);
    }

    @Override
    public void onViewRemoved(BrushDrawingView brushDrawingView) {
        mGraphicManager.removeView(this);
    }

    @Override
    public void onStartDrawing() {
        OnPhotoEditorListener onPhotoEditorListener = mGraphicManager.getOnPhotoEditorListener();
        if (onPhotoEditorListener != null) {
            onPhotoEditorListener.onStartViewChangeListener(ViewType.BRUSH_DRAWING);
        }
    }

    @Override
    public void onStopDrawing() {
        OnPhotoEditorListener onPhotoEditorListener = mGraphicManager.getOnPhotoEditorListener();
        if (onPhotoEditorListener != null) {
            onPhotoEditorListener.onStopViewChangeListener(ViewType.BRUSH_DRAWING);
        }
    }
}
