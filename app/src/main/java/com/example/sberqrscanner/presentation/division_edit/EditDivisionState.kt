package com.example.sberqrscanner.presentation.division_edit

import com.example.sberqrscanner.domain.model.Division

data class EditDivisionState(
    val selected: Division? = null,
    val loading: Boolean = false
) {
}