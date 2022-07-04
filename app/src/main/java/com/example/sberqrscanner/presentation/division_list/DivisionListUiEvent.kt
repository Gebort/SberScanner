package com.example.sberqrscanner.presentation.division_list

import com.example.sberqrscanner.domain.model.Division

sealed class DivisionListUiEvent {
    class DivisionDeleted(val division: Division): DivisionListUiEvent()
    class Error(val e: Exception): DivisionListUiEvent()
    object AddressDeleted: DivisionListUiEvent()
}
