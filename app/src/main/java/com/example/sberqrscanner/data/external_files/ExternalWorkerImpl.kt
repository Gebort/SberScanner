package com.example.sberqrscanner.data.external_files

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.sberqrscanner.R
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.data.util.getCurrentDate
import com.example.sberqrscanner.data.util.toString
import com.example.sberqrscanner.domain.external_files.ExternalStorageWorker
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ExternalWorkerImpl: ExternalStorageWorker {

    private fun getUri(data: Any, activity: Activity): Reaction<Uri> {
        try {
            val fileName = when (data) {
                is Bitmap -> {
                    "Code" + System.currentTimeMillis().toString().dropLast(8) + ".jpg"
                }
                is PdfDocument -> {
                    val timeStr = getCurrentDate().toString("dd.MM.yyyy_HH:mm")
                    "Report_${timeStr}.pdf"
                }
                else -> {
                    throw Exception("No realisation for this type")
                }
            }
                val storePath = Environment.getExternalStorageDirectory().absolutePath
                val appDir = File(storePath)
                if (!appDir.exists()) {
                    appDir.mkdir()
                }
                val file = File(appDir, fileName)
                val fileOutputStream = FileOutputStream(file)
                val isSuccess = when (data) {
                    is Bitmap -> {
                        data.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream)
                    }
                    is PdfDocument -> {
                        data.writeTo(fileOutputStream)
                        true
                    }
                    else -> {
                        throw Exception("No realisation for this type")
                    }
                }
                fileOutputStream.flush()
                fileOutputStream.close()

                val uri = FileProvider.getUriForFile(
                    activity,
                    "com.example.sberqrscanner.provider",  //(use your app signature + ".provider" )
                    file
                )

                return if (isSuccess) {
                    Reaction.Success(uri)
                } else {
                    Reaction.Error(Exception("Unknown exception"))
                }
        } catch (e: Exception) {
            return Reaction.Error(e)
        }
    }

    override fun exportBitmap(bitmap: Bitmap, activity: Activity): Reaction<String?> {
        return when (val reaction = getUri(bitmap, activity)) {
            is Reaction.Success -> {
                val uri = reaction.data
                activity.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                Reaction.Success(uri.path)
            }
            is Reaction.Error -> {
                reaction
            }
        }
    }

    private fun shareData(data: Any, activity: Activity): Reaction<Unit> {
        return when (val reaction = getUri(data, activity)){
            is Reaction.Success -> {
                val uri = reaction.data
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = when (data) {
                    is Bitmap -> {
                        "image/jpeg"
                    }
                    is PdfDocument -> {
                        "application/pdf"
                    }
                    else -> {
                        throw Exception("No realisation for this type")
                    }
                }
                val message = when (data) {
                    is Bitmap -> {
                        R.string.share_image
                    }
                    is PdfDocument -> {
                        R.string.share_report
                    }
                    else -> {
                        throw Exception("No realisation for this type")
                    }
                }
                if (intent.type != null) {
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    activity.startActivity(Intent.createChooser(intent, activity.getString(message)))
                    Reaction.Success(Unit)
                }
                else {
                    Reaction.Error(Exception("Wrong type"))
                }

            }
            is Reaction.Error -> {
                reaction
            }
        }
    }

    override fun shareBitmap(bitmap: Bitmap, activity: Activity): Reaction<Unit> {
        return shareData(bitmap, activity)
    }

    override fun sharePdf(pdf: PdfDocument, activity: Activity): Reaction<Unit> {
        return shareData(pdf, activity)
    }

}