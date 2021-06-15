package com.diegoparra.veggie.auth.ui

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.diegoparra.veggie.core.kotlin.Either
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException

class GoogleSignInContract :
    ActivityResultContract<GoogleSignInClient, Either<ApiException, GoogleSignInAccount>>() {

    override fun createIntent(context: Context, input: GoogleSignInClient?): Intent {
        return input!!.signInIntent
    }

    override fun parseResult(
        resultCode: Int, intent: Intent?
    ): Either<ApiException, GoogleSignInAccount> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account = task.getResult(ApiException::class.java)!!
            Either.Right(account)
        } catch (e: ApiException) {
            Either.Left(e)
        }
    }

}