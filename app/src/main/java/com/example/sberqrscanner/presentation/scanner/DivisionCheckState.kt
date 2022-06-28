package com.example.sberqrscanner.presentation.scanner

import com.example.sberqrscanner.domain.model.Division

data class DivisionCheckState(
    val divisions: List<Division> = listOf()
) {
}