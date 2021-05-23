package com.diegoparra.veggie.user.viewmodels

import androidx.lifecycle.*
import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.user.entities_and_repo.IsEmailRegistered
import com.diegoparra.veggie.user.usecases.ValidateEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailAuthViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase
) : ViewModel() {

    private val _email = MutableStateFlow<String?>(null)
    val email = _email.asLiveData()

    /*
        TODO:
            emailState problem with LiveData and navigation -> can't get back from signIn to email fragment
            check rotating (app crashed before but it is now not crashing)
            hilt nav graph view model
            maybe create new layout for signInOptions horizontal, move app icon to the left, and
                check scroll views (e.g. in signIn horizontal screen a scroll view must be added)
            Correct navigation to signIn-signUp -> Should navigate to signIn and in there
                validate data, if correct stay there but if not move to signUp.
     */

    private val _validateEmailResult = MutableStateFlow<Either<Failure, IsEmailRegistered>?>(null)
    fun validateAndSetEmail(email: String) : LiveData<Either<Failure, IsEmailRegistered>> {
        viewModelScope.launch {
            val result = validateEmailUseCase(email)
            _validateEmailResult.value = result
            when(result) {
                is Either.Left -> _email.value = null
                is Either.Right -> _email.value = email
            }
        }
        return _validateEmailResult.filterNotNull().asLiveData()
    }

}