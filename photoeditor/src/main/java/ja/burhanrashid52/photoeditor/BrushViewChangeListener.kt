package ja.mhdxbilal007.photoeditor

/**
 * Created on 1/17/2018.
 * @author [mhdxbilal007](https://github.com/mhdxbilal007)
 *
 *
 */
interface BrushViewChangeListener {
    fun onViewAdd(drawingView: DrawingView)
    fun onViewRemoved(drawingView: DrawingView)
    fun onStartDrawing()
    fun onStopDrawing()
}