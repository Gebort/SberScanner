package com.example.sberqrscanner.domain.use_case

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.AddressOption
import com.example.sberqrscanner.domain.login.City
import com.example.sberqrscanner.domain.repository.DivisionRepository
import kotlinx.coroutines.flow.Flow

class GetAddressesOptions(
    private val repository: DivisionRepository
) {

    operator fun invoke(city: City): Flow<Reaction<AddressOption>> {
        return repository.getAddressOptions(city)
    }

}