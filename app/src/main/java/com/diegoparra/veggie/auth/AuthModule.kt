package com.diegoparra.veggie.auth

import android.content.Context
import com.diegoparra.veggie.R
import com.diegoparra.veggie.auth.data.AuthRepositoryImpl
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    abstract fun bindsUserRepository(
        userRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

}

@Module
@InstallIn(SingletonComponent::class)
object AuthModuleProvides {

    @Provides
    fun providesGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        return GoogleSignIn.getClient(context, gso)
    }

    @Provides
    fun providesFacebookLoginManager(): LoginManager {
        return LoginManager.getInstance()
    }

}