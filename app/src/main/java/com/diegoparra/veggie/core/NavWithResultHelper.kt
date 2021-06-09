package com.diegoparra.veggie.core

import androidx.annotation.IdRes
import androidx.navigation.NavController

open class NavWithResultHelper<T>(
    @IdRes val startDestination: Int,
    private val resultKey: String
) {
    companion object {
        const val ORIGINAL_DESTINATION = "original_destination"
    }

    fun setPreviousDestinationAsOriginal(navController: NavController) {
        navController.currentBackStackEntry?.savedStateHandle?.set(
            ORIGINAL_DESTINATION,
            navController.previousBackStackEntry?.destination?.id!!
        )
    }

    @IdRes
    private fun getOriginalDestination(
        navController: NavController
    ): Int {
        val savedStateHandle = navController.getBackStackEntry(startDestination).savedStateHandle
        return savedStateHandle.get<Int>(ORIGINAL_DESTINATION)!!
    }

    fun setResult(navController: NavController, result: T) {
        val originalDestination = getOriginalDestination(navController)
        val originalSavedStateHandle =
            navController.getBackStackEntry(originalDestination).savedStateHandle
        originalSavedStateHandle.set(resultKey, result)
    }

    fun setResultAndNavigate(navController: NavController, result: T) {
        setResult(navController, result)
        navController.popBackStack(startDestination, true)
    }

}