package com.diegoparra.veggie.core

/**
 * Used as a wrapper to set state on data going from viewModel to Ui.
 */
sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val failure: Failure) : Resource<T>()
}