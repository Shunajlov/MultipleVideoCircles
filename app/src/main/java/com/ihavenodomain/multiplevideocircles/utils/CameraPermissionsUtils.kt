package com.ihavenodomain.multiplevideocircles.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.ihavenodomain.multiplevideocircles.R

object CameraPermissionsUtils {
    @JvmStatic
    private val CAMERA_REQUEST_CODE = 1024

    fun checkCameraPermission(activity: Activity): Boolean {
        if (!hasCamera(activity)) return false

        if (!isCameraPermissionGranted(activity)) {
            showAlert(activity)
            return false
        }

        return true
    }

    /** Check if this device has a camera */
    private fun hasCamera(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    private fun isCameraPermissionGranted(context: Context) =
            Build.VERSION.SDK_INT >= 23
                    && ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(23)
    fun askForCameraPermission(activity: Activity) {
        activity.requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    private fun showAlert(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            AlertDialog.Builder(activity)
                    .setMessage(R.string.permission_alert_message)
                    .setPositiveButton(android.R.string.yes) { dialog, _ ->
                        askForCameraPermission(activity)
                        dialog.cancel()
                    }
                    .show()
        }
    }

    fun onRequestPermissionsResult(
            context: Context, requestCode: Int, permissions: Array<out String>,
            grantResults: IntArray
    ): Boolean {
        val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (!permissionGranted) {
                Toast.makeText(context, R.string.permission_not_granted, Toast.LENGTH_SHORT).show()
            }
        }

        return permissionGranted
    }
}