package com.example.sberqrscanner.domain.use_case

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.example.sberqrscanner.data.util.Reaction
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ExportCode(
    private val saveBitmap: SaveBitmap
) {

    operator fun invoke(code: Bitmap, activity: Activity): Reaction<Unit> {
        return when (val reaction = saveBitmap(code)) {
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