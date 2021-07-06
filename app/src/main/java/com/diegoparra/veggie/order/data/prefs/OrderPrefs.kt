package com.diegoparra.veggie.order.data.prefs

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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "order_prefs")

class OrderPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val ORDER_FETCHED_AT_MILLIS = longPreferencesKey("order_fetched_at_millis")
    }

    suspend fun saveOrdersLastSuccessfulFetchAt(value: BasicTime) {
        context.dataStore.edit { prefs ->
            prefs[ORDER_FETCHED_AT_MILLIS] = value.millisEpochUTC
        }
    }

    suspend fun getOrdersLastSuccessfulFetchAt(): BasicTime? {
        return context.dataStore.data
            .map { prefs ->
                prefs[ORDER_FETCHED_AT_MILLIS]?.let {
                    BasicTime(it)
                }
            }
            .first()
    }

}