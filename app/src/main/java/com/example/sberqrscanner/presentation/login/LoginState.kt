package com.example.sberqrscanner.presentation.login

import com.example.sberqrscanner.domain.login.Address
import com.example.sberqrscanner.domain.login.AddressOption
import com.example.sberqrscanner.domain.login.City
import com.example.sberqrscanner.domain.login.CityOptions

data class LoginState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val canBeValidated: Boolean = false,
    val cityOptions: CityOptions = CityOptions(listOf()),
    val addressOption: AddressOption? = null
) {
}