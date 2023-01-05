package ja.burhanrashid52.photoeditor.shape

/**
 * The different kind of known Shapes.
 */
sealed interface ShapeType {

    object Brush : ShapeType
    object Oval : ShapeType
    object Rectangle : ShapeType
    object Line : ShapeType
    class Arrow(val pointerPosition: ArrowPointerPosition = ArrowPointerPosition.START) : ShapeType

}