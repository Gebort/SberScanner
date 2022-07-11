package com.example.sberqrscanner.domain.use_case

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.domain.repository.DivisionRepository

class UpdateDivision(
    private val repository: DivisionRepository
) {
    suspend operator fun invoke(division: Division): Reaction<Division> {
        return repository.updateDivision(division)
    }
}