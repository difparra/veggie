package com.diegoparra.veggie.products.data.products.firebase

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.gson.Gson
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProductsApi @Inject constructor(
        private val database: FirebaseFirestore,
        private val remoteConfig: FirebaseRemoteConfig
) {
    private val gson by lazy { Gson() }


    suspend fun getTags() : Either<Failure, List<TagDto>> = suspendCoroutine { continuation ->
        remoteConfig
                .fetchAndActivate()
                .addOnSuccessListener {
                    val categoriesString = remoteConfig[ProdsFirebaseConstants.RemoteConfigKeys.categories].asString()
                    val tagsList = gson.fromJson(categoriesString, TagDtoList::class.java)
                    continuation.resume(Either.Right(tagsList.tagsArray))
                }
                .addOnFailureListener {
                    Timber.d("Error getting tags: $it")
                    continuation.resume(Either.Left(Failure.UnknownFailure(exception = it, message = "Error getting tags.")))
                }
    }

    suspend fun getMainProductsByTagId(tagId: String) : Either<Failure, List<ProductDto>> = suspendCoroutine { continuation ->
        database.collection(ProdsFirebaseConstants.Collections.products)
                .whereEqualTo(ProdsFirebaseConstants.Keys.tagId, tagId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val prods = snapshot.map {
                        it.toObject<ProductDto>()
                    }
                    continuation.resume(Either.Right(prods))
                }
                .addOnFailureListener {
                    Timber.d("Error getting products: $it")
                    continuation.resume(Either.Left(Failure.UnknownFailure(exception = it, message = "Error getting products.")))
                }
    }

}