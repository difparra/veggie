package com.diegoparra.veggie.user.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.diegoparra.veggie.user.entities_and_repo.SignInMethod
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val LAST_SIGNED_IN_WITH = stringPreferencesKey("current_signed_in_with")
        val FB_ACCESS_TOKEN = stringPreferencesKey("fb")
    }

    suspend fun saveLastSignedInWith(signInMethod: SignInMethod) {
        context.dataStore.edit { prefs ->
            prefs[LAST_SIGNED_IN_WITH] = signInMethod.toString()
        }
    }

    suspend fun getLastSignedInWith(): SignInMethod? {
        return context.dataStore.data
            .map { prefs ->
                prefs[LAST_SIGNED_IN_WITH]?.let {
                    SignInMethod.valueOfOrUnknown(it)
                }
            }.first()
    }


    suspend fun saveFacebookAccessToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[FB_ACCESS_TOKEN] = token
        }
    }

    suspend fun getFacebookAccessToken(): String? {
        return context.dataStore.data
            .map { prefs ->
                prefs[FB_ACCESS_TOKEN]
            }.first()
    }

}