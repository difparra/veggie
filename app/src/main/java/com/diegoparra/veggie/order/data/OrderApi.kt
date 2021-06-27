package com.diegoparra.veggie.order.data

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.order.data.order_dto.OrderDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderApi @Inject constructor(
    private val database: FirebaseFirestore
) {

    suspend fun sendOrder(order: OrderDto): Either<Failure, String> {
        return try {
            val update = database
                .collection(FirebaseConstants.Firestore.Collections.orders)
                .add(order)
                .await()
            Either.Right(update.id)
        }catch (e: Exception) {
            Either.Left(Failure.ServerError(exception = e))
        }
    }

}