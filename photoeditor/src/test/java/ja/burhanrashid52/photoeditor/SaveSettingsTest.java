package ja.burhanrashid52.photoeditor;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;


public class SaveSettingsTest {

    /*@Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }*/

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
}