package com.example.sberqrscanner.domain.use_case

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.CityOptions
import com.example.sberqrscanner.domain.repository.DivisionRepository
import kotlinx.coroutines.flow.Flow

class GetCityOptions(
    private val repository: DivisionRepository
) {

    operator fun invoke(): Flow<Reaction<CityOptions>> {
        return repository.getCityOptions()
    }

}