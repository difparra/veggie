package com.diegoparra.veggie.products.data

import com.diegoparra.veggie.core.android.IoDispatcher
import com.diegoparra.veggie.core.kotlin.*
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
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject


class ProductsRepositoryImpl @Inject constructor(
    private val productsDao: ProductsDao,
    private val productsApi: ProductsApi,
    private val prefs: ProductPrefs,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductsRepository {


    /*
                TAGS RELATED        ----------------------------------------------------------------
     */

    override suspend fun getTags(source: Source): Either<Failure, List<Tag>> =
        withContext(dispatcher) {
            if (source.isDataExpired(prefs.getTagsUpdatedAt() ?: BasicTime(0))) {
                productsApi.getTags()
                    .map {
                        productsDao.updateAllTags(tags = it.map { it.toTagEntity() })
                        prefs.saveTagsUpdatedAt(BasicTime.now())
                    }
                    .onFailure {
                        Timber.e("Error while fetching tags from server (remoteConfig). Failure=${it}")
                        return@withContext Either.Left(it)
                    }
            }

            val tagsLocal = productsDao.getAllTags()
            return@withContext Either.Right(tagsLocal.map { it.toTag() })
        }

    /*
                PRODUCTS RELATED        ------------------------------------------------------------
     */

    override suspend fun getMainProductsByTagId(
        tagId: String,
        source: Source
    ): Either<Failure, List<Product>> = withContext(dispatcher) {
        updateLocalProductsIfExpired(source).onFailure {
            return@withContext Either.Left(it)
        }
        val productsLocal = productsDao.getMainProductsByTagId(tagId)
        return@withContext Either.Right(productsLocal.map { it.toProduct() })
    }

    override suspend fun searchMainProductsByName(
        query: String,
        source: Source
    ): Either<Failure, List<Product>> = withContext(dispatcher) {
        updateLocalProductsIfExpired(source).onFailure {
            return@withContext Either.Left(it)
        }
        val localSearchResults = productsDao.searchMainProdByName(query)
        return@withContext Either.Right(localSearchResults.map { it.toProduct() })
    }

    override suspend fun getVariationsByMainId(
        mainId: String,
        source: Source
    ): Either<Failure, List<VariationData>> = withContext(dispatcher) {
        updateLocalProductsIfExpired(source).onFailure {
            return@withContext Either.Left(it)
        }
        val variationsLocal = productsDao.getProductVariationsByMainId(mainId)
        return@withContext if (variationsLocal.isNullOrEmpty()) {
            Timber.wtf("getVariationsByMainId - No variations were found for product with mainId=$mainId")
            Either.Left(Failure.NotFound)
        } else {
            Either.Right(variationsLocal.map { it.toVariationData() })
        }
    }

    override suspend fun getProduct(
        mainId: String, varId: String,
        source: Source
    ): Either<Failure, Product> = withContext(dispatcher) {
        updateLocalProductsIfExpired(source).onFailure {
            return@withContext Either.Left(it)
        }
        val productLocal = productsDao.getProduct(mainId, varId)
        return@withContext if (productLocal == null) {
            Timber.wtf("No product was found with mainId=$mainId, varId=$varId")
            Either.Left(Failure.NotFound)
        } else {
            Either.Right(productLocal.toProduct())
        }
    }


    /*
            ----------------------------------------------------------------------------------------
                    HELPERS
            ----------------------------------------------------------------------------------------
     */

    /*  //  Some idea but still not sure how to get this working.

    private fun isInternetAvailable(): Boolean {
        return true
    }

    private suspend fun updateLocalProductsIfExpired2(source: Source): Either<Failure, Unit> {
        return if (isInternetAvailable()) {
            val productsUpdatedAt = prefs.getProductsUpdatedAt() ?: BasicTime(0)
            if (source.isDataExpired(productsUpdatedAt)) {
                updateLocalProducts().onSuccess { prefs.saveProductsUpdatedAt(BasicTime.now()) }
            } else {
                Either.Right(Unit)
            }
        } else {
            Either.Right(Unit)
        }
    }*/

    private suspend fun updateLocalProductsIfExpired(source: Source): Either<Failure, Unit> {
        val productsUpdatedAt = prefs.getProductsUpdatedAt() ?: BasicTime(0)
        return if (source.isDataExpired(productsUpdatedAt)) {
            updateLocalProducts()
                    .onSuccess { prefs.saveProductsUpdatedAt(BasicTime.now()) }
        } else {
            Either.Right(Unit)
        }
    }

    //  Update taking into consideration the lastUpdateTime stored in the local database, not the last query
    //  as if last query happens to be wrong, localLastUpdate in database will not.
    private suspend fun updateLocalProducts(): Either<Failure, Unit> {
        //  TODO:   Complete operation using workManager, as it is an operation that must be completed
        //  Will get the last time a product was actually updated rather than the last time firebase was successfully called.
        val actualProductsUpdatedAt = BasicTime(productsDao.getLastProdUpdatedAtInMillis() ?: 0)
        return productsApi.getProductsUpdatedAfter(actualProductsUpdatedAt.toTimestamp())
            .map {
                productsDao.updateProducts(
                    mainProdsIdToDelete = it.getMainProdIdsToDelete(),
                    prodsToUpdate = it.getListProdUpdateRoom()
                )
            }
    }

}