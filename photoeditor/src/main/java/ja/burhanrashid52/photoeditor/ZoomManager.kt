package ja.burhanrashid52.photoeditor

interface ZoomListener {
    fun onBackgroundZoomStarted()
    fun onBackgroundZoomChanged(scale: Float)
}

object ZoomManager {
    var scale: Float = 1f
    private val zoomListeners = mutableListOf<ZoomListener>()

    fun addZoomListener(listener: ZoomListener) {
        zoomListeners.add(listener)
    }

    fun removeZoomListener(listener: ZoomListener) {
        zoomListeners.remove(listener)
    }

    fun notifyZoomChange(scale: Float) {
        this.scale = scale
        zoomListeners.forEach {
            it.onBackgroundZoomChanged(scale)
        }
    }

    fun notifyZoomStart() {
        zoomListeners.forEach {
            it.onBackgroundZoomStarted()
        }
    }
}