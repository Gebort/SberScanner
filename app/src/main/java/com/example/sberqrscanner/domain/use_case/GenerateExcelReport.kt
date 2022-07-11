package com.example.sberqrscanner.domain.use_case

import android.content.Context
import com.example.sberqrscanner.R
import com.example.sberqrscanner.data.util.getCurrentDate
import com.example.sberqrscanner.data.util.toString
import com.example.sberqrscanner.domain.login.Profile
import com.example.sberqrscanner.domain.model.Division
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress


class GenerateExcelReport {

    operator fun invoke(divisions: List<Division>, profile: Profile, context: Context): Workbook {

        val hssfWorkbook = HSSFWorkbook()
        val dateLite = getCurrentDate().toString(context.resources.getString(R.string.date_format))
        val hssfSheet: HSSFSheet = hssfWorkbook.createSheet(dateLite)

        hssfSheet.addMergedRegion(CellRangeAddress(1,1, 1, 6 ))
        hssfSheet.addMergedRegion(CellRangeAddress(2,2, 1, 6 ))

        val row1 = hssfSheet.createRow(1)
        val cell1B = row1.createCell(1)
        val row2 = hssfSheet.createRow(2)
        val cell2B = row2.createCell(1)

        val date = getCurrentDate().toString(context.resources.getString(R.string.date_format))
        val dateStr = context.resources.getString(R.string.report, date)

        cell1B.setCellValue(dateStr)
        cell2B.setCellValue("${profile.city}, ${profile.address}")

        return hssfWorkbook
    }

}