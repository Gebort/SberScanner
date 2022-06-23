package com.example.sberqrscanner

import android.app.Application
import com.example.sberqrscanner.data.repository.FirestoreDivisionRep
import com.example.sberqrscanner.data.scanner.ScannerXCamera
import com.example.sberqrscanner.domain.use_case.*

class MyApp: Application() {

    val divisionsRep = FirestoreDivisionRep()
    val getDivisions = GetDivisions(divisionsRep)
    val insertDivision = InsertDivision(divisionsRep)
    val deleteDivision = DeleteDivision(divisionsRep)
    val divisionUseCases = DivisionsUseCases(
        deleteDivision,
        insertDivision,
        getDivisions
    )
    val scanner = ScannerXCamera()
    val bindCamera = BindCamera(scanner)

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: MyApp? = null
    }
}