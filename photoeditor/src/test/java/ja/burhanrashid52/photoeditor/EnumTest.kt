package ja.burhanrashid52.photoeditor

import ja.burhanrashid52.photoeditor.shape.ShapeType
import junit.framework.TestCase.assertEquals
import org.junit.Assert
import org.junit.Test

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