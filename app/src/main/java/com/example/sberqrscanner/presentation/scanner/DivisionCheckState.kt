package com.example.sberqrscanner.presentation.scanner

import com.example.sberqrscanner.presentation.scanner.adapter.DivisionItem

data class DivisionCheckState(
    val divisions: List<DivisionItem> = listOf(),
    val checkedCount: Int = 0
) {
}