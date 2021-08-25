package ja.burhanrashid52.photoeditor;

/**
 * Created on 1/17/2018.
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * <p></p>
 */

interface BrushViewChangeListener {
    void onViewAdd(DrawingView drawingView);

    void onViewRemoved(DrawingView drawingView);

    void onStartDrawing();

    void onStopDrawing();
}
