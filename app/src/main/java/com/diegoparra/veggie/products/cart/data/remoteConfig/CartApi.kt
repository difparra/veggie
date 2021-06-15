package com.diegoparra.veggie.products.cart.data.remoteConfig

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class CartApi @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) {

    suspend fun getMinOrder(): Either<Failure, Int> {
        return try {
            remoteConfig.fetchAndActivate().await()
            val minOrder = remoteConfig[CartFirebaseConstants.RemoteConfigKeys.minOrder].asDouble()
            Either.Right(minOrder.toInt())
        } catch (e: Exception) {
            Timber.d("Error getting minOrder: $e")
            Either.Left(Failure.ServerError(exception = e))
        }
    }

}