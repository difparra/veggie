package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Event
import com.diegoparra.veggie.user.usecases.GetBasicAuthInfoUseCase
import com.diegoparra.veggie.user.usecases.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getBasicAuthInfoUseCase: GetBasicAuthInfoUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    val isSignedIn =
        getBasicAuthInfoUseCase.isSignedIn()
            .map { Event(it) }
            .asLiveData()

    val name =
        getBasicAuthInfoUseCase.getName()
            .map { if(it is Either.Right) it.b else null }
            .asLiveData()

    val email =
        getBasicAuthInfoUseCase.getEmail()
            .map { if(it is Either.Right) it.b else null }
            .asLiveData()

    val photoUrl =
        getBasicAuthInfoUseCase.getPhotoUrl()
            .map { if(it is Either.Right) it.b else null }
            .asLiveData()


    fun signOut() = viewModelScope.launch { signOutUseCase() }

}