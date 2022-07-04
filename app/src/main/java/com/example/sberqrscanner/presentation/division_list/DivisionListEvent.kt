package com.example.sberqrscanner.presentation.division_list

import com.example.sberqrscanner.presentation.MainActivity

sealed class DivisionListEvent{
    class InsertDivision(val name: String, val id: String? = null): DivisionListEvent()
    class DeleteAddress(val activity: MainActivity): DivisionListEvent()
}
