package com.example.sberqrscanner.data.repository

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.data.util.await
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.domain.repository.DivisionRepository
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.lang.Exception

private const val DIVISIONS = "divisions"
private const val NAME = "name"
private const val CHECKED = "checked"

class FirestoreDivisionRep: DivisionRepository {

    private var getDivisionsFlow: Flow<Reaction<List<Division>>>? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getDivisions(): Flow<Reaction<List<Division>>> {
        getDivisionsFlow?.let {
            return it
        }
        getDivisionsFlow = callbackFlow {
            val db = Firebase.firestore
            val subscription = db.collection(DIVISIONS).addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(Reaction.Error(error))
                } else {
                    val list = value?.documents?.filterNot {
                        it.getString(NAME) == null || it.getBoolean(CHECKED) == null
                    }?.map { docSnap ->
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
        }.also {
            return it
        }
    }

    override suspend fun insertDivision(name: String, id: String?): Reaction<Unit> {
        val db = Firebase.firestore
        return try {
            if (id == null) {
                db.collection(DIVISIONS)
                    .add(hashMapOf(Pair(NAME, name)))
                    .await()
            } else {
                db.collection(DIVISIONS)
                    .document(id)
                    .set(hashMapOf(Pair(NAME, name)))
                    .await()
            }

            Reaction.Success(Unit)
        } catch (e: Exception) {
            Reaction.Error(e)
        }
    }

    override suspend fun updateDivision(division: Division): Reaction<Unit> {
        val db = Firebase.firestore
        return try {
            db.collection(DIVISIONS)
                .document(division.id)
                .set(
                    hashMapOf(
                        NAME to division.name,
                        CHECKED to division.checked
                    )
                )
                .await()
            Reaction.Success(Unit)
        } catch (e: Exception) {
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
        } catch (e: Exception) {
            Reaction.Error(e)
        }

    }

    override suspend fun dropChecks(divisions: List<Division>): Reaction<Unit> {
        return try {
            val db = Firebase.firestore
            val docs = divisions
                .map {
                    db.collection(DIVISIONS).document(it.id)
                }
            db.runBatch { batch ->
                for (doc in docs) {
                    batch.update(doc, CHECKED, false)
                }
            }.await()
            Reaction.Success(Unit)

        } catch (e: Exception) {
            Reaction.Error(e)
        }
    }
}



