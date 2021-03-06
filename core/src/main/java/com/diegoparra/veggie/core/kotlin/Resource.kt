package com.diegoparra.veggie.core.kotlin

/**
 * Used as a wrapper to set state on data going from viewModel to Ui.
 */
sealed class Resource<out T> {
    object Loading: Resource<Nothing>()
    class Success<T>(val data: T) : Resource<T>() {
        override fun toString(): String {
            return "{ Resource.Success / data = $data }"
        }
    }
    class Error<T>(val failure: Failure) : Resource<T>(){
        override fun toString(): String {
            return "{ Resource.Failure / failure = $failure }"
        }
    }
}