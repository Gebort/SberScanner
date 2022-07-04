package com.example.sberqrscanner.domain.use_case

import com.example.sberqrscanner.domain.repository.DivisionRepository

class GetProfile(
    private val repository: DivisionRepository
) {

    operator fun invoke() = repository.getProfile()

}