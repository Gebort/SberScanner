package com.example.sberqrscanner.domain.use_case

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.City
import com.example.sberqrscanner.domain.repository.DivisionRepository

class InsertCity(
    private val repository: DivisionRepository
) {

    suspend operator fun invoke(city: String,  address: String, id: String? = null): Reaction<City> {
        return repository.insertCity(city, address, id)
    }

}