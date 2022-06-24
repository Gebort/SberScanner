package com.example.sberqrscanner

import android.app.Application
import com.example.sberqrscanner.data.code_generator.QRCodeGenerator
import com.example.sberqrscanner.data.repository.FirestoreDivisionRep
import com.example.sberqrscanner.data.scanner.ScannerXCamera
import com.example.sberqrscanner.domain.use_case.*

class MyApp: Application() {

    val divisionsRep = FirestoreDivisionRep()
    val getDivisions = GetDivisions(divisionsRep)
    val insertDivision = InsertDivision(divisionsRep)
    val deleteDivision = DeleteDivision(divisionsRep)
    val updateDivision = UpdateDivision(divisionsRep)
    val scanner = ScannerXCamera()
    val bindCamera = BindCamera(scanner)
    val codeGenerator = QRCodeGenerator()
    val generateCode = GenerateCode(codeGenerator)
    val saveBitmap = SaveBitmap()
    val exportCode = ExportCode(saveBitmap)
    val shareCode = ShareCode(saveBitmap)
    val checkPermission = CheckPermission()
    val requestPermission = RequestPermission(checkPermission)

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: MyApp? = null
    }
}