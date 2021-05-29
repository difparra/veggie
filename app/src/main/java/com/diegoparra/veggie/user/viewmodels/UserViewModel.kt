package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.diegoparra.veggie.core.Event
import com.diegoparra.veggie.user.usecases.IsSignedInUseCase
import com.diegoparra.veggie.user.usecases.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val isSignedInUseCase: IsSignedInUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    val isSignedIn = isSignedInUseCase().asLiveData()

    fun signOut() = signOutUseCase()

}