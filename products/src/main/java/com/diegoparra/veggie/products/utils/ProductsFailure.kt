package com.diegoparra.veggie.products.utils

import com.diegoparra.veggie.core.kotlin.Failure
import timber.log.Timber

sealed class ProductsFailure : Failure.FeatureFailure() {

    object EmptySearchQuery : com.diegoparra.veggie.products.utils.ProductsFailure()
    //  Created this failure to be managed in use case. It is actually a failure, in order to
    //  distinct from the case where search didn't throw results because no products were found.

    //  Empty lists should not be failures, or at least not failures to be managed in repositories.
    //  Returning an empty list from repository is totally fine, that would mean no items matches
    //  the query, but not that an error occurred.

}