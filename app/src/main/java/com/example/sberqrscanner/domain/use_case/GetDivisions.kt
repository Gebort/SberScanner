package com.example.sberqrscanner.domain.use_case

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.domain.repository.DivisionRepository
import kotlinx.coroutines.flow.Flow

class GetDivisions(
    private val repository: DivisionRepository
) {

    operator fun invoke(): Flow<Reaction<List<Division>>> {
        return repository.getDivisions()
    }

}