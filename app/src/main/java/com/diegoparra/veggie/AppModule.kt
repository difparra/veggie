package com.diegoparra.veggie

import android.content.Context
import androidx.room.Room
import com.diegoparra.veggie.core.Exclude
import com.diegoparra.veggie.order.data.retrofit.OrderService
import com.diegoparra.veggie.order.data.room.OrderDao
import com.diegoparra.veggie.products.cart.data.room.CartDao
import com.diegoparra.veggie.products.data.room.ProductsDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    /*
                FIREBASE API         ---------------------------------------------------------------
     */

    @Singleton
    @Provides
    fun providesFirebaseDatabase(): FirebaseFirestore {
        return Firebase.firestore.apply {
            firestoreSettings = firestoreSettings {
                isPersistenceEnabled = false
            }
        }
    }

    @Singleton
    @Provides
    fun providesFirebaseRemoteConfig(): FirebaseRemoteConfig {
        return Firebase.remoteConfig.apply {
            setConfigSettingsAsync(remoteConfigSettings {
                minimumFetchIntervalInSeconds = TimeUnit.HOURS.toSeconds(8)
            })
            setDefaultsAsync(R.xml.remote_config_defaults)
        }
    }

    @Singleton
    @Provides
    fun providesGson(): Gson {
        return Gson()
    }

    @Singleton
    @Provides
    fun providesFirebaseAuth(): FirebaseAuth {
        return Firebase.auth.apply {
            //  OK: It will be the languge set on the phone (e.g. German)
            useAppLanguage()
        }
    }


    /*
                RETROFIT         -------------------------------------------------------------------
     */

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        //  Gson configuration
        val gson = GsonBuilder()
            .addSerializationExclusionStrategy(object : ExclusionStrategy {
                override fun shouldSkipField(f: FieldAttributes?): Boolean {
                    return f?.getAnnotation(Exclude::class.java) != null
                }
                override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                    return false
                }
            })
            .create()

        //  Retrofit Logging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        //  Retrofit
        return Retrofit.Builder()
            .baseUrl("https://veggie-co-default-rtdb.firebaseio.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun providesOrderService(retrofit: Retrofit): OrderService {
        return retrofit.create(OrderService::class.java)
    }


    /*
                ROOM API         -------------------------------------------------------------------
     */

    @Singleton
    @Provides
    fun providesVeggieDatabase(@ApplicationContext appContext: Context): VeggieDatabase {
        return Room
            .databaseBuilder(appContext, VeggieDatabase::class.java, VeggieDatabase.DB_NAME)
            .build()
    }

    @Provides
    fun providesProductsDao(veggieDatabase: VeggieDatabase): ProductsDao {
        return veggieDatabase.productsDao()
    }

    @Provides
    fun providesCartDao(veggieDatabase: VeggieDatabase): CartDao {
        return veggieDatabase.cartDao()
    }

    @Provides
    fun providesOrderDao(veggieDatabase: VeggieDatabase): OrderDao {
        return veggieDatabase.orderDao()
    }

}