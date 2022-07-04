package com.example.sberqrscanner.domain.use_case

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.City
import com.example.sberqrscanner.domain.repository.DivisionRepository

class InsertAddress(
    private val repository: DivisionRepository
) {

    suspend operator fun invoke(city: City, address: String, id: String? = null): Reaction<Unit> {
        return repository.insertAddress(city, address, id)
    }

}