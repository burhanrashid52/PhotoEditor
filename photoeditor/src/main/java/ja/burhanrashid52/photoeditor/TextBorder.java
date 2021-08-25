package ja.burhanrashid52.photoeditor;

public class TextBorder {
    float corner;
    int backGroundColor;
    int strokeWidth;
    int strokeColor;


    public TextBorder(float corner, int backGroundColor, int strokeWidth, int strokeColor) {
        this.corner = corner;
        this.backGroundColor = backGroundColor;
        this.strokeWidth = strokeWidth;
        this.strokeColor = strokeColor;
    }

    public float getCorner() {
        return corner;
    }

    public void setCorner(float corner) {
        this.corner = corner;
    }

    public int getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(int backGroundColor) {
        this.backGroundColor = backGroundColor;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }
}
