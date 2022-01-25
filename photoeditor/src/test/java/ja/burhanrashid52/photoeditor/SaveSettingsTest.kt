package ja.burhanrashid52.photoeditor

import junit.framework.TestCase
import android.graphics.Bitmap.CompressFormat
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import org.junit.Test

class SaveSettingsTest {
    @Test
    fun testByDefaultTransparentAndClearViewFlagSettingIsEnabled() {
        val saveSettings = SaveSettings.Builder().build()
        TestCase.assertTrue(saveSettings.isClearViewsEnabled)
        TestCase.assertTrue(saveSettings.isTransparencyEnabled)
    }

    @Test
    fun testWhenTransparentSettingIsDisabled() {
        val saveSettings = SaveSettings.Builder()
            .setTransparencyEnabled(false)
            .build()
        assertFalse(saveSettings.isTransparencyEnabled)
        TestCase.assertTrue(saveSettings.isClearViewsEnabled)
    }

    @Test
    fun testWhenClearViewAfterSaveSettingIsDisabled() {
        val saveSettings = SaveSettings.Builder()
            .setClearViewsEnabled(false)
            .build()
        assertFalse(saveSettings.isClearViewsEnabled)
        TestCase.assertTrue(saveSettings.isTransparencyEnabled)
    }

    @Test
    fun testWhenBothTransparentClearViewAfterSaveSettingIsDisabled() {
        val saveSettings = SaveSettings.Builder()
            .setClearViewsEnabled(false)
            .setTransparencyEnabled(false)
            .build()
        assertFalse(saveSettings.isClearViewsEnabled)
        assertFalse(saveSettings.isTransparencyEnabled)
    }

    @Test
    fun testDefaultCompressAndQualitySaveSettings() {
        val saveSettings = SaveSettings.Builder()
            .build()
        assertEquals(saveSettings.compressFormat, CompressFormat.PNG)
        assertEquals(saveSettings.compressQuality, 100)
    }

    @Test
    fun testCompressAndQualitySaveSettingsValues() {
        val compressFormat = CompressFormat.PNG
        val compressQuality = 50
        val saveSettings = SaveSettings.Builder()
            .setCompressFormat(compressFormat)
            .setCompressQuality(compressQuality)
            .build()
        assertEquals(saveSettings.compressFormat, compressFormat)
        assertEquals(saveSettings.compressQuality, compressQuality)
    }
}