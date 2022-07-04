package com.example.sberqrscanner.domain.use_case

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.City
import com.example.sberqrscanner.domain.repository.DivisionRepository

class DeleteCity(
    private val repository: DivisionRepository
) {

    suspend operator fun invoke(city: City): Reaction<Unit> {
        return repository.deleteCity(city)
    }

}