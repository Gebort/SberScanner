package com.example.sberqrscanner.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.sberqrscanner.MyApp
import com.example.sberqrscanner.R

private const val CAMERA_PERMISSION_REQUEST_CODE = 1
//private const val STORAGE_PERMISSION_REQUEST_CODE = 2
//private const val REQUEST_PERMISSIONS_CODE = 3


class MainActivity : AppCompatActivity() {

    private val requestPermission = MyApp.instance!!.requestPermission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        )

        setContentView(R.layout.activity_main)

        requestPermission(
            Manifest.permission.CAMERA,
            CAMERA_PERMISSION_REQUEST_CODE,
            this
        )
  //      requestCameraPermission()
        //requestStoragePermission()
//        if (!hasCameraPermission())
//            requestCameraPermission()
//        if (!hasStoragePermission())
//            requestStoragePermission()
    }

//    private fun hasCameraPermission() =
//        ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED
//
//    private fun hasStoragePermission() =
//        ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED

//    private fun requestCameraPermission(){
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.CAMERA),
//            CAMERA_PERMISSION_REQUEST_CODE
//        )
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if ((requestCode == CAMERA_PERMISSION_REQUEST_CODE
//            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//        ) {
//        } else {
//            // user did not grant permissions - we can't use the camera
//            val toast = Toast.makeText(
//                this,
//                R.string.provide_permissions,
//                Toast.LENGTH_LONG)
//            toast.setGravity(Gravity.CENTER, 0, 0)
//            toast.show()
//
//
//        }
//    }
}