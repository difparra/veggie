package com.diegoparra.veggie.main_activity

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.products.cart.domain.CartRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCartSizeUseCase @Inject constructor(
        private val cartRepository: CartRepository
) {

    operator fun invoke() = cartRepository.getCartSize().map {
        if(it is Either.Right){
            return@map it.b
        }else{
            return@map 0
        }
    }

}