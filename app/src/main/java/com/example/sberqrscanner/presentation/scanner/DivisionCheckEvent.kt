package com.example.sberqrscanner.presentation.scanner

import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.domain.scanner.ScanResult

sealed class DivisionCheckEvent {
    class CheckDivisions(val scans: List<ScanResult>): DivisionCheckEvent()
    class UncheckDivision(val division: Division): DivisionCheckEvent()
}