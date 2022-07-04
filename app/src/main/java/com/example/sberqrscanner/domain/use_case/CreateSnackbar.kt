package com.example.sberqrscanner.domain.use_case

import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

class CreateSnackbar {

    operator fun invoke(
        view: View,
        type: Int,
        contentStr: String? = null,
        contentId: Int? = null,
        actionStr: String? = null,
        actionId: Int? = null,
        action: (() -> Unit)? = null)
    {
        val content = contentStr
                ?: if (contentId != null) view.context.getString(contentId)
                else throw Exception("no content provided for snackbar")

        if (type == SmallBottom || type == SmallCenter) {
            val toast = Toast.makeText(
                view.context,
                content,
                Toast.LENGTH_SHORT
            )
            toast.setGravity(
                if (type == SmallBottom) Gravity.BOTTOM else Gravity.CENTER,
                0,
                0)
            toast.show()
        }
        else if (type == Large) {
            Snackbar.make(
                view,
                content,
                Snackbar.LENGTH_LONG
            )
                .show()
        }
        else if (type == LargeAction) {
            val actionName = actionStr
                ?: if (actionId != null) view.context.getString(actionId)
                else throw Exception("no action provided for snackbar")
            if (action == null){
                throw Exception("Missing action function for snackbar")
            }
            Snackbar.make(
                view,
                content,
                Snackbar.LENGTH_LONG
            )
                .setAction(actionName) {
                    action()
                }
                .show()
        }
        else {
            throw Exception("Unknown snackbar type")
        }
    }

    companion object {
        const val SmallBottom = 0
        const val SmallCenter = 1
        const val Large = 2
        const val LargeAction = 3
    }

}