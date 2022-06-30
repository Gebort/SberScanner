package com.example.sberqrscanner.domain.use_case

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.Profile
import com.example.sberqrscanner.domain.repository.DivisionRepository

class ValidateProfile(
    private val repository: DivisionRepository
) {

    suspend operator fun invoke(profile: Profile): Reaction<Unit> {
        return repository.validateAddress(profile)
    }

}