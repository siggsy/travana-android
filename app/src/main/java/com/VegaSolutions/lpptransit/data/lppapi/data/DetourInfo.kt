package com.VegaSolutions.lpptransit.data.lppapi.data

data class DetourInfo(
    val title: String,
    val date: String,
    val moreDataUrl: String? = null,
    val content: String? = null,
    val photoUrl: String? = null
)
