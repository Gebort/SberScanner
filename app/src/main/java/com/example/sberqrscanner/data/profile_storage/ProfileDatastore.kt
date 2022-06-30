package com.example.sberqrscanner.data.profile_storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.sberqrscanner.data.util.Reaction
import com.example.sberqrscanner.domain.login.Address
import com.example.sberqrscanner.domain.login.City
import com.example.sberqrscanner.domain.login.Profile
import com.example.sberqrscanner.domain.profileStorage.ProfileStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val STORE_NAME = "user_profile"

class ProfileDatastore(
    private val context: Context
): ProfileStorage {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = STORE_NAME)

    override fun getProfile(): Flow<Reaction<Profile>> = context.dataStore.data
        .catch { e ->
            Reaction.Error(Exception(e.message))
        }
        .map { preferences ->
            val cityStr = preferences[PreferencesKeys.CITY]
            val addressStr = preferences[PreferencesKeys.ADDRESS]
            val cityId = preferences[PreferencesKeys.CITY_ID]
            val addressId = preferences[PreferencesKeys.ADDRESS_ID]
            if (
                cityStr == null || cityStr == "" ||
                addressStr == null || addressStr == "" ||
                cityId == null || cityId == "" ||
                addressId == null || addressId == ""
            ){
                Reaction.Error(Exception("Cant read the data"))
            }
            else {
                Reaction.Success(Profile(
                    City(cityId, cityStr),
                    Address(addressId, addressStr)
                ))

            }
        }

    override suspend fun updateProfile(profile: Profile?): Reaction<Unit> {
        val prof = profile ?:
        Profile(
            City("", ""),
            Address("", "")
        )
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CITY] = prof.city.name
            preferences[PreferencesKeys.CITY_ID] = prof.city.id
            preferences[PreferencesKeys.ADDRESS] = prof.address.name
            preferences[PreferencesKeys.ADDRESS_ID] = prof.address.id
        }
        return Reaction.Success(Unit)
    }

    private object PreferencesKeys {
        val CITY = stringPreferencesKey("city")
        val CITY_ID = stringPreferencesKey("city_id")
        val ADDRESS = stringPreferencesKey("address")
        val ADDRESS_ID = stringPreferencesKey("address_id")

    }
}