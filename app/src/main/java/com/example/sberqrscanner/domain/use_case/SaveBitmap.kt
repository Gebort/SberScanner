package com.example.sberqrscanner.domain.use_case

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.sberqrscanner.data.util.Reaction
import java.io.File
import java.io.FileOutputStream


class SaveBitmap {

    operator fun invoke(bitmap: Bitmap): Reaction<Uri> {
        try {
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            val storePath = Environment.getExternalStorageDirectory().absolutePath
            val appDir = File(storePath)
            if (!appDir.exists()) {
                appDir.mkdir()
            }
            val file = File(appDir, fileName)
            val fileOutputStream = FileOutputStream(file)
            val isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            val uri = Uri.fromFile(file)

            return if (isSuccess){
                Reaction.Success(uri)
            } else {
                Reaction.Error(Exception("Unknown exception"))
            }
        } catch (e: Exception) {
            return Reaction.Error(e)
        }
    }
}