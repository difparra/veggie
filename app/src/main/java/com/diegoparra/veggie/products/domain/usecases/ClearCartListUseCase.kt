package com.diegoparra.veggie.products.domain.usecases

import com.diegoparra.veggie.products.domain.repositories.CartRepository
import javax.inject.Inject

class ClearCartListUseCase @Inject constructor(
        private val cartRepository: CartRepository
) {

    suspend operator fun invoke() {
        cartRepository.deleteAllItems()
    }

}