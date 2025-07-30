package com.VegaSolutions.lpptransit.data.lppapi.data

class ApiResponse<T>(
    val isSuccess: Boolean,
    val data: T,
    val message: String,
    val type: String
)
