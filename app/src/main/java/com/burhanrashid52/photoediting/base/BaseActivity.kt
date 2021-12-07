package com.burhanrashid52.photoediting.base

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Burhanuddin Rashid on 1/17/2018.
 */
open class BaseActivity : AppCompatActivity() {
    private var mProgressDialog: ProgressDialog? = null
    fun requestPermission(permission: String): Boolean {
        val isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                    this, arrayOf(permission),
                    READ_WRITE_STORAGE)
        }
        return isGranted
    }

    open fun isPermissionGranted(isGranted: Boolean, permission: String?) {}
    fun makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            READ_WRITE_STORAGE -> isPermissionGranted(grantResults[0] == PackageManager.PERMISSION_GRANTED, permissions[0])
        }
    }

    protected fun showLoading(message: String) {
        mProgressDialog = ProgressDialog(this).apply {
            setMessage(message)
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            setCancelable(false)
            show()
        }

    }

    protected fun hideLoading() {
        mProgressDialog?.dismiss()
    }

    protected fun showSnackbar(message: String) {
        val view = findViewById<View>(android.R.id.content)
        view?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
            ?: Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val READ_WRITE_STORAGE = 52
    }
}