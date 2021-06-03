package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Event
import com.diegoparra.veggie.user.usecases.GetProfileUseCase
import com.diegoparra.veggie.user.usecases.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    val isSignedIn =
        getProfileUseCase.isSignedIn()
            .map { Event(it) }
            .asLiveData()

    val name =
        getProfileUseCase.getName()
            .map { if(it is Either.Right) it.b else null }
            .asLiveData()

    val email =
        getProfileUseCase.getEmail()
            .map { if(it is Either.Right) it.b else null }
            .asLiveData()

    val photoUrl =
        getProfileUseCase.getPhotoUrl()
            .map { if(it is Either.Right) it.b else null }
            .asLiveData()


    fun signOut() = viewModelScope.launch { signOutUseCase() }

}