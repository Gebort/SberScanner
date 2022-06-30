package com.example.sberqrscanner.domain.use_case

import android.content.Context
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.Profile
import com.example.sberqrscanner.domain.profileStorage.ProfileStorage
import kotlinx.coroutines.flow.Flow

class GetProfileStorage(
    private val storage: ProfileStorage
) {

    operator fun invoke(): Flow<Reaction<Profile>> {
        return storage.getProfile()
    }

}