package com.VegaSolutions.lpptransit.data.lppapi.data

data class ApiResponse<T>(
    val success: Boolean?,
    val data: T,
    val message: String,
    val type: String
)
