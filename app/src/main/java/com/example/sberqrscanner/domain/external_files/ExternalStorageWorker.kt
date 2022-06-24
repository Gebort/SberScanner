package com.example.sberqrscanner.domain.external_files

interface ExternalStorageWorker {

    fun getPdfUri()

    fun getBitmapUri()

    fun exportBitmap()

    fun shareBitmap()

    fun sharePdf()

}