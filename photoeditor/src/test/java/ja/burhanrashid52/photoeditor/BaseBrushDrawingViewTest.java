package ja.burhanrashid52.photoeditor;

import android.content.Context;
import androidx.annotation.NonNull;

import org.robolectric.RuntimeEnvironment;

public class BaseBrushDrawingViewTest {
    protected Context mContext = RuntimeEnvironment.systemContext;

    @NonNull
    protected BrushDrawingView setupBrushForTouchEvents(BrushViewChangeListener brushViewChangeListener) {
        BrushDrawingView brushDrawingView = new BrushDrawingView(mContext);
        brushDrawingView.setBrushDrawingMode(true);
        brushDrawingView.setBrushViewChangeListener(brushViewChangeListener);
        brushDrawingView.onSizeChanged(500, 500, 500, 500);
        return brushDrawingView;
    }

}
