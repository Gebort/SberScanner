package com.example.sberqrscanner.domain.use_case

import android.app.Activity
import androidx.core.app.ActivityCompat

class CheckRequestPerm(
    private val checkPermission: CheckPermission
) {

    operator fun invoke(
        permission: String,
        code: Int,
        activity: Activity
    ): Boolean {
            if (checkPermission(
                    permission,
                    code,
                    activity
                )){
                return true
            }
            else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permission),
                    code
                )
                return false
        }
    }

}