package com.diegoparra.veggie.auth.additional_features.phone_number.ui

import android.app.Activity
import androidx.lifecycle.*
import com.diegoparra.veggie.auth.utils.AuthFailure
import com.diegoparra.veggie.core.kotlin.Event
import com.diegoparra.veggie.core.kotlin.Failure
import com.diegoparra.veggie.auth.additional_features.phone_number.domain.SavePhoneNumberUseCase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PhoneNumberViewModel @Inject constructor(
    private val savePhoneNumberUseCase: SavePhoneNumberUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val auth = savePhoneNumberUseCase.auth
    private val _phoneNumber = savedStateHandle.getLiveData<String>(PHONE_NUMBER_SAVED_STATE_KEY)
    private val _verificationId =
        savedStateHandle.getLiveData<String>(VERIFICATION_ID_SAVED_STATE_KEY)
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading
    private val _failure = MutableLiveData<Event<Failure>>()
    val failure: LiveData<Event<Failure>> = _failure
    private val _navigateSuccess = MutableLiveData<Event<Boolean>>()
    val navigateSuccess: LiveData<Event<Boolean>> = _navigateSuccess

    private val _codeSent = MutableLiveData<Event<Pair<String, String>>>()
    val codeSent: LiveData<Event<Pair<String, String>>> = _codeSent



    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        _loading.value = true
        savePhoneNumberUseCase.validatePhoneNumber(phoneNumber).fold(
            { _failure.value = Event(it); Unit },
            {
                savedStateHandle.set(PHONE_NUMBER_SAVED_STATE_KEY, it)
                val options =
                    PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(it)
                        .setTimeout(60, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(phoneAuthCallbacks)
                        .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        )
    }

    fun resendVerificationCode(activity: Activity) {
        if(_phoneNumber.value == null || forceResendingToken == null) {
            Timber.e("Can't resend verification code, as phone number, or token are null")
            return
        }
        _loading.value = true
        val options =
            PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(_phoneNumber.value!!)
                .setTimeout(60, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(phoneAuthCallbacks)
                .setForceResendingToken(forceResendingToken!!)
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyPhoneNumberWithSmsCode(smsCode: String) {
        Timber.d("verifySmsCode() called with: smsCode = $smsCode")
        _loading.value = true
        try {
            val credential = PhoneAuthProvider.getCredential(_verificationId.value!!, smsCode)
            verifyPhoneNumberWithPhoneAuthCredential(credential)
        } catch (e: Exception) {
            Timber.d("verifySmsCode exception = $e")
            _failure.value = Event(Failure.ServerError(e))
        }
    }




    private val phoneAuthCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations: Instant verification and Auto-retrieval.
            _loading.value = false
            verifyPhoneNumberWithPhoneAuthCredential(phoneAuthCredential = credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            _loading.value = false
            val failure = when(e) {
                is FirebaseAuthInvalidCredentialsException -> AuthFailure.PhoneAuthFailures.InvalidRequest
                is FirebaseTooManyRequestsException -> AuthFailure.PhoneAuthFailures.TooManyRequests
                else -> Failure.ServerError(e)
            }
            _failure.value = Event(failure)
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            _loading.value = false
            savedStateHandle.set(VERIFICATION_ID_SAVED_STATE_KEY, verificationId)
            forceResendingToken = token
            _codeSent.value = Event(Pair(_phoneNumber.value!!, verificationId))
        }
    }

    private fun verifyPhoneNumberWithPhoneAuthCredential(phoneAuthCredential: PhoneAuthCredential) {
        viewModelScope.launch {
            savePhoneNumberUseCase.verifyPhoneNumberWithPhoneAuthCredential(_phoneNumber.value!!, phoneAuthCredential).fold(
                {
                    _loading.value = false
                    _failure.value = Event(it)
                    Unit
                },
                {
                    _loading.value = false
                    _navigateSuccess.value = Event(true)
                    Unit
                }
            )
        }
    }


    companion object {
        const val PHONE_NUMBER_SAVED_STATE_KEY = "phone_number"
        const val VERIFICATION_ID_SAVED_STATE_KEY = "verification_id"
    }

}