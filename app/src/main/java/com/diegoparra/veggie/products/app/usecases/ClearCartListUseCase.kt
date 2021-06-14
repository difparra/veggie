package com.diegoparra.veggie.products.app.usecases

import com.diegoparra.veggie.products.cart.domain.CartRepository
import javax.inject.Inject

class ClearCartListUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {

    suspend operator fun invoke() =
        cartRepository.deleteAllItems()

}