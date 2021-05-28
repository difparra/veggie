package com.diegoparra.veggie.user.ui

import androidx.annotation.IdRes
import androidx.navigation.NavController
import com.diegoparra.veggie.R

/**
 * - It is the one that will save the original destination in its saveStateHandle (as it is the one
 * that can access to it easily).
 * - Will also be the reference to popBackStack and finish login flow.
 */
@IdRes private val startDestination = R.id.signInOptionsFragment

@IdRes
private fun getOriginalDestination(
    navController: NavController
): Int {
    val savedStateHandle = navController.getBackStackEntry(startDestination).savedStateHandle
    return savedStateHandle.get<Int>(SignInOptionsFragment.ORIGINAL_DESTINATION)!!
}

fun setLoginResult(navController: NavController, loginSuccessful: Boolean){
    val originalDestination = getOriginalDestination(navController)
    val originalSavedStateHandle = navController.getBackStackEntry(originalDestination).savedStateHandle
    originalSavedStateHandle.set(SignInOptionsFragment.LOGIN_SUCCESSFUL, loginSuccessful)
}

fun setLoginResultAndNavigate(
    navController: NavController,
    loginResult: Boolean = true
) {
    setLoginResult(navController, loginResult)
    navController.popBackStack(startDestination, true)
}