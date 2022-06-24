package com.example.sberqrscanner.presentation.division_edit

import com.example.sberqrscanner.domain.model.Division

sealed class EditDivisionUiEvent {
    class Deleted(val division: Division): EditDivisionUiEvent()
    class Changed(val division: Division): EditDivisionUiEvent()
    class Error(val e: Exception): EditDivisionUiEvent()
}