package com.diegoparra.veggie.main_activity

import com.diegoparra.veggie.core.utils_repo.UtilsRepository
import javax.inject.Inject

class SetInternetAvailableUseCase @Inject constructor(
    private val utilsRepository: UtilsRepository
) {

    suspend operator fun invoke(isInternetAvailable: Boolean) {
        utilsRepository.setInternetAvailable(isInternetAvailable)
    }

}