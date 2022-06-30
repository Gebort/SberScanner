package com.example.sberqrscanner.domain.use_case

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.external_files.ExternalStorageWorker

class SharePdf(
    private val externalStorageWorker: ExternalStorageWorker
) {

    suspend operator fun invoke(pdf: PdfDocument, activity: Activity): Reaction<Unit> {
        return externalStorageWorker.shareFile(
            ExternalStorageWorker.FileOption.Pdf(pdf),
            activity)

    }

}