package com.diegoparra.veggie.user.data

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

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val LAST_SIGNED_IN_EMAIL = stringPreferencesKey("last_signed_in_email")
    }


    suspend fun savedLastSignedInEmail(email: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_SIGNED_IN_EMAIL] = email
        }
    }

    suspend fun getLastSignedInEmail() : String? {
        return context.dataStore.data
            .map { prefs ->
                prefs[LAST_SIGNED_IN_EMAIL]
            }
            .first()
    }

}