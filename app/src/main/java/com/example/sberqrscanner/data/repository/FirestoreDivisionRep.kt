package com.example.sberqrscanner.data.repository

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.data.util.await
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.domain.repository.DivisionRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception

class FirestoreDivisionRep: DivisionRepository {

    private val DIVISIONS = "divisions"

    private var getDivisionsFlow: Flow<Reaction<List<Division>>>? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getDivisions(): Flow<Reaction<List<Division>>> {
        if (getDivisionsFlow != null){
            return getDivisionsFlow as Flow<Reaction<List<Division>>>
        }
        else {
            getDivisionsFlow = callbackFlow {
                val db = Firebase.firestore
                val subscription = db.collection(DIVISIONS).addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(Reaction.Error(error))
                    } else {
                        val list = value?.documents?.map { docSnap ->
                            Division(docSnap.id)
                        } ?: listOf()
                        trySend(Reaction.Success(list))
                    }

                }

                awaitClose {
                    // Dismisses real time listener
                    subscription.remove()
                }
            }
            return getDivisionsFlow as Flow<Reaction<List<Division>>>
        }
    }

    override suspend fun insertDivision(division: Division): Reaction<Unit> {
        val db = Firebase.firestore
        return try {
            db.collection(DIVISIONS)
                .document(division.name)
                .set(hashMapOf<Nothing, Nothing>())
                .await()
            Reaction.Success(Unit)
        } catch (e: Exception){
            Reaction.Error(e)
        }
    }

    override suspend fun deleteDivision(division: Division): Reaction<Unit> {
        val db = Firebase.firestore
        return try {
            db.collection(DIVISIONS)
                .document(division.name)
                .delete()
                .await()
            Reaction.Success(Unit)
        } catch (e: Exception){
            Reaction.Error(e)
        }

    }
}

