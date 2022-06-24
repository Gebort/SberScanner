package com.example.sberqrscanner.domain.repository

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.model.Division
import kotlinx.coroutines.flow.Flow

interface DivisionRepository {

    fun getDivisions(): Flow<Reaction<List<Division>>>

    suspend fun insertDivision(name: String, id: String? = null): Reaction<Unit>

    suspend fun updateDivision(division: Division): Reaction<Unit>

    suspend fun deleteDivision(division: Division): Reaction<Unit>

}