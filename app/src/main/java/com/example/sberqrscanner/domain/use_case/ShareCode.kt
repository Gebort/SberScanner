package com.example.sberqrscanner.domain.use_case

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import com.example.sberqrscanner.R
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.external_files.ExternalStorageWorker

class ShareCode(
    private val externalStorageWorker: ExternalStorageWorker
) {

    suspend operator fun invoke(bitmap: Bitmap, activity: Activity): Reaction<Unit> {
        return externalStorageWorker.shareFile(
            ExternalStorageWorker.FileOption.Image(bitmap),
            activity)

    }

}