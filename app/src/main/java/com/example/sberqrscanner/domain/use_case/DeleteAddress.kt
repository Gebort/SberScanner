package com.example.sberqrscanner.domain.use_case

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.Address
import com.example.sberqrscanner.domain.repository.DivisionRepository

class DeleteAddress(
    private val repository: DivisionRepository
) {

    suspend operator fun invoke(address: Address): Reaction<Unit> {
        return repository.deleteAddress(address)
    }

}