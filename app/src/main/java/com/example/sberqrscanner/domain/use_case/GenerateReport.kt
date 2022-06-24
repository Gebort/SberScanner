package com.example.sberqrscanner.domain.use_case

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.presentation.scanner.adapter.DivisionItem


class GenerateReport {

    operator fun invoke(divisions: List<DivisionItem>): PdfDocument {
        val document = PdfDocument()
        val pageInfo = PageInfo.Builder(100, 100, 1).create()
        val page: PdfDocument.Page = document.startPage(pageInfo)

        var title = Paint()
        title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        title.textSize = 15F
        title.color = Color.BLACK

        var canvas = page.canvas

        canvas.drawText("Hello world", 209F, 100F, title)
        canvas.drawText("High hopes", 209F, 80F, title)

        title.textAlign = Paint.Align.CENTER
        canvas.drawText("Welcome to the machine", 396F, 560F, title)

        document.finishPage(page)
        document.close()

        return document
    }
}