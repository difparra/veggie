package com.diegoparra.veggie.products.app.usecases

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.products.cart.domain.CartRepository
import javax.inject.Inject

class GetMinOrderCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {

    operator fun invoke(): Int {
        val minOrderEither = cartRepository.getMinOrder()
        if (minOrderEither is Either.Right) {
            return minOrderEither.b
        } else {
            return 100000
        }
    }

}