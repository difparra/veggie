package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.diegoparra.veggie.user.usecases.IsSignedInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val isSignedInUseCase: IsSignedInUseCase
) : ViewModel() {

    val isSignedIn = isSignedInUseCase().asLiveData()

}