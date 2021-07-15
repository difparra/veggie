package com.diegoparra.veggie.products.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.diegoparra.veggie.core.kotlin.BasicTime
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prods_prefs")

class ProductPrefsImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ProductPrefs {

    companion object {
        val TAGS_FETCHED_AT_MILLIS = longPreferencesKey("tags_fetched_at_millis")
    }


    //      HELPER FUNCTIONS

    private suspend fun saveUpdatedAt(key: Preferences.Key<Long>, updatedAtInMillis: Long) {
        context.dataStore.edit { prefs ->
            prefs[key] = updatedAtInMillis
        }
    }

    private suspend fun getUpdatedAt(key: Preferences.Key<Long>): Long? {
        return context.dataStore.data
            .map { preferences ->
                preferences[key]
            }
            .first()
    }


    //      TAGS

    override suspend fun saveTagsFetchAt(value: BasicTime) =
        saveUpdatedAt(TAGS_FETCHED_AT_MILLIS, value.millisEpochUTC)

    override suspend fun getTagsFetchAt(): BasicTime? =
        getUpdatedAt(TAGS_FETCHED_AT_MILLIS)?.let { BasicTime(it) }

}