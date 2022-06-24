package com.example.sberqrscanner.domain.use_case

import android.app.Activity
import androidx.core.app.ActivityCompat

class RequestPermission(
    private val checkPermission: CheckPermission
) {

    operator fun invoke(permission: String, code: Int, activity: Activity) {
        if (!checkPermission(permission, code, activity)){
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission),
                code
            )
        }
    }

}