package com.example.sberqrscanner.domain.use_case

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.external_files.ExternalStorageWorker

class ExportCode(
    private val externalStorageWorker: ExternalStorageWorker
) {

    suspend operator fun invoke(code: Bitmap, activity: Activity): Reaction<String?> {
        return externalStorageWorker.exportFile(
            ExternalStorageWorker.FileOption.Image(code),
            activity
        )
    }

}