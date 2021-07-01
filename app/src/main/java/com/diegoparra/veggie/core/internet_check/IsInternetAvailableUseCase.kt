package com.diegoparra.veggie.core.internet_check

import com.diegoparra.veggie.core.utils_repo.UtilsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class IsInternetAvailableUseCase @Inject constructor(
    private val utilsRepository: UtilsRepository
) {

    operator fun invoke(): Flow<Boolean> {
        return utilsRepository
            .isInternetAvailable()
            .filterNotNull()
            .distinctUntilChanged()
    }

}