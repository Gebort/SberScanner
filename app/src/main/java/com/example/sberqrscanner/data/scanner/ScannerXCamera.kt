package com.example.sberqrscanner.data.scanner

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.sberqrscanner.data.util.await
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.domain.scanner.ScanResult
import com.example.sberqrscanner.domain.scanner.Scanner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.Executors

private const val TAG = "SCANNER"

class ScannerXCamera: Scanner {

    @SuppressLint("UnsafeOptInUsageError")
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun bindCamera(
        lifecycleOwner: LifecycleOwner,
        context: Context,
        previewView: PreviewView
    ): Flow<ScanResult> {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val flow = callbackFlow {
            cameraProviderFuture.addListener(
                {
                    val cameraProvider = cameraProviderFuture.get()

                    // setting up the preview use case
                    val previewUseCase = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                    // configure to use the back camera
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    // configure our MLKit BarcodeScanning client
                    /* passing in our desired barcode formats - MLKit supports additional formats outside of the ones listed here, and you may not need to offer support for all of these. You should only specify the ones you need */
                    val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
                        Barcode.FORMAT_CODE_128,
                        Barcode.FORMAT_CODE_39,
                        Barcode.FORMAT_CODE_93,
                        Barcode.FORMAT_EAN_8,
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_UPC_A,
                        Barcode.FORMAT_UPC_E,
                        Barcode.FORMAT_PDF417
                    ).build()
                    // getClient() creates a new instance of the MLKit barcode scanner with the specified options
                    val barcodeScanner = BarcodeScanning.getClient(options)

                    // setting up the analysis use case
                    val analysisUseCase = ImageAnalysis.Builder()
                        .build()

                    // define the actual functionality of our analysis use case
                    analysisUseCase.setAnalyzer(
                        // newSingleThreadExecutor() will let us perform analysis on a single worker thread
                        Executors.newSingleThreadExecutor()
                    ) { imageProxy ->
                        imageProxy.image?.let { image ->
                            val inputImage =
                                InputImage.fromMediaImage(
                                    image,
                                    imageProxy.imageInfo.rotationDegrees
                                )

                            barcodeScanner.process(inputImage)
                                .addOnSuccessListener { barcodeList ->
                                    val barcode = barcodeList.getOrNull(0)
                                    // `rawValue` is the decoded value of the barcode
                                    barcode?.rawValue?.let { value ->
                                        // update our textView to show the decoded value
                                        trySend(ScanResult(Division(value)))
                                    }
                                }
                                .addOnFailureListener {
                                    // This failure will happen if the barcode scanning model
                                    // fails to download from Google Play Services
                                    Log.e(TAG, it.message.orEmpty())
                                }.addOnCompleteListener {
                                    // When the image is from CameraX analysis use case, must
                                    // call image.close() on received images when finished
                                    // using them. Otherwise, new images may not be received
                                    // or the camera may stall.
                                    imageProxy.image?.close()
                                    imageProxy.close()
                                }
                        }
                    }

                    try {
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            previewUseCase,
                            analysisUseCase
                        )
                    } catch (illegalStateException: IllegalStateException) {
                        // If the use case has already been bound to another lifecycle or method is not called on main thread.
                        Log.e(TAG, illegalStateException.message.orEmpty())
                    } catch (illegalArgumentException: IllegalArgumentException) {
                        // If the provided camera selector is unable to resolve a camera to be used for the given use cases.
                        Log.e(TAG, illegalArgumentException.message.orEmpty())
                    }
                },
                ContextCompat.getMainExecutor(context)
            )

            awaitClose {
                // Dismisses real time listener
                cameraProviderFuture.cancel(true)
            }
        }

        return flow
    }
}