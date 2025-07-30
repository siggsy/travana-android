package com.VegaSolutions.lpptransit.data.lppapi

import arrow.core.Either
import io.ktor.http.HttpStatusCode

data class ApiError(
    val type: String,
    override val message: String
) : RuntimeException(message)

data class HttpError(
    val httpStatus: HttpStatusCode,
    override val message: String = "Request failed with status code: $httpStatus"
) : RuntimeException(message)
