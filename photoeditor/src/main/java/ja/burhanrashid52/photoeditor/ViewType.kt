package ja.burhanrashid52.photoeditor

/**
 *
 *
 * Enum define for various operation happening on the [PhotoEditorView] while editing
 *
 *
 * @author [Burhanuddin Rashid](https://github.com/burhanrashid52)
 * @version 0.1.1
 * @since 18/01/2017.
 */
sealed interface ViewType {
    object Brush : ViewType
    object Text : ViewType
    object Image : ViewType
    object Emoji : ViewType
    data class Custom<T : Any>(val data: T) : ViewType
}