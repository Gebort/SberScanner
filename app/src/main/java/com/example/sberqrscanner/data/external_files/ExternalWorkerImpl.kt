package com.example.sberqrscanner.data.external_files

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.sberqrscanner.R
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.data.util.getCurrentDate
import com.example.sberqrscanner.data.util.toString
import com.example.sberqrscanner.domain.external_files.ExternalStorageWorker
import java.io.File
import java.io.FileOutputStream

class ExternalWorkerImpl: ExternalStorageWorker {

    private fun getUri(option: ExternalStorageWorker.FileOption, activity: Activity): Reaction<Uri> {
        try {
            val fileName = when (option) {
                is ExternalStorageWorker.FileOption.Image -> {
                    "Code" + System.currentTimeMillis().toString().drop(8) + ".jpg"
                }
                is ExternalStorageWorker.FileOption.Pdf -> {
                    val timeStr = getCurrentDate().toString("dd.MM.yyyy_HH.mm")
                    "Report_${timeStr}.pdf"
                }
                else -> {
                    throw Exception("getUri: No realisation for this type")
                }
            }

               val storePath = Environment.getExternalStorageDirectory().absolutePath
                val appDir = File(storePath)
                if (!appDir.exists()) {
                    appDir.mkdir()
                }
                val file = File(appDir, fileName)
                val fileOutputStream = FileOutputStream(file)
                val isSuccess = when (option) {
                    is ExternalStorageWorker.FileOption.Image -> {
                        option.data.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream)
                    }
                    is ExternalStorageWorker.FileOption.Pdf -> {
                        option.data.writeTo(fileOutputStream)
                        true
                    }
                    else -> {
                        throw Exception("getUri: No realisation for this type")
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
                    Reaction.Error(Exception("getUri: Unknown exception"))
                }
        } catch (e: Exception) {
            return Reaction.Error(e)
        }
    }

    override fun exportFile(option: ExternalStorageWorker.FileOption, activity: Activity): Reaction<String?> {
        return when (val reaction = getUri(option, activity)) {
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

    override fun shareFile(file: ExternalStorageWorker.FileOption, activity: Activity): Reaction<Unit> {
        return when (val reaction = getUri(file, activity)){
            is Reaction.Success -> {
                val uri = reaction.data
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = when (file) {
                    is ExternalStorageWorker.FileOption.Image -> {
                        "image/jpeg"
                    }
                    is ExternalStorageWorker.FileOption.Pdf -> {
                        "application/pdf"
                    }
                    else -> {
                        throw Exception("shareFile: No realisation for this type")
                    }
                }
                val message = when (file) {
                    is ExternalStorageWorker.FileOption.Image -> {
                        R.string.share_image
                    }
                    is ExternalStorageWorker.FileOption.Pdf -> {
                        R.string.share_report
                    }
                    else -> {
                        throw Exception("shareFile: No realisation for this type")
                    }
                }
                if (intent.type != null) {
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    activity.startActivity(Intent.createChooser(intent, activity.getString(message)))
                    Reaction.Success(Unit)
                }
                else {
                    Reaction.Error(Exception("shareFile: Wrong type"))
                }

            }
            is Reaction.Error -> {
                reaction
            }
        }
    }

}