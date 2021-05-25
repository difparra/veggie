package com.diegoparra.veggie.products.data.firebase

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class ProductsApi @Inject constructor(
        private val database: FirebaseFirestore,
        private val remoteConfig: FirebaseRemoteConfig
) {
    private val gson by lazy { Gson() }

    suspend fun getTags(): Either<Failure, List<TagDto>> {
        return try {
            remoteConfig.fetchAndActivate().await()
            val categoriesString =
                remoteConfig[ProdsFirebaseConstants.RemoteConfigKeys.categories].asString()
            val tagsList = gson.fromJson(categoriesString, TagDtoList::class.java)
            Either.Right(tagsList.tagsArray)
        } catch (e: Exception) {
            Timber.d("Error getting tags: $e")
            Either.Left(Failure.ServerError(exception = e, message = "Error getting tags."))
        }
    }

    suspend fun getSortedProductsUpdatedAfter(timestamp: Timestamp) : Either<Failure, List<ProductDto>> {
        return try {
            val prods = database
                .collection(ProdsFirebaseConstants.Collections.products)
                .whereGreaterThan(ProdsFirebaseConstants.Keys.updatedAt, timestamp)
                .orderBy(ProdsFirebaseConstants.Keys.updatedAt)
                .get()
                .await()
                .map { it.toObject<ProductDto>() }
            Either.Right(prods)
        }catch (e: Exception){
            Either.Left(Failure.ServerError(exception = e, message = "Error getting last updated products."))
        }
    }

}