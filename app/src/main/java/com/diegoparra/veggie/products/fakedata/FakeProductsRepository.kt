package com.diegoparra.veggie.products.fakedata

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.products.domain.entities.MainProduct
import com.diegoparra.veggie.products.domain.entities.ProductAdmin
import com.diegoparra.veggie.products.domain.entities.Tag
import com.diegoparra.veggie.products.domain.entities.VariationProduct
import com.diegoparra.veggie.products.domain.repositories.ProductsRepository
import com.diegoparra.veggie.products.fakedata.UtilsFakes.toMainProduct
import com.diegoparra.veggie.products.fakedata.UtilsFakes.toVariationProduct
import timber.log.Timber
import java.util.*

class FakeProductsRepository(
    private val tags: List<Tag> = FakeProductsDatabase.tags,
    private val products: List<ProductAdmin> = FakeProductsDatabase.products
) : ProductsRepository {

    override suspend fun getTags(forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<Tag>> {
        return if(tags.isNullOrEmpty()){
            Timber.e("Tags not found")
            Either.Left(Failure.ProductsFailure.TagsNotFound)
        }else{
            Timber.i("tags: $tags")
            Either.Right(tags)
        }
    }

    override suspend fun getMainProductsByTagId(tagId: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<MainProduct>> {
        if(products.isNullOrEmpty())    return Either.Left(Failure.ProductsFailure.ProductsNotFound)

        val mainProds = mutableListOf<MainProduct>()
        for(product in products){
            if(product.tagId == tagId){
                mainProds.add(product.toMainProduct())
            }
        }

        return if(mainProds.isNullOrEmpty()){
            Timber.e("Main products not found")
            Either.Left(Failure.ProductsFailure.ProductsNotFound)
        }else{
            Timber.i("mainProds: $mainProds")
            Either.Right(mainProds)
        }
    }

    override suspend fun searchMainProductsByName(query: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<MainProduct>> {
        if(products.isNullOrEmpty())    return Either.Left(Failure.ProductsFailure.ProductsNotFound)
        if(query.isEmpty())             return Either.Left(Failure.SearchFailure.EmptyQuery)

        val mainProds = mutableListOf<MainProduct>()
        for(product in products){
            if(product.mainData.name.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))){
                mainProds.add(product.toMainProduct())
            }
        }

        return if(mainProds.isNullOrEmpty()){
            Timber.e("Main products not found for the search query $query")
            Either.Left(Failure.SearchFailure.NoSearchResults)
        }else{
            Timber.i("query: ${query}, mainProds: $mainProds")
            Either.Right(mainProds)
        }
    }


    override suspend fun getProductVariationsByMainId(mainId: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<VariationProduct>> {
        if(products.isNullOrEmpty())    return Either.Left(Failure.ProductsFailure.ProductsNotFound)

        for(product in products){
            if(product.mainData.mainId == mainId){
                Timber.i("variations: ${product.variations.map { it.toVariationProduct() }}")
                return Either.Right(product.variations.map { it.toVariationProduct() })
            }
        }

        Timber.e("Product with mainId: $mainId not found")
        return Either.Left(Failure.ProductsFailure.ProductsNotFound)
    }

}