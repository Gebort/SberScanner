package com.example.sberqrscanner.domain.external_files

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import com.example.sberqrscanner.data.util.Reaction

interface ExternalStorageWorker {

    fun exportBitmap(bitmap: Bitmap, activity: Activity): Reaction<String?>

    fun shareBitmap(bitmap: Bitmap, activity: Activity): Reaction<Unit>

    fun sharePdf(pdf: PdfDocument, activity: Activity): Reaction<Unit>

}