package com.example.sberqrscanner.presentation.login

import com.example.sberqrscanner.domain.login.Address

sealed class AddressItem {
    class RealAddress(val address: Address): AddressItem()
    object NewAddress: AddressItem()
}
