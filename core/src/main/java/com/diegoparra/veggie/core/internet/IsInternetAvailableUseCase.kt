package com.diegoparra.veggie.core.internet

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject


/**
 * This allows the app to have a single source of truth to get the connectivity state.
 * However, in order to get this working, setInternetAvailable must be set in some part of the app,
 * it could be in mainActivity using connectionLiveData.
 */

class IsInternetAvailableUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {

    operator fun invoke(): Flow<Boolean> {
        return coreRepository
            .isInternetAvailable()
            .filterNotNull()
            .distinctUntilChanged()
    }



}