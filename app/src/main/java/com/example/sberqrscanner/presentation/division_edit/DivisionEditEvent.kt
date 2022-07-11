package com.example.sberqrscanner.presentation.division_edit

import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.presentation.division_list.DivisionListEvent

sealed class DivisionEditEvent{
    class Select(val division: Division?): DivisionEditEvent()
    object DeleteSelected: DivisionEditEvent()
    class UpdateDivision(val division: Division): DivisionEditEvent()
}
