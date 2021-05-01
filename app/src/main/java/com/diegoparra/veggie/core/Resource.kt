package com.diegoparra.veggie.core

sealed class Resource<T>(
    val data: T? = null,
    val failure: Failure? = null
) {
    class Loading<T> : Resource<T>()
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(failure: Failure) : Resource<T>(failure = failure)
}