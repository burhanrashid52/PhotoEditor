package ja.burhanrashid52.photoeditor

import junit.framework.TestCase.assertEquals
import org.junit.Test

class EnumTest {

    @Test
    fun testNumberOfPhotoFilterTypes() {
        assertEquals(PhotoFilter.values().size.toLong(), 24)
    }
}