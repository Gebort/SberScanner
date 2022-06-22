package com.example.sberqrscanner.presentation.division_list

import com.example.sberqrscanner.domain.model.Division

sealed class DivisionListEvent{
    class InsertDivision(val division: Division): DivisionListEvent()
    class DeleteDivision(val division: Division): DivisionListEvent()
}
