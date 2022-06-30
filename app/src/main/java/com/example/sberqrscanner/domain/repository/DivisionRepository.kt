package com.example.sberqrscanner.domain.repository

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.City
import com.example.sberqrscanner.domain.login.CityOptions
import com.example.sberqrscanner.domain.login.AddressOption
import com.example.sberqrscanner.domain.login.Profile
import com.example.sberqrscanner.domain.model.Division
import kotlinx.coroutines.flow.Flow

interface DivisionRepository {

    fun getDivisions(): Flow<Reaction<List<Division>>>

    suspend fun insertDivision(name: String, id: String? = null): Reaction<Unit>

    suspend fun updateDivision(division: Division): Reaction<Unit>

    suspend fun deleteDivision(division: Division): Reaction<Unit>

    suspend fun dropChecks(divisions: List<Division>): Reaction<Unit>

    fun getCityOptions(): Flow<Reaction<CityOptions>>

    fun getAddressOptions(city: City): Flow<Reaction<AddressOption>>

    suspend fun validateAddress(profile: Profile): Reaction<Unit>

    fun exitProfile()

}