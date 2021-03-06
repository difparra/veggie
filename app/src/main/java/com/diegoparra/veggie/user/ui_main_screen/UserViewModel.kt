package com.diegoparra.veggie.user.ui_main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.auth.usecases.GetProfileAsFlowUseCase
import com.diegoparra.veggie.auth.usecases.SignOutUseCase
import com.diegoparra.veggie.core.kotlin.Either
import com.diegoparra.veggie.core.kotlin.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getProfileAsFlowUseCase: GetProfileAsFlowUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    val isSignedIn =
        getProfileAsFlowUseCase.isSignedIn()
            .map { Event(it) }
            .asLiveData()

    val name =
        getProfileAsFlowUseCase.getName()
            .map { if (it is Either.Right) it.b else null }
            .asLiveData()

    val email =
        getProfileAsFlowUseCase.getEmail()
            .map { if (it is Either.Right) it.b else null }
            .asLiveData()

    val photoUrl =
        getProfileAsFlowUseCase.getPhotoUrl()
            .map {
                if (it is Either.Right) {
                    Timber.d("photoUri = ${it.b}")
                    it.b
                } else {
                    null
                }
            }
            .asLiveData()


    fun signOut() = viewModelScope.launch { signOutUseCase() }

}