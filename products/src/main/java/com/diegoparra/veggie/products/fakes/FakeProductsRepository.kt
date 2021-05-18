package com.diegoparra.veggie.products.fakes

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.products.domain.*
import com.diegoparra.veggie.products.fakes.UtilsFakes.toProduct
import com.diegoparra.veggie.products.fakes.UtilsFakes.toVariation
import timber.log.Timber
import java.lang.IllegalStateException
import java.util.*

class FakeProductsRepository(
    private val tags: List<Tag> = FakeProductsDatabase.tags,
    private val products: List<AdminProduct> = FakeProductsDatabase.products
) : ProductsRepository {

    private fun checkProductsNeitherNullNorEmpty(methodNameLog: String? = null) {
        if(products.isNullOrEmpty()){
            throw IllegalStateException("productsList in FakeProductsRepository is null or empty - Called from method: $methodNameLog")
        }
    }

    //  TODO:   Search the difference between throwable and exception in kotlin

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

    override suspend fun getMainProductsByTagId(tagId: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<Product>> {
        Timber.d("getMainProductsByTagId() called with: tagId = $tagId, forceUpdate = $forceUpdate, expirationTimeMillis = $expirationTimeMillis")
        checkProductsNeitherNullNorEmpty("getMainProductsByTagId")

        val mainProds = mutableListOf<Product>()
        for(product in products){
            if(product.tagId == tagId){
                try {
                    val mainProd = product.toProduct(product.mainData.mainVariationId)
                    mainProds.add(mainProd)
                }catch (e: Exception){
                    Timber.e("getMainProductsByTagId - mainVarId: ${product.mainData.mainVariationId} not found in mainId: ${product.mainData.mainId}")
                }
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

    override suspend fun searchMainProductsByName(query: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<Product>> {
        Timber.d("searchMainProductsByName() called with: query = $query, forceUpdate = $forceUpdate, expirationTimeMillis = $expirationTimeMillis")
        checkProductsNeitherNullNorEmpty("searchMainProductsByName")
        if(query.isEmpty()){
            Timber.e("searchMainProductsByName - empty search query")
            return Either.Left(Failure.SearchFailure.EmptyQuery)
        }

        val mainProds = mutableListOf<Product>()
        for(product in products){
            if(product.mainData.name.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))){
                try{
                    val mainProd = product.toProduct(product.mainData.mainVariationId)
                    mainProds.add(mainProd)
                }catch (e: Exception) { Timber.e("searchMainProductsByName - mainVarId: ${product.mainData.mainVariationId} not found in mainId: ${product.mainData.mainId}") }
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


    override suspend fun getProductVariationsByMainId(mainId: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<Variation>> {
        Timber.d("getProductVariationsByMainId() called with: mainId = $mainId, forceUpdate = $forceUpdate, expirationTimeMillis = $expirationTimeMillis")
        checkProductsNeitherNullNorEmpty("getProductVariationsByMainId")

        for(product in products){
            if(product.mainData.mainId == mainId){
                Timber.i("getProductVariationsByMainId: mainId= $mainId, variations= ${product.variations.map { it.toVariation() }}")
                return Either.Right(product.variations.map { it.toVariation() })
            }
        }

        Timber.e("getProductVariationsByMainId - Product with mainId: $mainId not found")
        return Either.Left(Failure.ProductsFailure.ProductsNotFound)
    }

    override suspend fun getProduct(mainId: String, varId: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, Product> {
        Timber.d("getProduct() called with: mainId = $mainId, varId = $varId, forceUpdate = $forceUpdate, expirationTimeMillis = $expirationTimeMillis")
        checkProductsNeitherNullNorEmpty("getProduct")

        for(product in products){
            if(product.mainData.mainId == mainId){
                try {
                    val prod = product.toProduct(varId)
                    return Either.Right(prod)
                }catch (e: Exception){
                    Timber.e("getProduct - varId: $varId not found in mainId: $mainId")
                    return Either.Left(Failure.ProductsFailure.ProductsNotFound)
                }
            }
        }
        Timber.e("getProduct - Product with mainId: $mainId not found")
        return Either.Left(Failure.ProductsFailure.ProductsNotFound)
    }
}