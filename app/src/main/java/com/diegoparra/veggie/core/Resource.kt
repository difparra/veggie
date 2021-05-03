package com.diegoparra.veggie.core

sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val failure: Failure) : Resource<T>()
}