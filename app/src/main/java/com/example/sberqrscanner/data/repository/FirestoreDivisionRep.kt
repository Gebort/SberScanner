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
private const val NUMBER = "number"
private const val FLOOR = "floor"
private const val WING = "wing"
private const val PHONE = "phone"
private const val FIO = "fio"

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
                                it.getString(NAME) == null ||
                                it.getBoolean(CHECKED) == null ||
                                it.getLong(NUMBER) == null ||
                                it.getLong(FLOOR) == null ||
                                it.getString(WING) == null ||
                                it.getString(PHONE) == null ||
                                it.getString(FIO) == null
                    }?.map { docSnap ->
                        val id = docSnap.id
                        val name = docSnap.getString(NAME)
                        val checked = docSnap.getBoolean(CHECKED)
                        val number = docSnap.getLong(NUMBER)
                        val floor = docSnap.getLong(FLOOR)
                        val wing = docSnap.getString(WING)
                        val phone = docSnap.getString(PHONE)
                        val fio = docSnap.getString(FIO)

                        Division(
                            id = id,
                            name = name!!,
                            checked = checked!!,
                            number = number!!.toInt(),
                            floor = floor!!.toInt(),
                            wing = wing!!,
                            phone = phone!!,
                            fio = fio!!
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

    override suspend fun insertDivision(name: String, division: Division?): Reaction<Unit> {
        if (profile == null) {
            throw Exception("Unauthorized access")
        }
        val db = Firebase.firestore
        return try {
            if (division == null) {
                db.collection(CITIES)
                    .document(profile!!.city.id)
                    .collection(ADDRESSES)
                    .document(profile!!.address.id)
                    .collection(DIVISIONS)
                    .add(getDefaultMap(name))
                    .await()
            } else {
                db.collection(CITIES)
                    .document(profile!!.city.id)
                    .collection(ADDRESSES)
                    .document(profile!!.address.id)
                    .collection(DIVISIONS)
                    .document(division.id)
                    .set(getHashMap(division))
                    .await()
            }

            Reaction.Success(Unit)
        } catch (e: Exception) {
            Reaction.Error(e)
        }
    }

    override suspend fun updateDivision(division: Division): Reaction<Division> {
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
                .set(getHashMap(division))
                .await()
            Reaction.Success(division)
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

    override suspend fun insertAddress(city: City, address: String, id: String?): Reaction<Unit> {
        val db = Firebase.firestore
        return try {
            if (id == null) {
                db.collection(CITIES)
                    .document(city.id)
                    .collection(ADDRESSES)
                    .add(hashMapOf(
                        ADDRESS to address,
                    ))
                    .await()
            } else {
                db.collection(CITIES)
                    .document(city.id)
                    .collection(ADDRESSES)
                    .document(id)
                    .set(hashMapOf(ADDRESS to address))
                    .await()
            }
            Reaction.Success(Unit)
        } catch (e: Exception) {
            Reaction.Error(e)
        }
    }

    override suspend fun insertCity(city: String, address: String, id: String?): Reaction<City> {
        return try {
            val db = Firebase.firestore
            val cityRef =
                if (id == null) db.collection(CITIES).document()
                else db.collection(CITIES).document(id)

            cityRef
                .set(hashMapOf(NAME to city))
                .continueWith{
                    cityRef.collection(ADDRESSES)
                        .add(hashMapOf(ADDRESS to address))
                }
                .await()
            Reaction.Success(
                City(id = cityRef.id, name = city)
            )

        } catch (e: Exception) {
            Reaction.Error(e)
        }
    }

    override suspend fun deleteAddress(address: Address): Reaction<Unit> {
        if (profile == null) {
            throw Exception("Unauthorized access")
        }
        val db = Firebase.firestore
        return try {
            val cityDoc = db.collection(CITIES)
                .document(profile!!.city.id)
                .collection(ADDRESSES)
                .get()
            cityDoc.await()
            if (cityDoc.result.size() <= 1) {
                db.collection(CITIES)
                    .document(profile!!.city.id)
                    .collection(ADDRESSES)
                    .document(profile!!.address.id)
                    .delete()
                    .continueWith {
                        db.collection(CITIES)
                            .document(profile!!.city.id)
                            .delete()
                    }
                    .await()
            }
            else {
                db.collection(CITIES)
                    .document(profile!!.city.id)
                    .collection(ADDRESSES)
                    .document(profile!!.address.id)
                    .delete()
                    .await()
            }
            Reaction.Success(Unit)
        } catch (e: Exception) {
            Reaction.Error(e)
        }
    }

    override suspend fun deleteCity(city: City): Reaction<Unit> {
        if (profile == null) {
            throw Exception("Unauthorized access")
        }
        val db = Firebase.firestore
        return try {
            db.collection(CITIES)
                .document(profile!!.city.id)
                .delete()
                .await()
            Reaction.Success(Unit)
        } catch (e: Exception) {
            Reaction.Error(e)
        }
    }

    override fun getProfile(): Profile {
        if (profile == null){
            throw Exception("Unauthorized access")
        }
        return profile!!
    }

    override fun exitProfile() {
        profile = null
        getDivisionsFlow = null
    }

    private fun getDefaultMap(name: String):HashMap<String, Any> {
        return getHashMap(Division(
            id = "",
            name = name,
            floor = 0,
            checked = false,
            number = 0,
            wing = "-",
            phone = "-",
            fio = "-"
        ))
    }

    private fun getHashMap(division: Division): HashMap<String, Any> {
        return hashMapOf(
            NAME to division.name,
            CHECKED to division.checked,
            NUMBER to division.number,
            FLOOR to division.floor,
            WING to division.wing,
            PHONE to division.phone,
            FIO to division.fio
        )
    }
}



