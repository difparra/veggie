package com.diegoparra.veggie.support.data

import com.diegoparra.veggie.core.android.IoDispatcher
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.support.domain.SupportRepository
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.google.firebase.remoteconfig.ktx.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class SupportRepositoryImpl @Inject constructor(
    //  Can create an Api, but support repository is so small that I think it is not really necessary
    private val remoteConfig: FirebaseRemoteConfig,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): SupportRepository {

    private suspend fun <T> getValueFromRemoteConfig(
        configKey: String,
        parseConfigValue: (FirebaseRemoteConfigValue) -> T
    ): Either<Failure, T> {
        return try {
            remoteConfig.fetchAndActivate().await()
            Timber.d("key = $configKey, lastFetchTime = ${remoteConfig.info.fetchTimeMillis}")
            val configValue = remoteConfig[configKey]
            val parsedConfigValue = parseConfigValue(configValue)
            Timber.d("returned value: $parsedConfigValue")
            Either.Right(parsedConfigValue)
        } catch (e: Exception) {
            Timber.e("Exception occur while getting remoteConfig value: $configKey, exceptionClass=${e.javaClass}, exceptionMessage=${e.message}")
            Either.Left(Failure.ServerError(e))
        }
    }

    override suspend fun getContactEmail(): Either<Failure, String> = withContext(dispatcher) {
        getValueFromRemoteConfig(
            configKey = SupportFirebaseConstants.RemoteConfigKeys.contactEmail,
            parseConfigValue = { it.asString() }
        )
    }

    override suspend fun getContactPhoneNumber(): Either<Failure, String> = withContext(dispatcher){
        getValueFromRemoteConfig(
            configKey = SupportFirebaseConstants.RemoteConfigKeys.contactNumber,
            parseConfigValue = { it.asString() }
        )
    }
}