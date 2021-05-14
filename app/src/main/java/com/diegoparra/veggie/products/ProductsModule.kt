package com.diegoparra.veggie.products

import android.content.Context
import androidx.room.Room
import com.diegoparra.veggie.R
import com.diegoparra.veggie.products.data.cart.CartDao
import com.diegoparra.veggie.products.data.cart.CartRepositoryImpl
import com.diegoparra.veggie.products.data.VeggieDatabase
import com.diegoparra.veggie.products.data.products.ProductsRepositoryFirebase
import com.diegoparra.veggie.products.data.products.firebase.ProductsApi
import com.diegoparra.veggie.products.domain.repositories.CartRepository
import com.diegoparra.veggie.products.domain.repositories.ProductsRepository
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
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductsModule {

    @Provides
    fun providesProductsRepository(productsApi: ProductsApi) : ProductsRepository {
        return ProductsRepositoryFirebase(productsApi)
    }

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

    @Provides
    fun providesProductsApi(database: FirebaseFirestore, remoteConfig: FirebaseRemoteConfig) : ProductsApi {
        return ProductsApi(database, remoteConfig)
    }



    @Singleton
    @Provides
    fun providesVeggieDatabase(@ApplicationContext appContext: Context) : VeggieDatabase {
        return Room
                .databaseBuilder(appContext, VeggieDatabase::class.java, VeggieDatabase.DB_NAME)
                .build()
    }

    @Provides
    fun providesCartDao(veggieDatabase: VeggieDatabase) : CartDao {
        return veggieDatabase.cartDao()
    }

    @Provides
    fun providesCartRepository(cartDao: CartDao) : CartRepository {
        return CartRepositoryImpl(cartDao)
    }
}