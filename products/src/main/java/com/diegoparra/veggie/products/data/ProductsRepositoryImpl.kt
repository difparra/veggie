package com.diegoparra.veggie.products.data

import android.content.Context
import com.diegoparra.veggie.core.android.IoDispatcher
import com.diegoparra.veggie.core.android.LocalUpdateHelper
import com.diegoparra.veggie.core.internet.IsInternetAvailableUseCase
import com.diegoparra.veggie.core.kotlin.*
import com.diegoparra.veggie.products.data.firebase.ProductsApi
import com.diegoparra.veggie.products.data.prefs.ProductPrefs
import com.diegoparra.veggie.products.data.room.ProductEntitiesTransformations.toProduct
import com.diegoparra.veggie.products.data.room.ProductEntitiesTransformations.toTag
import com.diegoparra.veggie.products.data.room.ProductEntitiesTransformations.toVariationData
import com.diegoparra.veggie.products.data.room.ProductsDao
import com.diegoparra.veggie.products.data.DtoToEntityTransformations.toProductUpdateRoom
import com.diegoparra.veggie.products.data.DtoToEntityTransformations.toTagEntity
import com.diegoparra.veggie.products.data.firebase.ProductDto
import com.diegoparra.veggie.products.data.room.ProductUpdateRoom
import com.diegoparra.veggie.products.domain.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject


class ProductsRepositoryImpl @Inject constructor(
    private val productsDao: ProductsDao,
    private val productsApi: ProductsApi,
    private val prefs: ProductPrefs,
    @ApplicationContext private val context: Context,
    private val isInternetAvailableUseCase: IsInternetAvailableUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductsRepository {

    /*
                TAGS RELATED        ----------------------------------------------------------------
     */

    override suspend fun getTags(source: Source): Either<Failure, List<Tag>> =
        withContext(dispatcher) {
            if (source.mustFetchFromServer(
                    lastSuccessfulFetch = prefs.getTagsFetchAt() ?: BasicTime(0),
                    isInternetAvailable = isInternetAvailableUseCase.invoke().first()
                )
            ) {
                productsApi.getTags()
                    .map {
                        Timber.d("Tags collected from server: $it")
                        productsDao.updateAllTags(tags = it.map { it.toTagEntity() })
                    }
                    .onSuccess {
                        Timber.d("Tags successfully updated in local database")
                        prefs.saveTagsFetchAt(BasicTime.now())
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
        localUpdateHelper.update(source).onFailure {
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
        localUpdateHelper.update(source).onFailure {
            return@withContext Either.Left(it)
        }
        val localSearchResults = productsDao.searchMainProdByName(query)
        return@withContext Either.Right(localSearchResults.map { it.toProduct() })
    }

    override suspend fun getVariationsByMainId(
        mainId: String,
        source: Source
    ): Either<Failure, List<VariationData>> = withContext(dispatcher) {
        localUpdateHelper.update(source).onFailure {
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
        localUpdateHelper.update(source).onFailure {
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

    private val localUpdateHelper = LocalUpdateHelper(
        lastSuccessfulFetchPrefs = LocalUpdateHelper.TimePrefsImpl(key = "products_updated_at", context = context),
        room = productsDao,
        serverApi = productsApi,
        mapper = object : LocalUpdateHelper.Mapper<ProductDto, ProductUpdateRoom> {
            override fun mapToEntity(dto: ProductDto): ProductUpdateRoom = dto.toProductUpdateRoom()
            override fun isDtoDeleted(dto: ProductDto): Boolean = dto.deleted
            override fun getId(dto: ProductDto): String = dto.mainId
        },
        isInternetAvailableUseCase = isInternetAvailableUseCase
    )

}