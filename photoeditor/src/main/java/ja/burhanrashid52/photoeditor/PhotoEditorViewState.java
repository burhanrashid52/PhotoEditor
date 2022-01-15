package ja.burhanrashid52.photoeditor;

import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Tracked state of user-added views (stickers, emoji, text, etc)
 */
public class PhotoEditorViewState {

    private Graphic currentGraphic;
    private View currentSelectedView;
    private List<View> addedViews;
    private Stack<View> redoViews;
    private Map<View, MultiTouchListener> multiTouchListenerByView;

    PhotoEditorViewState() {
        this.currentSelectedView = null;
        this.addedViews = new ArrayList<>();
        this.redoViews = new Stack<>();
        this.multiTouchListenerByView = new HashMap<>();
    }

    public View getCurrentSelectedView() {
        return currentSelectedView;
    }

    void setGraphic(Graphic graphic) {
        this.currentSelectedView = graphic.getRootView();
        this.currentGraphic = graphic;
    }

    public Graphic getCurrentGraphic() {
        return currentGraphic;
    }

    public void clearCurrentSelectedView() {
        this.currentSelectedView = null;
    }

    View getAddedView(int index) {
        return addedViews.get(index);
    }

    int getAddedViewsCount() {
        return addedViews.size();
    }

    void clearAddedViews() {
        addedViews.clear();
    }

    void addAddedView(final View view) {
        addedViews.add(view);
    }

    void removeAddedView(final View view) {
        addedViews.remove(view);
    }

    View removeAddedView(final int index) {
        return addedViews.remove(index);
    }

    boolean containsAddedView(final View view) {
        return addedViews.contains(view);
    }

    /**
     * Replaces a view in the current "added views" list.
     *
     * @param view The view to replace
     * @return true if the view was found and replaced, false if the view was not found
     */
    boolean replaceAddedView(final View view) {
        final int i = addedViews.indexOf(view);
        if (i > -1) {
            addedViews.set(i, view);
            return true;
        }
        return false;
    }

    void clearRedoViews() {
        redoViews.clear();
    }

    void pushRedoView(final View view) {
        redoViews.push(view);
    }

    View popRedoView() {
        return redoViews.pop();
    }

    int getRedoViewsCount() {
        return redoViews.size();
    }

    View getRedoView(int index) {
        return redoViews.get(index);
    }

    public Map<View, MultiTouchListener> getMultiTouchListenerByView() {
        return multiTouchListenerByView;
    }
}
