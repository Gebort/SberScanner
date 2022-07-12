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
import com.example.sberqrscanner.domain.login.Profile
import com.example.sberqrscanner.domain.model.Division

private const val PAGE_HEIGHT = 1120
private const val PAGE_WIDTH = 792
private const val PADDING = 30F

class GenerateReport {

    operator fun invoke(divisions: List<Division>, profile: Profile, context: Context): PdfDocument {

        val title = Paint()
        title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        title.color = Color.BLACK

        val writer = DocWriter()

        title.textAlign = Paint.Align.CENTER
        title.textSize = 24F
        val date = getCurrentDate().toString(context.resources.getString(R.string.date_format))
        val dateStr = context.resources.getString(R.string.report, date)
       // canvas.drawText(dateStr, PAGE_WIDTH/2F,30F , title)
        writer.writeInDoc(
            PAGE_WIDTH/2F,
            30F,
            dateStr,
            title
        )

        title.textSize = 20F
       // canvas.drawText("${profile.city}, ${profile.address}", PAGE_WIDTH/2F,60F , title)
        writer.writeInDoc(
            PAGE_WIDTH/2F,
            30F,
            "${profile.city}, ${profile.address}",
            title
        )

        title.textAlign = Paint.Align.LEFT
        title.textSize = 15F
        val totalCount = divisions.size
        val absent = divisions.filter { !it.checked }
        val absentCount = absent.count()

        val absentStr = context.resources.getString(R.string.report_absent, absentCount)
        val totalStr = context.resources.getString(R.string.report_total, totalCount)
        val absentListStr = context.resources.getString(R.string.report_absent_list)


     //   canvas.drawText(absentStr, 30F, 100F, title)
        writer.writeInDoc(
            30F,
            40F,
            absentStr,
            title
        )
      //  canvas.drawText(totalStr, 30F, 120F, title)
        writer.writeInDoc(
            30F,
            20F,
            totalStr,
            title
        )
 //       canvas.drawText(absentListStr, 30F, 160F, title)
        writer.writeInDoc(
            30F,
            40F,
            absentListStr,
            title
        )

        for (i in 1 .. absentCount) {
            writer.writeInDoc(
                    30F,
                    20F,
                    "$i. ${absent[i - 1].name}",
                    title
                )
        }



        return writer.getDoc()
    }

    private inner class DocWriter {
        private val pages = mutableListOf<PdfDocument.Page>()
        private val doc = PdfDocument()
        var lastY = PADDING

        init {
            newPage()
        }

        private fun newPage(){
            if (pages.isNotEmpty()){
                doc.finishPage(pages.last())
            }
            val pageInfo = PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pages.size).create()
            val newPage: PdfDocument.Page = doc.startPage(pageInfo)
            pages.add(newPage)
        }

        fun writeInDoc(
            x: Float,
            dy: Float,
            text: String,
            paint: Paint,
        ){
            lastY = if (lastY + dy + PADDING > PAGE_HEIGHT){
                newPage()
                PADDING
            } else {
                (lastY + dy)
            }

            if (lastY < PADDING) {
                lastY = PADDING
            }

            val page = pages.last()
            page.canvas.drawText(
                text,
                x % PAGE_WIDTH,
                lastY,
                paint
            )

        }

        fun getDoc(): PdfDocument {
            doc.finishPage(pages.last())
            return doc
        }

    }
}