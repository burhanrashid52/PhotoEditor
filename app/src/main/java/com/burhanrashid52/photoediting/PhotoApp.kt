package com.burhanrashid52.photoediting

import android.app.Application
import android.content.Context

/**
 * Created by Burhanuddin Rashid on 1/23/2018.
 */
class PhotoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        photoApp = this
    }

    companion object {
        var photoApp: PhotoApp? = null
            private set
        private val TAG = PhotoApp::class.java.simpleName
    }
}