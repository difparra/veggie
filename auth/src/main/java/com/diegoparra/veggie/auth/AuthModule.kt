package com.diegoparra.veggie.auth

import com.diegoparra.veggie.auth.data.AuthRepositoryImpl
import com.diegoparra.veggie.auth.domain.AuthRepository
import com.facebook.login.LoginManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    abstract fun bindsAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    /*
        TODO:   Add authCallbacks in external module
     */
}

@Module
@InstallIn(SingletonComponent::class)
object AuthModuleProvides {

    /*  //  TODO: Added in external module
    @Provides
    fun providesGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        return GoogleSignIn.getClient(context, gso)
    }*/

    @Provides
    fun providesFacebookLoginManager(): LoginManager {
        return LoginManager.getInstance()
    }

}