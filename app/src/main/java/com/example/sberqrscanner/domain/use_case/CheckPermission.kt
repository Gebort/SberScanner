package com.example.sberqrscanner.domain.use_case

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class CheckPermission {

    operator fun invoke(permission: String, code: Int, activity: Activity): Boolean {
        return (ActivityCompat.checkSelfPermission(
            activity.applicationContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED)
    }
}