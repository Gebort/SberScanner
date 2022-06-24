package com.example.sberqrscanner.presentation.division_list

sealed class DivisionListEvent{
    class InsertDivision(val name: String, val id: String? = null): DivisionListEvent()
}
