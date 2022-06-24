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

private const val DIVISIONS = "divisions"
private const val NAME = "name"

class FirestoreDivisionRep: DivisionRepository {

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
                        val list = value?.documents?.
                        filterNot {
                            it.getString("name") == null
                        }?.
                        map{ docSnap ->
                            val id = docSnap.id
                            val name = docSnap.getString("name")
                            Division(
                                id = id,
                                name = name!!
                            )
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

    override suspend fun insertDivision(name: String, id: String?): Reaction<Unit> {
        val db = Firebase.firestore
        return try {
            if (id == null){
                db.collection(DIVISIONS)
                    .add(hashMapOf(Pair(NAME, name)))
                    .await()
            }
            else {
                db.collection(DIVISIONS)
                    .document(id)
                    .set(hashMapOf(Pair(NAME, name)))
                    .await()
            }

            Reaction.Success(Unit)
        } catch (e: Exception){
            Reaction.Error(e)
        }
    }

    override suspend fun updateDivision(division: Division): Reaction<Unit> {
        val db = Firebase.firestore
        return try {
            db.collection(DIVISIONS)
                .document(division.id)
                .set(hashMapOf(Pair(NAME, division.name)))
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
                .document(division.id)
                .delete()
                .await()
            Reaction.Success(Unit)
        } catch (e: Exception){
            Reaction.Error(e)
        }

    }
}

