package ja.burhanrashid52.photoeditor

import ja.burhanrashid52.photoeditor.shape.ShapeType
import org.junit.Assert
import org.junit.Test

class EnumTest {
    @Test
    fun testNumberOfViewTypes() {
        Assert.assertEquals(ViewType.values().size.toLong(), 4)
    }

    @Test
    fun testNumberOfShapeTypes() {
        Assert.assertEquals(ShapeType.values().size.toLong(), 4)
    }

    @Test
    fun testNumberOfPhotoFilterTypes() {
        Assert.assertEquals(PhotoFilter.values().size.toLong(), 24)
    }
}