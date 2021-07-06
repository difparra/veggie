package com.diegoparra.veggie.core.internet

import javax.inject.Inject

class SetIsInternetAvailableUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {

    suspend operator fun invoke(isInternetAvailable: Boolean) {
        coreRepository.setInternetAvailable(isInternetAvailable)
    }

}