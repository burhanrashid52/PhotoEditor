package ja.burhanrashid52.photoeditor;

import android.view.View;

/**
 * <p>
 * This is custom drawing view used to do painting on user touch events it it will paint on canvas
 * as per attributes provided to the paint
 * </p>
 *
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.1
 * @since 12/1/18
 */
public class ViewInfo {

    private boolean isDeleting = false;

    private final int SCALE_DOWN_FACTOR = 8;

    private float defaultScaleX = 0;
    private float defaultScaleY = 0;
    private float scaleDownX = 0;
    private float scaleDownY = 0;

    public ViewInfo(View view) {
        defaultScaleX = view.getScaleX();
        defaultScaleY = view.getScaleY();
        scaleDownX = defaultScaleX/SCALE_DOWN_FACTOR;
        scaleDownY = defaultScaleY/SCALE_DOWN_FACTOR;

    }

    public void setDefaultScaleX(float defaultScaleX) {
        this.defaultScaleX = defaultScaleX;

    }

    public void setDefaultScaleY(float defaultScaleY) {
        this.defaultScaleY = defaultScaleY;
    }

    public boolean isDeleting() {
        return isDeleting;
    }

    public void setDeleting(boolean deleting) {
        isDeleting = deleting;
    }

    public float getDefaultScaleX() {
        return defaultScaleX;
    }

    public float getDefaultScaleY() {
        return defaultScaleY;
    }

    public float getScaledDownX(){
        return scaleDownX;
    }
    public float getScaledDownY(){
        return scaleDownY;
    }

}