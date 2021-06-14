package com.diegoparra.veggie.user.auth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Event
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.user.auth.usecases.auth.FacebookAuthUseCase
import com.diegoparra.veggie.user.auth.usecases.auth.GoogleAuthUseCase
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInOptionsViewModel @Inject constructor(
    val googleSignInClient: GoogleSignInClient,
    private val googleAuthUseCase: GoogleAuthUseCase,
    val loginManager: LoginManager,
    private val facebookAuthUseCase: FacebookAuthUseCase
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _failure = MutableLiveData<Failure>()
    val failure: LiveData<Failure> = _failure

    private val _navigateSignedIn = MutableLiveData<Event<Boolean>>()
    val navigateSignedIn: LiveData<Event<Boolean>> = _navigateSignedIn

    fun onSignInGoogleResult(account: Either<ApiException, GoogleSignInAccount>) {
        viewModelScope.launch {
            _loading.value = true
            googleAuthUseCase(account).fold(::handleFailureLogIn, ::handleSuccessfulLogIn)
        }
    }

    fun onSignInFacebookResult(result: Either<FacebookException, LoginResult>) {
        viewModelScope.launch {
            _loading.value = true
            facebookAuthUseCase(result).fold(::handleFailureLogIn, ::handleSuccessfulLogIn)
        }
    }

    private fun handleFailureLogIn(failure: Failure) {
        _loading.value = false
        _failure.value = failure
    }

    private fun handleSuccessfulLogIn(nothing: Unit) {
        _loading.value = false
        _navigateSignedIn.value = Event(true)
    }

}