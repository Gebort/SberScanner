package com.example.sberqrscanner.domain.profileStorage

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileStorage {

    fun getProfile(): Flow<Reaction<Profile>>

    suspend fun updateProfile(profile: Profile?): Reaction<Unit>

}