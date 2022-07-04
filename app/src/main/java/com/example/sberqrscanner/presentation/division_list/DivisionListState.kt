package com.example.sberqrscanner.presentation.division_list

import com.example.sberqrscanner.domain.login.Profile
import com.example.sberqrscanner.domain.model.Division

data class DivisionListState(
    val divisions: List<Division> = listOf(),
    val loading: Boolean = true,
    val profile: Profile
) {
}