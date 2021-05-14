package com.diegoparra.veggie.products.data.products.firebase

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.products.domain.entities.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProductsApi @Inject constructor() {

    private val db = Firebase.firestore

    suspend fun getMainProductsByTagId(tagId: String) : Either<Failure, List<ProductDto>> =
        suspendCoroutine { continuation ->
            db.collection("products")
                    .whereEqualTo("tagId", tagId)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val prods = snapshot.map {
                            it.toObject<ProductDto>()
                        }
                        continuation.resume(Either.Right(prods))
                    }
                    .addOnFailureListener {
                        Timber.d("Error getting products: $it")
                        continuation.resume(Either.Left(Failure.UnknownFailure(exception = it, message = it.message)))
                    }
        }

}