package com.diegoparra.veggie.order.data.firebase

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.order.data.firebase.order_dto.OrderDto
import com.diegoparra.veggie.products.data.firebase.ProdsFirebaseConstants
import com.diegoparra.veggie.products.data.firebase.ProductDto
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class OrderApi @Inject constructor(
    private val database: FirebaseFirestore
) {

    suspend fun sendOrder(order: OrderDto): Either<Failure, String> {
        return try {
            val update = database
                .collection(OrderFirebaseConstants.Firestore.Collections.orders)
                .add(order)
                .await()
            Either.Right(update.id)
        } catch (e: Exception) {
            Timber.e("Exception class=${e.javaClass}, message=${e.message}")
            Either.Left(Failure.ServerError(exception = e))
        }
    }

    suspend fun getOrdersUser(userId: String): Either<Failure, List<Pair<String, OrderDto>>> {
        return try {
            val orders = database
                .collection(OrderFirebaseConstants.Firestore.Collections.orders)
                .whereEqualTo(OrderFirebaseConstants.Firestore.Fields.userIdComplete, userId)
                .orderBy(OrderFirebaseConstants.Firestore.Fields.deliveryDateFrom, Query.Direction.DESCENDING)
                .get()
                .await()
                .map {
                    val id = it.id
                    val order = it.toObject<OrderDto>()
                    Pair(id, order)
                }
            Either.Right(orders)
        } catch (e: Exception) {
            Timber.e("Exception class=${e.javaClass}, message=${e.message}")
            Either.Left(Failure.ServerError(exception = e))
        }
    }

    suspend fun getOrdersUpdatedAfter(timestamp: Timestamp): Either<Failure, List<Pair<String, OrderDto>>> {
        return try {
            val orders = database
                .collection(OrderFirebaseConstants.Firestore.Collections.orders)
                .whereGreaterThan(OrderFirebaseConstants.Firestore.Fields.updatedAt, timestamp)
                .get(Source.SERVER)
                .await()
                .map {
                    val id = it.id
                    val order = it.toObject<OrderDto>()
                    Pair(id, order)
                }
            Either.Right(orders)
        } catch (e: Exception) {
            Timber.e("Exception class=${e.javaClass}, message=${e.message}")
            Either.Left(Failure.ServerError(exception = e))
        }
    }

}