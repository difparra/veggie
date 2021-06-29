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

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "prods_prefs")

class ProductPrefsImpl @Inject constructor(
        @ApplicationContext private val context: Context
) : ProductPrefs {

    companion object {
        val TAGS_UPDATED_AT_MILLIS = longPreferencesKey("tags_updated_at_millis")
    }


    private suspend fun saveUpdatedAt(key: Preferences.Key<Long>, updatedAtInMillis: Long) {
        context.dataStore.edit { prefs ->
            prefs[key] = updatedAtInMillis
        }
    }

    override suspend fun saveTagsUpdatedAt(value: BasicTime) = saveUpdatedAt(TAGS_UPDATED_AT_MILLIS, value.millisEpochUTC)


    private suspend fun getUpdatedAt(key: Preferences.Key<Long>) : Long? {
        return context.dataStore.data
                .map { preferences ->
                    preferences[key]
                }
                .first()
    }

    override suspend fun getTagsUpdatedAt(): BasicTime? =
        getUpdatedAt(TAGS_UPDATED_AT_MILLIS)?.let { BasicTime(it) }
}