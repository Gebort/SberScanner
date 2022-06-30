package com.example.sberqrscanner

import android.app.Application
import com.example.sberqrscanner.data.code_generator.QRCodeGenerator
import com.example.sberqrscanner.data.external_files.ExternalWorkerImpl
import com.example.sberqrscanner.data.profile_storage.ProfileDatastore
import com.example.sberqrscanner.data.repository.FirestoreDivisionRep
import com.example.sberqrscanner.data.scanner.ScannerXCamera
import com.example.sberqrscanner.domain.use_case.*

class MyApp: Application() {

    private val divisionsRep = FirestoreDivisionRep()
    val getDivisions = GetDivisions(divisionsRep)
    val insertDivision = InsertDivision(divisionsRep)
    val deleteDivision = DeleteDivision(divisionsRep)
    val updateDivision = UpdateDivision(divisionsRep)
    val dropChecks = DropChecks(divisionsRep)
    val getCityOptions = GetCityOptions(divisionsRep)
    val getAddressesOptions = GetAddressesOptions(divisionsRep)
    val validateProfile = ValidateProfile(divisionsRep)
    val exitProfile = ExitProfile(divisionsRep)

    val scanner = ScannerXCamera()
    val bindCamera = BindCamera(scanner)

    private val codeGenerator = QRCodeGenerator()
    val generateQRCode = GenerateQRCode(codeGenerator)
    val generateCode128 = GenerateCode128(codeGenerator)

    private val externalStorageWorker = ExternalWorkerImpl()
    val exportCode = ExportCode(externalStorageWorker)
    val shareCode = ShareCode(externalStorageWorker)
    val sharePdf = SharePdf(externalStorageWorker)

    val generateReport = GenerateReport()

    val checkPermission = CheckPermission()
    val checkRequestPerm = CheckRequestPerm(checkPermission)

    private lateinit var profileStorage: ProfileDatastore
    lateinit var writeProfileStorage: WriteProfileStorage
    lateinit var getProfileStorage: GetProfileStorage

    override fun onCreate() {
        super.onCreate()
        instance = this
        profileStorage = ProfileDatastore(this.applicationContext)
        writeProfileStorage = WriteProfileStorage(profileStorage)
        getProfileStorage = GetProfileStorage(profileStorage)
    }

    companion object {
        var instance: MyApp? = null
    }
}