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

    companion object {
        val SELECTED_ADDRESS_ID = stringPreferencesKey("selected_address")
    }


    suspend fun setSelectedAddress(addressId: String) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_ADDRESS_ID] = addressId
        }
    }

    suspend fun getSelectedAddress(): String? {
        return context.dataStore.data
            .map { prefs ->
                prefs[SELECTED_ADDRESS_ID]
            }.first()
    }

}