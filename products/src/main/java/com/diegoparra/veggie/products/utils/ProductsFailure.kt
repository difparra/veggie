package com.diegoparra.veggie.products.utils

import com.diegoparra.veggie.core.kotlin.Failure
import timber.log.Timber

sealed class ProductsFailure : Failure.FeatureFailure() {
    //  Create specific products failure here
}