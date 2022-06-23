package com.example.sberqrscanner.domain.use_case

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.example.sberqrscanner.domain.scanner.ScanResult
import com.example.sberqrscanner.domain.scanner.Scanner
import kotlinx.coroutines.flow.Flow

class BindCamera(
    private val scanner: Scanner
) {

    operator fun invoke(
        lifecycleOwner: LifecycleOwner,
        context: Context,
        previewView: PreviewView
    ): Flow<ScanResult> {
        return scanner.bindCamera(lifecycleOwner, context, previewView)
    }

}