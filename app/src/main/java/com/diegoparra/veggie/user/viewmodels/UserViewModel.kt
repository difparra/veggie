package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Event
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.user.entities_and_repo.BasicUserInfo
import com.diegoparra.veggie.user.usecases.GetBasicUserInfoUseCase
import com.diegoparra.veggie.user.usecases.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getBasicUserInfoUseCase: GetBasicUserInfoUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    val isSignedIn =
        getBasicUserInfoUseCase.isSignedIn()
            .map { Event(it) }
            .asLiveData()

    val name =
        getBasicUserInfoUseCase.getName()
            .map { if(it is Either.Right) it.b else null }
            .asLiveData()

    val email =
        getBasicUserInfoUseCase.getEmail()
            .map { if(it is Either.Right) it.b else null }
            .asLiveData()

    fun signOut() = signOutUseCase()

}