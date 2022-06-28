package com.example.sberqrscanner.data.code_generator

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import com.example.sberqrscanner.domain.code_generator.CodeGenerator
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

private const val WIDTH_QR = 700
private const val HEIGHT_QR = 700
private const val WIDTH_128 = 700
private const val HEIGHT_128 = 350

class QRCodeGenerator: CodeGenerator {

    override suspend fun generateCode(option: CodeGenerator.Option, data: String): Bitmap =
        withContext(Dispatchers.Default){
            when (option) {
                is CodeGenerator.Option.QRCode -> {
                    val writer = QRCodeWriter()
                    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, WIDTH_QR, HEIGHT_QR)
                    val width = bitMatrix.width
                    val height = bitMatrix.height
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                    for (x in 0 until width) {
                        for (y in 0 until height) {
                            bitmap.setPixel(
                                x,
                                y,
                                if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                            )
                        }
                    }
                    return@withContext bitmap
                }
                is CodeGenerator.Option.Code128 -> {
                    val writer = MultiFormatWriter()
                    val finalData: String = Uri.encode(data)

                    // Use 1 as the height of the matrix as this is a 1D Barcode.

                    val bm = writer.encode(finalData, BarcodeFormat.CODE_128, WIDTH_128, 1)
                    val bmWidth = bm.width

                    val bitmap = Bitmap.createBitmap(bmWidth, HEIGHT_128, Bitmap.Config.ARGB_8888)

                    for (i in 0 until bmWidth) {
                        // Paint columns of width 1
                        val column = IntArray(HEIGHT_128)
                        Arrays.fill(column, if (bm[i, 0]) Color.BLACK else Color.WHITE)
                        bitmap.setPixels(column, 0, 1, i, 0, 1, HEIGHT_128)
                    }

                    return@withContext bitmap
                }
                else -> {
                    throw Exception("generateCode: No realisation for this type")
                }
            }
    }
}