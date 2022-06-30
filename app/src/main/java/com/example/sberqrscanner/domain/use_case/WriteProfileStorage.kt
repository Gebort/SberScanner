package com.example.sberqrscanner.domain.use_case

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.Profile
import com.example.sberqrscanner.domain.profileStorage.ProfileStorage

class WriteProfileStorage(
    private val storage: ProfileStorage
) {

    suspend operator fun invoke(profile: Profile?): Reaction<Unit> {
        return storage.updateProfile(profile)
    }

}