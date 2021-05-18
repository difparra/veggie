package com.diegoparra.veggie.products

import com.diegoparra.veggie.products.data.ProductsRepositoryImpl
import com.diegoparra.veggie.products.domain.ProductsRepository
import com.diegoparra.veggie.products.data.prefs.ProductPrefs
import com.diegoparra.veggie.products.data.prefs.ProductPrefsImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductsModule {

    @Binds
    abstract fun bindsProductsRepository(
        productsRepositoryImpl: ProductsRepositoryImpl
    ) : ProductsRepository

    @Binds
    abstract fun bindsProductsPrefs(
        productPrefsImpl: ProductPrefsImpl
    ) : ProductPrefs


    //      Requires also productsDao, but was inserted in the same appModule with the veggieDatabase

}