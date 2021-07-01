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
        Timber.d("getMainProductsByTagId called with tagId=$tagId, source=$source")
        updateLocalProductsIfExpired(source).onFailure {
            Timber.d("getMainProductsByTagId - Failure while updatingProducts")
            return@withContext Either.Left(it)
        }
        Timber.d("getMainProductsByTagId - productsUpdated. Now getting local products...")
        val productsLocal = productsDao.getMainProductsByTagId(tagId)
        Timber.d("getMainProductsByTagId - productsLocal = $productsLocal")
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

    private suspend fun updateLocalProductsIfExpired(source: Source): Either<Failure, Unit> {
        Timber.d("updateLocalProductsIfExpired called with source = $source")
        val productsUpdatedAt = prefs.getProductsUpdatedAt() ?: BasicTime(0)
        Timber.d("updateLocalProductsIfExpired - productsUpdatedAt = $productsUpdatedAt")
        return if (source.isDataExpired(productsUpdatedAt)) {
            Timber.d("updateLocalProductsIfExpired - Data is expired. Calling to update...")
            updateLocalProducts()
                    .onSuccess { prefs.saveProductsUpdatedAt(BasicTime.now()) }
        } else {
            Timber.d("updateLocalProductsIfExpired - Data is updated. Returning...")
            Either.Right(Unit)
        }
    }

    //  Still not sure if update with workManager is the best idea, it could avoid some reads to
    //  database in case user closed the app while updating local database, but the most time is
    //  taken to fetch products from remote, and if they haven't been fetched reads will not count
    //  (more than one). As local database is fast, it is not likely that the user close the app
    //  just while updating, so it is not a big worry, and workManager may be not necessary.
    //  On the other hand, to implement workManager, it must be considered that workManager does not
    //  always work immediately, so it may not be the most proper useCase as I need to know the
    //  products immediately to display in app.
    private suspend fun updateLocalProducts(): Either<Failure, Unit> {
        //  Will get the last time a product was actually updated rather than the last time firebase was successfully called.
        Timber.d("updateLocalProducts called!")
        val actualProductsUpdatedAt = BasicTime(productsDao.getLastProdUpdatedAtInMillis() ?: 0)
        Timber.d("updateLocalProducts - actualProductsUpdatedAt = $actualProductsUpdatedAt")
        return productsApi.getProductsUpdatedAfter(actualProductsUpdatedAt.toTimestamp())
            .map {
                Timber.d("updateLocalProducts - Fetch from firebase was successfult. Products = $it")
                productsDao.updateProducts(
                    mainProdsIdToDelete = it.getMainProdIdsToDelete(),
                    prodsToUpdate = it.getListProdUpdateRoom()
                )
                Timber.d("updateLocalProducts - ProductsUpdated!")
            }
    }

}