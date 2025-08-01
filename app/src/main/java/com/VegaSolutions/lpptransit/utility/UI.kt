package com.VegaSolutions.lpptransit.utility

sealed class Resource<T> {
    class Loading<T>() : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    class Error<T>() : Resource<T>()
}