package ja.burhanrashid52.photoeditor;

public enum CropRatio {

    NONE("None", -1f),
    RATIO_1_1("1:1", 1f),
    RATIO_1_2("1:2", 1 / 2f),
    RATIO_1_3("1:3", 1 / 3f),
    RATIO_2_3("2:3", 2 / 3f),
    RATIO_3_4("3:4", 3 / 4f),
    RATIO_2_1("2:1", 2f),
    RATIO_3_1("3:1", 3f),
    RATIO_3_2("3:2", 3 / 2f),
    RATIO_4_3("4:3", 4 / 3f);


    private String ratioName;
    private float rationValue;

    CropRatio(String ratioName, float rationValue) {
        this.ratioName = ratioName;
        this.rationValue = rationValue;
    }

    public String getRatioName() {
        return ratioName;
    }

    public float getRationValue() {
        return rationValue;
    }
}
