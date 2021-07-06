package com.diegoparra.veggie.core.internet

import kotlinx.coroutines.flow.Flow

interface CoreRepository {

    suspend fun setInternetAvailable(value: Boolean)
    fun isInternetAvailable(): Flow<Boolean?>

}