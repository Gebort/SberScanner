package com.example.sberqrscanner.domain.use_case

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import com.example.sberqrscanner.R
import com.example.sberqrscanner.data.util.Reaction

class ShareCode(
    private val saveBitmap: SaveBitmap
) {

    operator fun invoke(bitmap: Bitmap, activity: Activity): Reaction<Unit> {
        return when (val reaction = saveBitmap(bitmap)){
            is Reaction.Success -> {
                val uri = reaction.data
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/jpeg"
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share_image)))
                Reaction.Success(Unit)
            }
            is Reaction.Error -> {
                reaction
            }
        }

    }

}