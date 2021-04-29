package com.diegoparra.veggie.products.domain.usecases

import com.diegoparra.veggie.products.domain.repositories.ProductsRepository
import javax.inject.Inject

class GetTagsUseCase @Inject constructor(private val productsRepository: ProductsRepository) {

    suspend operator fun invoke() = productsRepository.getTags()

}