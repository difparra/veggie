package com.diegoparra.veggie.products.domain.usecases

import com.diegoparra.veggie.products.domain.repositories.CartRepository
import javax.inject.Inject

class GetMinOrderCartUseCase @Inject constructor(
        private val cartRepository: CartRepository
) {

    operator fun invoke() = cartRepository.getMinOrder()

}