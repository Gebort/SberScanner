package com.example.sberqrscanner.domain.use_case

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import com.example.sberqrscanner.R
import com.example.sberqrscanner.data.util.getCurrentDate
import com.example.sberqrscanner.data.util.toString
import com.example.sberqrscanner.presentation.scanner.adapter.DivisionItem

private const val PAGE_HEIGHT = 1120
private const val PAGE_WIDTH = 792

class GenerateReport {

    operator fun invoke(divisions: List<DivisionItem>, context: Context): PdfDocument {
        val document = PdfDocument()
        val pageInfo = PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page: PdfDocument.Page = document.startPage(pageInfo)

        val title = Paint()
        title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        title.color = Color.BLACK

        val canvas = page.canvas

        title.textAlign = Paint.Align.CENTER
        title.textSize = 24F
        val date = getCurrentDate().toString(context.resources.getString(R.string.date_format))
        canvas.drawText("Отчет $date", PAGE_WIDTH/2F,30F , title)

        title.textAlign = Paint.Align.LEFT
        title.textSize = 15F
        val totalCount = divisions.size
        val absent = divisions.filter { !it.checked }
        val absentCount = absent.count()

        val absentStr = context.resources.getString(R.string.report_absent, absentCount)
        val totalStr = context.resources.getString(R.string.report_total, totalCount)
        val absentListStr = context.resources.getString(R.string.report_absent_list)


        canvas.drawText(absentStr, 30F, 70F, title)
        canvas.drawText(totalStr, 30F, 90F, title)
        canvas.drawText(absentListStr, 30F, 120F, title)

        for (i in 1..absentCount) {
            if (120F+(i*20F)+40 < PAGE_HEIGHT) {
                canvas.drawText(
                    "$i. ${divisions[i - 1].division.name}",
                    30F,
                    120F + (i * 20F),
                    title
                )
            }
            else {
                val moreStr = context.resources.getString(
                    R.string.report_absent_more,
                    absentCount-i+1
                )
                canvas.drawText(
                    moreStr,
                    60F,
                    120F + (i * 20F),
                    title
                )
                break
            }
        }



        document.finishPage(page)

        return document
    }
}