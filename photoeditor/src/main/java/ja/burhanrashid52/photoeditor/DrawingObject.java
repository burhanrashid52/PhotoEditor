package ja.burhanrashid52.photoeditor;

import android.view.View;

public class DrawingObject {
    private final ViewType viewType;
    private final View view;
    private final MultiTouchListener multiTouchListener;

    public DrawingObject(ViewType viewType, View view, MultiTouchListener multiTouchListener) {
        this.viewType = viewType;
        this.view = view;
        this.multiTouchListener = multiTouchListener;
    }

    public ViewType getViewType() {
        return viewType;
    }

    public View getView() {
        return view;
    }

    public MultiTouchListener getMultiTouchListener() {
        return multiTouchListener;
    }
}
