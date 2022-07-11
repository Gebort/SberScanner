package com.example.sberqrscanner.domain.repository

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.*
import com.example.sberqrscanner.domain.model.Division
import kotlinx.coroutines.flow.Flow

interface DivisionRepository {

    fun getDivisions(): Flow<Reaction<List<Division>>>

    suspend fun insertDivision(name: String, division: Division?): Reaction<Unit>

    suspend fun updateDivision(division: Division): Reaction<Division>

    suspend fun deleteDivision(division: Division): Reaction<Unit>

    suspend fun dropChecks(divisions: List<Division>): Reaction<Unit>

    fun getCityOptions(): Flow<Reaction<CityOptions>>

    fun getAddressOptions(city: City): Flow<Reaction<AddressOption>>

    suspend fun validateAddress(profile: Profile): Reaction<Unit>

    suspend fun insertAddress(city: City, address: String, id: String? = null): Reaction<Unit>

    suspend fun insertCity(city: String, address: String, id: String? = null): Reaction<City>

    suspend fun deleteAddress(address: Address): Reaction<Unit>

    suspend fun deleteCity(city: City): Reaction<Unit>

    fun getProfile(): Profile

    fun exitProfile()

}