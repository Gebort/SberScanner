package com.example.sberqrscanner.data.repository

import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.data.util.await
import com.example.sberqrscanner.domain.login.*
import com.example.sberqrscanner.domain.model.Division
import com.example.sberqrscanner.domain.repository.DivisionRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.lang.Exception

private const val CITIES = "cities"
private const val ADDRESSES = "addresses"
private const val ADDRESS = "address"
private const val DIVISIONS = "divisions"
private const val NAME = "name"
private const val CHECKED = "checked"

class FirestoreDivisionRep: DivisionRepository {

    private var getDivisionsFlow: Flow<Reaction<List<Division>>>? = null
    private var getCityFlow: Flow<Reaction<CityOptions>>? = null
    private var getAddressFlow: Flow<Reaction<AddressOption>>? = null

    private var profile: Profile? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getDivisions(): Flow<Reaction<List<Division>>> {
        if (profile == null) {
            throw Exception("Unauthorized access")
        }
        getDivisionsFlow?.let {
            return it
        }
        getDivisionsFlow = callbackFlow {
            val db = Firebase.firestore
            val subscription = db.collection(CITIES)
                .document(profile!!.city.id)
                .collection(ADDRESSES)
                .document(profile!!.address.id)
                .collection(DIVISIONS).addSnapshotListener { value, error ->
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
        if (profile == null) {
            throw Exception("Unauthorized access")
        }
        val db = Firebase.firestore
        return try {
            if (id == null) {
                db.collection(CITIES)
                    .document(profile!!.city.id)
                    .collection(ADDRESSES)
                    .document(profile!!.address.id)
                    .collection(DIVISIONS)
                    .add(hashMapOf(
                        NAME to name,
                        CHECKED to false
                    ))
                    .await()
            } else {
                db.collection(CITIES)
                    .document(profile!!.city.id)
                    .collection(ADDRESSES)
                    .document(profile!!.address.id)
                    .collection(DIVISIONS)
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
        if (profile == null) {
            throw Exception("Unauthorized access")
        }
        val db = Firebase.firestore
        return try {
            db.collection(CITIES)
                .document(profile!!.city.id)
                .collection(ADDRESSES)
                .document(profile!!.address.id)
                .collection(DIVISIONS)
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
        if (profile == null) {
            throw Exception("Unauthorized access")
        }
        val db = Firebase.firestore
        return try {
            db.collection(CITIES)
                .document(profile!!.city.id)
                .collection(ADDRESSES)
                .document(profile!!.address.id)
                .collection(DIVISIONS)
                .document(division.id)
                .delete()
                .await()
            Reaction.Success(Unit)
        } catch (e: Exception) {
            Reaction.Error(e)
        }

    }

    override suspend fun dropChecks(divisions: List<Division>): Reaction<Unit> {
        if (profile == null) {
            throw Exception("Unauthorized access")
        }
        return try {
            val db = Firebase.firestore
            val docs = divisions
                .map {
                    db.collection(CITIES)
                        .document(profile!!.city.id)
                        .collection(ADDRESSES)
                        .document(profile!!.address.id)
                        .collection(DIVISIONS)
                        .document(it.id)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCityOptions(): Flow<Reaction<CityOptions>> {
        getCityFlow?.let {
            return it
        }
        getCityFlow = callbackFlow {
            val db = Firebase.firestore
            val subscription = db.collection(CITIES).addSnapshotListener { value, error ->
                if (error != null) {
                    trySend(Reaction.Error(error))
                } else {
                    val list = value?.documents?.filterNot {
                        it.getString(NAME) == null
                    }?.map { docSnap ->
                        val id = docSnap.id
                        val name = docSnap.getString(NAME)
                        City(
                            id = id,
                            name = name!!
                        )
                    } ?: listOf()
                    trySend(Reaction.Success(CityOptions(list)))
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAddressOptions(city: City): Flow<Reaction<AddressOption>> {
        getAddressFlow?.let {
            return it
        }
        getAddressFlow = callbackFlow {
            val db = Firebase.firestore
            val subscription = db.collection(CITIES)
                .document(city.id)
                .collection(ADDRESSES)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        trySend(Reaction.Error(error))
                    } else {
                        val list = value?.documents?.filterNot {
                            it.getString(ADDRESS) == null
                        }?.map { docSnap ->
                            val id = docSnap.id
                            val name = docSnap.getString(ADDRESS)
                            Address(
                                id = id,
                                name = name!!
                            )
                        } ?: listOf()
                        trySend(Reaction.Success(AddressOption(city, list)))
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

    override suspend fun validateAddress(profile: Profile): Reaction<Unit> {
        val db = Firebase.firestore
        return try {
            val doc = db.collection(CITIES)
                .document(profile.city.id)
                .collection(ADDRESSES)
                .document(profile.address.id)
                .get()
                .await()
            if (doc.getString(ADDRESS) != null) {
                this.profile = profile
                Reaction.Success(Unit)
            } else {
                Reaction.Error(InvalidLoginException())
            }
        } catch (e: Exception) {
            Reaction.Error(e)
        }
    }

    override fun exitProfile() {
        profile = null
        getDivisionsFlow = null
    }
}



