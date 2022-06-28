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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception

private const val DIVISIONS = "divisions"
private const val NAME = "name"
private const val CHECKED = "checked"

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
                            it.getString(NAME) == null || it.getBoolean(CHECKED) == null
                        }?.
                        map{ docSnap ->
                            val id = docSnap.id
                            val name = docSnap.getString(NAME)
                            val checked = docSnap.getBoolean(CHECKED)
                            Division(
                                id = id,
                                name = name!!,
                                checked = checked!!
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
                .set(hashMapOf(
                    NAME to division.name,
                    CHECKED to division.checked
                ))
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

    override suspend fun dropChecks(): Reaction<Unit> {
//        val db = Firebase.firestore
//        return try {
//
//
//
//            val nycRef = db.collection("cities").document("NYC")
//            val sfRef = db.collection("cities").document("SF")
//            val laRef = db.collection("cities").document("LA")
//
//// Get a new write batch and commit all write operations
//            db.runBatch { batch ->
//                // Set the value of 'NYC'
//                batch.set(nycRef, City())
//
//                // Update the population of 'SF'
//                batch.update(sfRef, "population", 1000000L)
//
//                // Delete the city 'LA'
//                batch.delete(laRef)
//            }.addOnCompleteListener {
//                // ...
//            }
//            db.collection(DIVISIONS)
//                .document(division.id)
//                .delete()
//                .await()
//            Reaction.Success(Unit)
//        } catch (e: Exception){
//            Reaction.Error(e)
//        }
    }
}



