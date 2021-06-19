package com.diegoparra.veggie.order.usecases

import com.diegoparra.veggie.ConfigDefaults
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.getOrElse
import com.diegoparra.veggie.order.domain.OrderRepository
import javax.inject.Inject

class GetMinOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {

    private var minOrder: Int? = null

    /**
     * This function load value from repository only once by saving the result in a local variable.
     * This is ok, as config values are not intended to change frequently, and this use case will
     * not survive to long. As long as another useCase is created value will be loaded again from repo.
     * UseCase will normally survive as long as the viewModel in where it was created.
     */
    suspend operator fun invoke(): Int {
        //  Return already loaded config value
        minOrder?.let { return@invoke it }

        //  Load from repo if value has not been initialized
        return when (val result = orderRepository.getMinOrder()) {
            is Either.Left -> ConfigDefaults.Order.minOrder
            is Either.Right -> {
                minOrder = result.b
                result.b
            }
        }
    }

    /*  TODO:   I am still not completely sure, but the intended behaviour of caching the value in
                the use case could be actually moved to the repository rather than keeping here.
                Repository can have a local and a remote database.
                The only drawback I can think of, and only in here is that minOrder is called every
                time total is changed, that is so frequently, and even if calling from local database
                in repo, a lot of processes will be done before collecting the value, possibly making
                the app slower: useCase call repo, repo get the local time and compare with the
                last fetched time, if data has not expired, it will call datastore to fetch local value,
                *(the fetch from datastore can be omitted by doing the same as here, with a local
                variable, and keeping the api as singleton in the app).
                Therefore, the additional step that will be done is comparing with local time.
     */

}