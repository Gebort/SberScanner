package com.example.sberqrscanner.presentation.scanner

import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.domain.scanner.ScanResult
import com.example.sberqrscanner.presentation.scanner.adapter.DivisionItem

sealed class DivisionCheckEvent {
    class CheckDivision(val scanResult: ScanResult): DivisionCheckEvent()
}