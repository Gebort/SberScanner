package com.example.sberqrscanner.domain.external_files

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import com.example.sberqrscanner.data.util.Reaction
import org.apache.poi.hssf.usermodel.HSSFWorkbook

interface ExternalStorageWorker {

    suspend fun shareFile(file: FileOption, activity: Activity): Reaction<Unit>

    sealed class FileOption {
        class Pdf(val data: PdfDocument): FileOption()
        class Image(val data: Bitmap): FileOption()
        class Excel(val data: HSSFWorkbook): FileOption()
    }

}