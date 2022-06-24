package com.example.sberqrscanner.domain.use_case

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import com.example.sberqrscanner.data.util.Reaction

class ExportCode(
    private val getBitmapUri: GetBitmapUri
) {

    operator fun invoke(code: Bitmap, activity: Activity): Reaction<Unit> {
        return when (val reaction = getBitmapUri(code, activity)) {
            is Reaction.Success -> {
                val uri = reaction.data
                activity.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                Reaction.Success(Unit)
            }
            is Reaction.Error -> {
                reaction
            }
        }
    }

}