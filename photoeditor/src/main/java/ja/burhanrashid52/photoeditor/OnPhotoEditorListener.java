package ja.burhanrashid52.photoeditor;

import android.view.View;

/**
 * Created by Burhanuddin Rashid on 18/01/2017.
 */

public interface OnPhotoEditorListener {

    void onEditTextChangeListener(View rootView, String text, int colorCode);

    void onAddViewListener(ViewType viewType, int numberOfAddedViews);

    void onRemoveViewListener(int numberOfAddedViews);

    void onStartViewChangeListener(ViewType viewType);

    void onStopViewChangeListener(ViewType viewType);
}
