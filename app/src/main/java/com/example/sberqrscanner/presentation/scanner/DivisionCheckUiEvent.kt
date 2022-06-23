package com.example.sberqrscanner.presentation.scanner

import com.example.sberqrscanner.domain.model.Division

sealed class DivisionCheckUiEvent {
    class DivisionChecked(val division: Division): DivisionCheckUiEvent()
}
