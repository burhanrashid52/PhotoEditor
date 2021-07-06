package ja.burhanrashid52.photoeditor;

import org.junit.Test;

import ja.burhanrashid52.photoeditor.shape.ShapeType;

import static org.junit.Assert.assertEquals;

public class EnumTest {

    @Test
    public void testNumberOfViewTypes() {
        assertEquals(ViewType.values().length, 4);
    }

    @Test
    public void testNumberOfShapeTypes() {
        assertEquals(ShapeType.values().length, 4);
    }

    @Test
    public void testNumberOfPhotoFilterTypes() {
        assertEquals(PhotoFilter.values().length, 24);
    }

}
