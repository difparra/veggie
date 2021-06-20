package com.diegoparra.veggie.order.usecases

import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.order.domain.DefaultConfigValues
import com.diegoparra.veggie.order.domain.OrderRepository
import javax.inject.Inject

class GetMinOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {

    /*
     *  Decided to finally cache value in useCase:
     *  Repo already cache configValues and do not fetch from server if minimum fetch interval has
     *  not been reached. But, caching the value in useCase avoid the parsing from primitive values
     *  (remote config types) to the objects actually used, and avoid verifying if minimum fetch
     *  interval is met. UseCases will commonly last as long as the viewModel, which is commonly a
     *  short time, so it is ok to request remoteConfig server only once while useCase is alive.
     */

    private var minOrder: Int? = null
    private val defaultMinOrder = DefaultConfigValues.minOrder
        //  Used when getting an exception from remoteConfigApi, such as when there is no internet
    private suspend fun getMinOrder(): Int {
        return minOrder
            ?: when (val repoValue = orderRepository.getMinOrder()) {
                is Either.Left -> defaultMinOrder
                is Either.Right -> {
                    minOrder = repoValue.b
                    repoValue.b
                }
            }
    }

    suspend operator fun invoke(): Int = getMinOrder()

}