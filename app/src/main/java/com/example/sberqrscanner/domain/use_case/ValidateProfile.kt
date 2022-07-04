package com.example.sberqrscanner.domain.use_case

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.Profile
import com.example.sberqrscanner.domain.repository.DivisionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.coroutineContext

class ValidateProfile(
    private val repository: DivisionRepository,
    private val writeProfileStorage: WriteProfileStorage
) {

    suspend operator fun invoke(profile: Profile): Reaction<Unit> {
        val reaction = repository.validateAddress(profile)
        if (reaction is Reaction.Success){
            coroutineScope {
                async {
                    writeProfileStorage(profile)
                }
            }
        }
        return reaction
    }

}