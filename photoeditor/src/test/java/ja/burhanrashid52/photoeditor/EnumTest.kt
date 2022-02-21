package ja.burhanrashid52.photoeditor

import ja.burhanrashid52.photoeditor.shape.ShapeType
import junit.framework.TestCase.assertEquals
import org.junit.Assert
import org.junit.Test

// NOTE(cheng): This test won't run because the Gradle version got upgraded. Fix it.
class EnumTest {
    @Test
    fun testNumberOfViewTypes() {
        assertEquals(ViewType.values().size.toLong(), 4)
    }

    @Test
    fun testNumberOfShapeTypes() {
        assertEquals(ShapeType.values().size.toLong(), 4)
    }

    @Test
    fun testNumberOfPhotoFilterTypes() {
        assertEquals(PhotoFilter.values().size.toLong(), 24)
    }
}