package com.example.sberqrscanner

import android.app.Application
import com.example.sberqrscanner.data.code_generator.QRCodeGenerator
import com.example.sberqrscanner.data.external_files.ExternalWorkerImpl
import com.example.sberqrscanner.data.repository.FirestoreDivisionRep
import com.example.sberqrscanner.data.scanner.ScannerXCamera
import com.example.sberqrscanner.domain.use_case.*

class MyApp: Application() {

    private val divisionsRep = FirestoreDivisionRep()
    val getDivisions = GetDivisions(divisionsRep)
    val insertDivision = InsertDivision(divisionsRep)
    val deleteDivision = DeleteDivision(divisionsRep)
    val updateDivision = UpdateDivision(divisionsRep)
    val scanner = ScannerXCamera()
    val bindCamera = BindCamera(scanner)
    private val codeGenerator = QRCodeGenerator()
    val generateCode = GenerateCode(codeGenerator)
    private val externalStorageWorker = ExternalWorkerImpl()
    val exportCode = ExportCode(externalStorageWorker)
    val shareCode = ShareCode(externalStorageWorker)
    val sharePdf = SharePdf(externalStorageWorker)
    val generateReport = GenerateReport()
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