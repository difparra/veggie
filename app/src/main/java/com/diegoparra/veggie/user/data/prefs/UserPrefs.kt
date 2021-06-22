package com.diegoparra.veggie.user.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private fun getAddressKeyForUser(userId: String) =
        stringPreferencesKey("sel_address_$userId")


    suspend fun setSelectedAddress(userId: String, addressId: String) {
        context.dataStore.edit { prefs ->
            prefs[getAddressKeyForUser(userId)] = addressId
        }
    }

    suspend fun getSelectedAddress(userId: String): String? {
        return context.dataStore.data
            .map { prefs ->
                prefs[getAddressKeyForUser(userId)]
            }.first()
    }

    /**
     * If the selectedAddress corresponds to the addressToDelete, address will be deleted as selected.
     */
    suspend fun deleteAddressAsSelectedIfApplicable(userId: String, addressId: String) {
        context.dataStore.edit { prefs ->
            val key = getAddressKeyForUser(userId)
            if (prefs[key] == addressId) {
                prefs.remove(key)
            }
        }
    }

}