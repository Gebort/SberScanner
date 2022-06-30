package com.example.sberqrscanner.presentation.scanner

import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.domain.scanner.ScanResult
import com.example.sberqrscanner.presentation.MainActivity

sealed class DivisionCheckEvent {
    class CheckDivisions(val scans: List<ScanResult>): DivisionCheckEvent()
    class UncheckDivision(val division: Division): DivisionCheckEvent()
    object DropChecks: DivisionCheckEvent()
    class Logout(val activity: MainActivity) : DivisionCheckEvent()
}