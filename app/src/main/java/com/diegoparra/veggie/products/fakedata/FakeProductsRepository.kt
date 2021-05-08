package com.diegoparra.veggie.products.fakedata

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.products.domain.entities.MainProduct
import com.diegoparra.veggie.products.domain.entities.AdminProduct
import com.diegoparra.veggie.products.domain.entities.Tag
import com.diegoparra.veggie.products.domain.entities.ProductVariation
import com.diegoparra.veggie.products.domain.repositories.ProductsRepository
import com.diegoparra.veggie.products.fakedata.UtilsFakes.toMainProduct
import com.diegoparra.veggie.products.fakedata.UtilsFakes.toVariationProduct
import timber.log.Timber
import java.util.*

class FakeProductsRepository(
    private val tags: List<Tag> = FakeProductsDatabase.tags,
    private val products: List<AdminProduct> = FakeProductsDatabase.products
) : ProductsRepository {

    override suspend fun getTags(forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<Tag>> {
        Timber.d("getTags() called with: forceUpdate = $forceUpdate, expirationTimeMillis = $expirationTimeMillis")
        return if(tags.isNullOrEmpty()){
            Timber.e("getTags - Tags not found")
            Either.Left(Failure.ProductsFailure.TagsNotFound)
        }else{
            Timber.i("getTags: $tags")
            Either.Right(tags)
        }
    }

    override suspend fun getMainProductsByTagId(tagId: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<MainProduct>> {
        Timber.d("getMainProductsByTagId() called with: tagId = $tagId, forceUpdate = $forceUpdate, expirationTimeMillis = $expirationTimeMillis")
        if(products.isNullOrEmpty()){
            Timber.e("getMainProductsByTagId - empty products list")
            return Either.Left(Failure.ProductsFailure.ProductsNotFound)
        }

        val mainProds = mutableListOf<MainProduct>()
        for(product in products){
            if(product.tagId == tagId){
                mainProds.add(product.toMainProduct())
            }
        }

        return if(mainProds.isNullOrEmpty()){
            Timber.e("getMainProductsByTagId - products for tag $tagId not found")
            Either.Left(Failure.ProductsFailure.ProductsNotFound)
        }else{
            Timber.i("getMainProductsByTagId: mainProds = $mainProds")
            Either.Right(mainProds)
        }
    }

    override suspend fun searchMainProductsByName(query: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<MainProduct>> {
        Timber.d("searchMainProductsByName() called with: query = $query, forceUpdate = $forceUpdate, expirationTimeMillis = $expirationTimeMillis")
        if(products.isNullOrEmpty()){
            Timber.e("searchMainProductsByName - empty products list")
            return Either.Left(Failure.ProductsFailure.ProductsNotFound)
        }else if(query.isEmpty()){
            Timber.e("searchMainProductsByName - empty search query")
            return Either.Left(Failure.SearchFailure.EmptyQuery)
        }

        val mainProds = mutableListOf<MainProduct>()
        for(product in products){
            if(product.mainData.name.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))){
                mainProds.add(product.toMainProduct())
            }
        }

        return if(mainProds.isNullOrEmpty()){
            Timber.e("searchMainProductsByName - Main products not found for the search: $query")
            Either.Left(Failure.SearchFailure.NoSearchResults)
        }else{
            Timber.i("searchMainProductsByName: query= ${query}, mainProds= $mainProds")
            Either.Right(mainProds)
        }
    }


    override suspend fun getProductVariationsByMainId(mainId: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<ProductVariation>> {
        Timber.d("getProductVariationsByMainId() called with: mainId = $mainId, forceUpdate = $forceUpdate, expirationTimeMillis = $expirationTimeMillis")
        if(products.isNullOrEmpty()){
            Timber.e("getProductVariationsByMainId - empty products list")
            return Either.Left(Failure.ProductsFailure.ProductsNotFound)
        }

        for(product in products){
            if(product.mainData.mainId == mainId){
                Timber.i("getProductVariationsByMainId: mainId= $mainId, variations= ${product.variations.map { it.toVariationProduct() }}")
                return Either.Right(product.variations.map { it.toVariationProduct() })
            }
        }

        Timber.e("getProductVariationsByMainId - Product with mainId: $mainId not found")
        return Either.Left(Failure.ProductsFailure.ProductsNotFound)
    }

}