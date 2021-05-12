package com.diegoparra.veggie.core

/**
 * To send the state of the data from the viewModel to the Ui.
 */
sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val failure: Failure) : Resource<T>()
}