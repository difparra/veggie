package com.diegoparra.veggie.core.android

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.diegoparra.veggie.core.internet.IsInternetAvailableUseCase
import com.diegoparra.veggie.core.kotlin.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "local_update_prefs")

class LocalUpdateHelper<out Dto, out Entity>(
    private val lastSuccessfulFetchPrefs: TimePrefsImpl,
    private val room: RoomDb<Entity>,
    private val serverApi: ServerApi<Dto>,
    private val mapper: Mapper<Dto, Entity>,
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase
) {

    class TimePrefsImpl(
        private val key: String,
        private val context: Context
    ) {
        //  Default key when collecting data does not depend on user (i.e. products)
        private val DEFAULT_PREFS_KEY = longPreferencesKey(key)

        private fun getKey(userIdStr: String?): Preferences.Key<Long> =
            if(userIdStr == null) DEFAULT_PREFS_KEY else longPreferencesKey(key + userIdStr)

        //      HELPER FUNCTIONS

        suspend fun saveUpdatedAt(updatedAt: BasicTime, userId: String?) {
            context.dataStore.edit { prefs ->
                prefs[getKey(userId)] = updatedAt.millisEpochUTC
            }
        }

        suspend fun getUpdatedAt(userId: String?): BasicTime? {
            return context.dataStore.data
                .map { preferences ->
                    preferences[getKey(userId)]
                }
                .first()
                ?.let { BasicTime(it) }
        }
    }

    interface RoomDb<Entity> {
        suspend fun getLastUpdatedTime(): Long?
        suspend fun updateItems(itemsUpdated: List<@JvmSuppressWildcards Entity>, itemsDeleted: List<String>)
    }

    interface ServerApi<Dto> {
        suspend fun getItemsUpdatedAfter(basicTime: BasicTime, userId: String?): Either<Failure, List<Dto>>
    }

    interface Mapper<Dto, Entity> {
        fun mapToEntity(dto: Dto): Entity
        fun isDtoDeleted(dto: Dto): Boolean
        fun getId(dto: Dto): String
    }


    suspend fun update(source: Source, userId: String? = null): Either<Failure, Unit> {
        return if (source.mustFetchFromServer(
                lastSuccessfulFetch = lastSuccessfulFetchPrefs.getUpdatedAt(userId) ?: BasicTime(0),
                isInternetAvailable = isInternetAvailableUseCase.invoke().first()
            )
        ) {
            val actualLastUpdate = BasicTime(room.getLastUpdatedTime() ?: 0)
            Timber.d("Source says: Data must be collected from server. ActualLastUpdate = $actualLastUpdate")
            serverApi.getItemsUpdatedAfter(actualLastUpdate, userId)
                .map {
                    Timber.d("Data collected from server. list: $it")
                    room.updateItems(
                        itemsUpdated = it.filterNot { mapper.isDtoDeleted(it) }.map { mapper.mapToEntity(it) },
                        itemsDeleted = it.filter { mapper.isDtoDeleted(it) }.map { mapper.getId(it) }
                    )
                }
                .onSuccess {
                    Timber.d("List updated locally. Setting new lastFetchTime in prefs: $it")
                    lastSuccessfulFetchPrefs.saveUpdatedAt(BasicTime.now(), userId)
                }
        } else {
            Timber.d("Source says: Don't collect products from server. Possible reasons: There is no internet access, data is not expired, source was set as cache. Returning without failure...")
            Either.Right(Unit)
        }
    }
}