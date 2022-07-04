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

    private lateinit var profileStorage: ProfileDatastore
    private lateinit var writeProfileStorage: WriteProfileStorage
    lateinit var getProfileStorage: GetProfileStorage

    val getDivisions = GetDivisions(divisionsRep)
    val insertDivision = InsertDivision(divisionsRep)
    val deleteDivision = DeleteDivision(divisionsRep)
    val updateDivision = UpdateDivision(divisionsRep)
    val dropChecks = DropChecks(divisionsRep)
    val getCityOptions = GetCityOptions(divisionsRep)
    val getAddressesOptions = GetAddressesOptions(divisionsRep)
    lateinit var validateProfile: ValidateProfile
    lateinit var exitProfile: ExitProfile
    val getProfile = GetProfile(divisionsRep)
    val insertCity = InsertCity(divisionsRep)
    val insertAddress = InsertAddress(divisionsRep)
    val deleteAddress = DeleteAddress(divisionsRep)
    val deleteCity = DeleteCity(divisionsRep)

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

    val createSnackbar = CreateSnackbar()


    override fun onCreate() {
        super.onCreate()
        instance = this
        profileStorage = ProfileDatastore(this.applicationContext)
        writeProfileStorage = WriteProfileStorage(profileStorage)
        getProfileStorage = GetProfileStorage(profileStorage)
        validateProfile = ValidateProfile(divisionsRep, writeProfileStorage)
        exitProfile = ExitProfile(divisionsRep, writeProfileStorage)
    }

    companion object {
        var instance: MyApp? = null
    }
}