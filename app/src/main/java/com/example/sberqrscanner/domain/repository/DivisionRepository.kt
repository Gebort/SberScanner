package com.example.sberqrscanner.domain.repository

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.model.Division
import kotlinx.coroutines.flow.Flow

interface DivisionRepository {

    fun getDivisions(): Flow<Reaction<List<Division>>>

    suspend fun insertDivision(division: Division): Reaction<Unit>

    suspend fun deleteDivision(division: Division): Reaction<Unit>

}