package com.VegaSolutions.lpptransit.lppapi.data

class ApiResponse<T>(
    val isSuccess: Boolean,
    val data: T,
    val message: String,
    val type: String
)
