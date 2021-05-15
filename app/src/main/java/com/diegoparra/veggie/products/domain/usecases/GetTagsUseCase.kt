package com.diegoparra.veggie.products.domain.usecases

import com.diegoparra.veggie.core.products.ProductsRepository
import javax.inject.Inject

class GetTagsUseCase @Inject constructor(private val productsRepository: ProductsRepository) {

    suspend operator fun invoke() = productsRepository.getTags()

}