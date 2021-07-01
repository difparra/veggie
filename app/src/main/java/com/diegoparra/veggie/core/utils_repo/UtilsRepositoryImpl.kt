package com.diegoparra.veggie.core.utils_repo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.diegoparra.veggie.core.utils_repo.UtilsRepository
import com.diegoparra.veggie.core.android.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_utils_prefs")

class UtilsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UtilsRepository {

    companion object {
        val IS_INTERNET_AVAILABLE_KEY = booleanPreferencesKey("is_internet_available")
    }

    override suspend fun setInternetAvailable(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_INTERNET_AVAILABLE_KEY] = value
        }
    }

    override fun isInternetAvailable(): Flow<Boolean?> {
        return context.dataStore.data
            .map { prefs ->
                prefs[IS_INTERNET_AVAILABLE_KEY]
            }
            .flowOn(dispatcher)
    }

}