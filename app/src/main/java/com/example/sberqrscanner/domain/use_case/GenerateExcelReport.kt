package com.example.sberqrscanner.domain.use_case

import android.content.Context
import com.example.sberqrscanner.R
import com.example.sberqrscanner.data.util.getCurrentDate
import com.example.sberqrscanner.data.util.toString
import com.example.sberqrscanner.domain.login.Profile
import com.example.sberqrscanner.domain.model.Division
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress


class GenerateExcelReport {

    operator fun invoke(divisions: List<Division>, profile: Profile, context: Context): HSSFWorkbook {

        val hssfWorkbook = HSSFWorkbook()
        val dateLite = getCurrentDate().toString(context.resources.getString(R.string.date_format_small))
        val hssfSheet: HSSFSheet = hssfWorkbook.createSheet(dateLite)

        hssfSheet.addMergedRegion(CellRangeAddress(1,1, 1, 6 ))
        hssfSheet.addMergedRegion(CellRangeAddress(2,2, 1, 6 ))

        val row1 = hssfSheet.createRow(1)
        val cell1B = row1.createCell(1)
        val row2 = hssfSheet.createRow(2)
        val cell2B = row2.createCell(1)

        val date = getCurrentDate().toString(context.resources.getString(R.string.date_format))
        val dateStr = context.resources.getString(R.string.report, date)

        //TITLE

        val font: Font = hssfWorkbook.createFont().also {
            it.fontHeightInPoints = 14

        }

        with (cell1B.cellStyle) {
            setAlignment(HorizontalAlignment.CENTER)
            setFont(font)
        }

        cell1B.setCellValue(dateStr)

        //PROFILE - CITY AND ADDRESS

        cell2B.setCellValue("${profile.city}, ${profile.address}")

        //DIVISIONS COUNT

        val row4 = hssfSheet.createRow(4)
        val cell4B = row4.createCell(1)
        val row5 = hssfSheet.createRow(5)
        val cell5B = row5.createCell(1)
        val row7 = hssfSheet.createRow(7)
        val cell7B = row7.createCell(1)
        val cell7I = row7.createCell(8)

        hssfSheet.addMergedRegion(CellRangeAddress(4,4, 1, 6 ))
        hssfSheet.addMergedRegion(CellRangeAddress(5,5, 1, 6 ))
        hssfSheet.addMergedRegion(CellRangeAddress(7,7, 1, 6 ))
        hssfSheet.addMergedRegion(CellRangeAddress(7,7, 8, 13 ))

        val totalCount = divisions.size
        val absent = divisions.filter { !it.checked }
        val checked = divisions.filter { it.checked }
        val absentCount = absent.count()

        val absentStr = context.resources.getString(R.string.report_absent, absentCount)
        val totalStr = context.resources.getString(R.string.report_total, totalCount)

        cell4B.setCellValue(absentStr)
        cell5B.setCellValue(totalStr)
        cell7B.setCellValue(context.getString(R.string.report_absent_list))
        cell7I.setCellValue(context.getString(R.string.report_checked_list))

        //ABSENT LIST HEAD

        val row8 = hssfSheet.createRow(8)
        val cell8B = row8.createCell(1)
        val cell8C = row8.createCell(2)
        val cell8D = row8.createCell(3)
        val cell8E = row8.createCell(4)
        val cell8F = row8.createCell(5)
        val cell8G = row8.createCell(6)

        cell8B.setCellValue(context.getString(R.string.division_number_r))
        cell8C.setCellValue(context.getString(R.string.division))
        cell8D.setCellValue(context.getString(R.string.floor_r))
        cell8E.setCellValue(context.getString(R.string.wing_r))
        cell8F.setCellValue(context.getString(R.string.phone_r))
        cell8G.setCellValue(context.getString(R.string.fio_r))

        for (i in 0 until absentCount) {
            //ABSENT LIST DATA
            val rowi = hssfSheet.createRow(i + 9)
            val celliB = rowi.createCell(1)
            val celliC = rowi.createCell(2)
            val celliD = rowi.createCell(3)
            val celliE = rowi.createCell(4)
            val celliF = rowi.createCell(5)
            val celliG = rowi.createCell(6)
            celliB.setCellValue(absent[i].number.toString())
            celliC.setCellValue(absent[i].name)
            celliD.setCellValue(absent[i].floor.toString())
            celliE.setCellValue(absent[i].wing)
            celliF.setCellValue(absent[i].phone)
            celliG.setCellValue(absent[i].fio)
            //CHECKED LIST DATA
            if (i < checked.count()) {
                val celliI = rowi.createCell(8)
                val celliJ = rowi.createCell(9)
                val celliK = rowi.createCell(10)
                val celliL = rowi.createCell(11)
                val celliM = rowi.createCell(12)
                val celliN = rowi.createCell(13)
                celliI.setCellValue(checked[i].number.toString())
                celliJ.setCellValue(checked[i].name)
                celliK.setCellValue(checked[i].floor.toString())
                celliL.setCellValue(checked[i].wing)
                celliM.setCellValue(checked[i].phone)
                celliN.setCellValue(checked[i].fio)
            }
        }

        // CHECKED LIST HEAD

        val cell8I = row8.createCell(8)
        val cell8J = row8.createCell(9)
        val cell8K = row8.createCell(10)
        val cell8L = row8.createCell(11)
        val cell8M = row8.createCell(12)
        val cell8N = row8.createCell(13)

        cell8I.setCellValue(context.getString(R.string.division_number_r))
        cell8J.setCellValue(context.getString(R.string.division))
        cell8K.setCellValue(context.getString(R.string.floor_r))
        cell8L.setCellValue(context.getString(R.string.wing_r))
        cell8M.setCellValue(context.getString(R.string.phone_r))
        cell8N.setCellValue(context.getString(R.string.fio_r))

        return hssfWorkbook
    }
}