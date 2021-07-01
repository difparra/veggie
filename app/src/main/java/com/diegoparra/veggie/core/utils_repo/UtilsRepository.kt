package com.diegoparra.veggie.core.utils_repo

import kotlinx.coroutines.flow.Flow

interface UtilsRepository {

    suspend fun setInternetAvailable(value: Boolean)
    fun isInternetAvailable(): Flow<Boolean?>

}