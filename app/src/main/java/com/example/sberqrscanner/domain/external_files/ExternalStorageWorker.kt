package com.example.sberqrscanner.domain.external_files

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import com.example.sberqrscanner.data.util.Reaction

interface ExternalStorageWorker {

    fun exportFile(file: FileOption, activity: Activity): Reaction<String?>

    fun shareFile(file: FileOption, activity: Activity): Reaction<Unit>

    sealed class FileOption {
        class Pdf(val data: PdfDocument): FileOption()
        class Image(val data: Bitmap): FileOption()
    }

}