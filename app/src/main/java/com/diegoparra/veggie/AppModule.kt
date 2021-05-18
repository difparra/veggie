package com.diegoparra.veggie

import android.content.Context
import androidx.room.Room
import com.diegoparra.veggie.products.VeggieDatabase
import com.diegoparra.veggie.products.cart.data.room.CartDao
import com.diegoparra.veggie.products.data.room.ProductsDao
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    /*
                FIREBASE API         ---------------------------------------------------------------
     */

    @Singleton
    @Provides
    fun providesFirebaseDatabase() : FirebaseFirestore {
        return Firebase.firestore
    }

    @Singleton
    @Provides
    fun providesFirebaseRemoteConfig() : FirebaseRemoteConfig {
        return Firebase.remoteConfig.apply {
            setConfigSettingsAsync(remoteConfigSettings {
                //  TODO:   Change when launched in production
                //minimumFetchIntervalInSeconds = TimeUnit.HOURS.toSeconds(12)
                minimumFetchIntervalInSeconds = 1
            })
            setDefaultsAsync(R.xml.remote_config_defaults)
        }
    }


    /*
                ROOM API         -------------------------------------------------------------------
     */

    @Singleton
    @Provides
    fun providesVeggieDatabase(@ApplicationContext appContext: Context) : VeggieDatabase {
        return Room
            .databaseBuilder(appContext, VeggieDatabase::class.java, VeggieDatabase.DB_NAME)
            .build()
    }

    @Provides
    fun providesProductsDao(veggieDatabase: VeggieDatabase) : ProductsDao {
        return veggieDatabase.productsDao()
    }

    @Provides
    fun providesCartDao(veggieDatabase: VeggieDatabase) : CartDao {
        return veggieDatabase.cartDao()
    }

}