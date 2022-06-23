package com.example.sberqrscanner.domain.scanner

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.Flow

interface Scanner {

    fun bindCamera(
        lifecycleOwner: LifecycleOwner,
        context: Context,
        previewView: PreviewView
    ): Flow<ScanResult>

}