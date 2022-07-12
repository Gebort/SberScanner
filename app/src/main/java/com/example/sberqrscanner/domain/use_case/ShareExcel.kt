package com.example.sberqrscanner.domain.use_case

import android.app.Activity
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.external_files.ExternalStorageWorker
import org.apache.poi.hssf.usermodel.HSSFWorkbook

class ShareExcel(
    private val externalStorageWorker: ExternalStorageWorker
) {

    suspend operator fun invoke(workbook: HSSFWorkbook, activity: Activity): Reaction<Unit> {
        return externalStorageWorker.shareFile(
            ExternalStorageWorker.FileOption.Excel(workbook),
            activity)

    }

}