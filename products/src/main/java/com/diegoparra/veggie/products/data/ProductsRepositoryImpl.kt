package com.diegoparra.veggie.products.data

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.products.IoDispatcher
import com.diegoparra.veggie.products.domain.Product
import com.diegoparra.veggie.products.data.DtoToEntityTransformations.toTimestamp
import com.diegoparra.veggie.products.data.firebase.ProductsApi
import com.diegoparra.veggie.products.data.prefs.ProductPrefs
import com.diegoparra.veggie.products.data.room.ProductEntitiesTransformations.toProduct
import com.diegoparra.veggie.products.data.room.ProductEntitiesTransformations.toTag
import com.diegoparra.veggie.products.data.room.ProductEntitiesTransformations.toVariation
import com.diegoparra.veggie.products.data.room.ProductsDao
import com.diegoparra.veggie.products.domain.Tag
import com.diegoparra.veggie.products.domain.ProductsRepository
import com.diegoparra.veggie.products.domain.Variation
import com.diegoparra.veggie.products.data.DtoToEntityTransformations.getListProdUpdateRoom
import com.diegoparra.veggie.products.data.DtoToEntityTransformations.getMainProdIdsToDelete
import com.diegoparra.veggie.products.data.DtoToEntityTransformations.toTagEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ProductsRepositoryImpl @Inject constructor(
    private val productsDao: ProductsDao,
    private val productsApi: ProductsApi,
    private val prefs: ProductPrefs,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductsRepository {

    private fun getCurrentTimeInMillis() = System.currentTimeMillis()
    private fun isDataExpired(lastUpdatedAtInMillis: Long, expirationTimeInMillis: Long) =
            (getCurrentTimeInMillis() - lastUpdatedAtInMillis) > expirationTimeInMillis

    override suspend fun getTags(forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<Tag>> = withContext(dispatcher) {
        if(forceUpdate || isDataExpired(lastUpdatedAtInMillis = prefs.getTagsUpdatedAt(), expirationTimeInMillis = expirationTimeMillis)) {
            when(val tagsNetwork = productsApi.getTags()){
                is Either.Left -> return@withContext tagsNetwork
                is Either.Right -> {
                    productsDao.updateAllTags(tags = tagsNetwork.b.map { it.toTagEntity() })
                    prefs.saveTagsUpdatedAt(getCurrentTimeInMillis())
                }
            }
        }
        val tagsLocal = productsDao.getAllTags()
        return@withContext if(tagsLocal.isNullOrEmpty()){
            Either.Left(Failure.ProductsFailure.TagsNotFound)
        }else{
            Either.Right(tagsLocal.map { it.toTag() })
        }
    }

    override suspend fun getMainProductsByTagId(tagId: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<Product>> = withContext(dispatcher) {
        val failure = updateLocalProductsFromApiIfNecessary(forceUpdate, expirationTimeMillis)
        if(failure != null){
            return@withContext Either.Left(failure)
        }

        val productsLocal = productsDao.getMainProductsByTagId(tagId)
        return@withContext if(productsLocal.isNullOrEmpty()){
            Either.Left(Failure.ProductsFailure.ProductsNotFound)
        }else{
            Either.Right(productsLocal.map { it.toProduct() })
        }
    }

    override suspend fun searchMainProductsByName(query: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<Product>> = withContext(dispatcher){
        val failure = updateLocalProductsFromApiIfNecessary(forceUpdate, expirationTimeMillis)
        if(failure != null){
            return@withContext Either.Left(failure)
        }

        val localSearchResults = productsDao.searchMainProdByName(query)
        return@withContext if(localSearchResults.isNullOrEmpty()){
            Either.Left(Failure.SearchFailure.NoSearchResults)
        }else{
            Either.Right(localSearchResults.map { it.toProduct() })
        }
    }

    override suspend fun getProductVariationsByMainId(mainId: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, List<Variation>> = withContext(dispatcher){
        val failure = updateLocalProductsFromApiIfNecessary(forceUpdate, expirationTimeMillis)
        if(failure != null){
            return@withContext Either.Left(failure)
        }

        val variationsLocal = productsDao.getProductVariationsByMainId(mainId)
        return@withContext if(variationsLocal.isNullOrEmpty()){
            Either.Left(Failure.ProductsFailure.ProductsNotFound)
        }else{
            Either.Right(variationsLocal.map { it.toVariation() })
        }
    }

    override suspend fun getProduct(mainId: String, varId: String, forceUpdate: Boolean, expirationTimeMillis: Long): Either<Failure, Product> = withContext(dispatcher){
        val failure = updateLocalProductsFromApiIfNecessary(forceUpdate, expirationTimeMillis)
        if(failure != null){
            return@withContext Either.Left(failure)
        }

        val productLocal = productsDao.getProduct(mainId, varId)
        return@withContext if(productLocal == null){
            Either.Left(Failure.ProductsFailure.ProductsNotFound)
        }else{
            Either.Right(productLocal.toProduct())
        }
    }




    private suspend fun updateLocalProductsFromApiIfNecessary(forceUpdate: Boolean, expirationTimeMillis: Long) : Failure? {
        //  TODO:   WorkManager to get products for the very first time
        val localLastUpdatedAtInMillis = productsDao.getLastProdUpdatedAtInMillis() ?: 0
        if(forceUpdate || isDataExpired(lastUpdatedAtInMillis = localLastUpdatedAtInMillis, expirationTimeInMillis = expirationTimeMillis)) {
            when(val productsNetwork = productsApi.getProductsUpdatedAfter(localLastUpdatedAtInMillis.toTimestamp())) {
                is Either.Left -> return productsNetwork.a
                is Either.Right -> {
                    val prods = productsNetwork.b
                    productsDao.updateProducts(
                            mainProdsIdToDelete = prods.getMainProdIdsToDelete(),
                            prodsToUpdate = prods.getListProdUpdateRoom()
                    )
                }
            }
        }
        return null
    }

}