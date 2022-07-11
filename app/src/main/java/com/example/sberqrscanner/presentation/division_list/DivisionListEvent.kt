package com.example.sberqrscanner.presentation.division_list

import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.presentation.MainActivity

sealed class DivisionListEvent{
    class InsertDivision(val name: String, val division: Division? = null): DivisionListEvent()
    class DeleteAddress(val activity: MainActivity): DivisionListEvent()
}
