package com.softgames.petscare.doman.user_case

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class CasePermissions {
    companion object {

        val CAMERA_PERMISSION_CODE = 1

        fun checkCameraPermission(context: Context) = ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        fun requestCameraPermission(context: Context) = ActivityCompat.requestPermissions(
            context as android.app.Activity, arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )

    }
}