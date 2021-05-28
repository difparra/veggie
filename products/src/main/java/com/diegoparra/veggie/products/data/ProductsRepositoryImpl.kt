package com.diegoparra.veggie.products.data

import com.diegoparra.veggie.core.Either
import com.diegoparra.veggie.core.Failure
import com.diegoparra.veggie.products.IoDispatcher
import com.diegoparra.veggie.products.data.DtoToEntityTransformations.toTimestamp
import com.diegoparra.veggie.products.data.firebase.ProductsApi
import com.diegoparra.veggie.products.data.prefs.ProductPrefs
import com.diegoparra.veggie.products.data.room.ProductEntitiesTransformations.toProduct
import com.diegoparra.veggie.products.data.room.ProductEntitiesTransformations.toTag
import com.diegoparra.veggie.products.data.room.ProductEntitiesTransformations.toVariationData
import com.diegoparra.veggie.products.data.room.ProductsDao
import com.diegoparra.veggie.products.data.DtoToEntityTransformations.getListProdUpdateRoom
import com.diegoparra.veggie.products.data.DtoToEntityTransformations.getMainProdIdsToDelete
import com.diegoparra.veggie.products.data.DtoToEntityTransformations.toTagEntity
import com.diegoparra.veggie.products.domain.*
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


    /*
                TAGS RELATED        ----------------------------------------------------------------
     */

    override suspend fun getTags(
        forceUpdate: Boolean,
        expirationTimeMillis: Long
    ): Either<Failure, List<Tag>> = withContext(dispatcher) {
        updateLocalTagsFromApiIfNecessary(forceUpdate, expirationTimeMillis)?.let {
            return@withContext Either.Left(it)
        }

        val tagsLocal = productsDao.getAllTags()
        return@withContext if (tagsLocal.isNullOrEmpty()) {
            Either.Left(Failure.ProductsFailure.TagsNotFound)
        } else {
            Either.Right(tagsLocal.map { it.toTag() })
        }
    }

    private suspend fun updateLocalTagsFromApiIfNecessary(
        forceUpdate: Boolean,
        expirationTimeMillis: Long
    ): Failure? {
        val localLastUpdatedAtInMillis = prefs.getTagsUpdatedAt()
        if (forceUpdate || isDataExpired(
                lastUpdatedAtInMillis = localLastUpdatedAtInMillis,
                expirationTimeInMillis = expirationTimeMillis
            )
        ) {
            when (val tagsNetwork = productsApi.getTags()) {
                is Either.Left -> return tagsNetwork.a
                is Either.Right -> {
                    productsDao.updateAllTags(tags = tagsNetwork.b.map { it.toTagEntity() })
                    prefs.saveTagsUpdatedAt(getCurrentTimeInMillis())
                }
            }
        }
        return null
    }


    /*
                PRODUCTS RELATED        ------------------------------------------------------------
     */

    override suspend fun getMainProductsByTagId(
        tagId: String,
        forceUpdate: Boolean,
        expirationTimeMillis: Long
    ): Either<Failure, List<Product>> = withContext(dispatcher) {
        updateLocalProductsFromApiIfNecessary(forceUpdate, expirationTimeMillis)?.let {
            return@withContext Either.Left(it)
        }

        val productsLocal = productsDao.getMainProductsByTagId(tagId)
        return@withContext productsLocal.let {
            if (it.isNullOrEmpty()) {
                Either.Left(Failure.ProductsFailure.ProductsNotFound)
            } else {
                Either.Right(it.map { it.toProduct() })
            }
        }
    }

    override suspend fun searchMainProductsByName(
        query: String,
        forceUpdate: Boolean,
        expirationTimeMillis: Long
    ): Either<Failure, List<Product>> = withContext(dispatcher) {
        updateLocalProductsFromApiIfNecessary(forceUpdate, expirationTimeMillis)?.let {
            return@withContext Either.Left(it)
        }

        val localSearchResults = productsDao.searchMainProdByName(query)
        return@withContext localSearchResults.let {
            if (it.isNullOrEmpty()) {
                Either.Left(Failure.SearchFailure.NoSearchResults)
            } else {
                Either.Right(it.map { it.toProduct() })
            }
        }
    }

    override suspend fun getVariationsByMainId(
        mainId: String,
        forceUpdate: Boolean,
        expirationTimeMillis: Long
    ): Either<Failure, List<VariationData>> = withContext(dispatcher) {
        updateLocalProductsFromApiIfNecessary(forceUpdate, expirationTimeMillis)?.let {
            return@withContext Either.Left(it)
        }

        val variationsLocal = productsDao.getProductVariationsByMainId(mainId)
        return@withContext variationsLocal.let {
            if (it.isNullOrEmpty()) {
                Either.Left(Failure.ProductsFailure.ProductsNotFound)
            } else {
                Either.Right(it.map { it.toVariationData() })
            }
        }
    }

    override suspend fun getProduct(
        mainId: String, varId: String,
        forceUpdate: Boolean,
        expirationTimeMillis: Long
    ): Either<Failure, Product> = withContext(dispatcher) {
        updateLocalProductsFromApiIfNecessary(forceUpdate, expirationTimeMillis)?.let {
            return@withContext Either.Left(it)
        }

        val productLocal = productsDao.getProduct(mainId, varId)
        return@withContext productLocal?.let {
            Either.Right(it.toProduct())
        } ?: Either.Left(Failure.ProductsFailure.ProductsNotFound)
    }


    private suspend fun updateLocalProductsFromApiIfNecessary(
        forceUpdate: Boolean,
        expirationTimeMillis: Long
    ): Failure? {
        //  TODO:   WorkManager to update local database, as it is an operation that must be completed
        //          If for example 10 products were needed to update: 7 updated on Monday and 3 on Tuesday,
        //          and the user opens the app on Wednesday and close before updating all the 10 products,
        //          resulting that 5 Monday products and 2 Tuesday products were updated, when the user
        //          opens the app again the lastProdUpdatedAt will be on Tuesday, so the remaining 2
        //          Monday products will now never been updated.
        //          It is therefore mandatory that the updates complete even if the user close the app, or
        //          sorting the products by lastUpdatedTime, so that the most recent update will always update
        //          the last.
        val localLastUpdatedAtInMillis = productsDao.getLastProdUpdatedAtInMillis() ?: 0
        if (forceUpdate || isDataExpired(
                lastUpdatedAtInMillis = localLastUpdatedAtInMillis,
                expirationTimeInMillis = expirationTimeMillis
            )
        ) {
            when (val productsNetwork =
                productsApi.getSortedProductsUpdatedAfter(localLastUpdatedAtInMillis.toTimestamp())) {
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