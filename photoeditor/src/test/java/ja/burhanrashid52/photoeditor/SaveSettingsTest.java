package ja.burhanrashid52.photoeditor;

import android.graphics.Bitmap;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;


public class SaveSettingsTest {

    @Test
    public void testByDefaultTransparentAndClearViewFlagSettingIsEnabled() {
        SaveSettings saveSettings = new SaveSettings.Builder().build();
        assertTrue(saveSettings.isClearViewsEnabled());
        assertTrue(saveSettings.isTransparencyEnabled());
    }

    @Test
    public void testWhenTransparentSettingIsDisabled() {
        SaveSettings saveSettings = new SaveSettings.Builder()
                .setTransparencyEnabled(false)
                .build();

        assertFalse(saveSettings.isTransparencyEnabled());
        assertTrue(saveSettings.isClearViewsEnabled());
    }

    @Test
    public void testWhenClearViewAfterSaveSettingIsDisabled() {
        SaveSettings saveSettings = new SaveSettings.Builder()
                .setClearViewsEnabled(false)
                .build();

        assertFalse(saveSettings.isClearViewsEnabled());
        assertTrue(saveSettings.isTransparencyEnabled());
    }

    @Test
    public void testWhenBothTransparentClearViewAfterSaveSettingIsDisabled() {
        SaveSettings saveSettings = new SaveSettings.Builder()
                .setClearViewsEnabled(false)
                .setTransparencyEnabled(false)
                .build();

        assertFalse(saveSettings.isClearViewsEnabled());
        assertFalse(saveSettings.isTransparencyEnabled());
    }

    @Test
    public void testDefaultCompressAndQualitySaveSettings() {
        SaveSettings saveSettings = new SaveSettings.Builder()
                .build();

        assertEquals(saveSettings.getCompressFormat(), Bitmap.CompressFormat.PNG);
        assertEquals(saveSettings.getCompressQuality(), 100);
    }

    @Test
    public void testCompressAndQualitySaveSettingsValues() {

        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
        int compressQuality = 50;

        SaveSettings saveSettings = new SaveSettings.Builder()
                .setCompressFormat(compressFormat)
                .setCompressQuality(compressQuality)
                .build();

        assertEquals(saveSettings.getCompressFormat(), compressFormat);
        assertEquals(saveSettings.getCompressQuality(), compressQuality);
    }
}