package com.example.sberqrscanner.presentation.scanner

import com.example.sberqrscanner.domain.model.Division
import java.lang.Exception

sealed class DivisionCheckUiEvent {
    class DivisionChecked(val division: Division): DivisionCheckUiEvent()
    class NetworkError(val e: Exception): DivisionCheckUiEvent()
    object Logout: DivisionCheckUiEvent()
}
