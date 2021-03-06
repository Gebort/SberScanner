package com.example.sberqrscanner.presentation.login

import com.example.sberqrscanner.domain.login.Address
import com.example.sberqrscanner.domain.login.City
import com.example.sberqrscanner.domain.login.Profile

sealed class LoginEvent {
    object TryLogin: LoginEvent()
    class CityChanged(val city: String): LoginEvent()
    class AddressChanged(val address: Address?): LoginEvent()
    class NewAddress(val city: String, val address: String): LoginEvent()
}
