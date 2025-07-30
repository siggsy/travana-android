package com.VegaSolutions.lpptransit.data.lppapi

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.RealResponseBody
import okio.GzipSource
import okio.buffer
import java.io.IOException

class GzipInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val body = response.body ?: return response
        if (response.header("Content-Encoding") != "gzip") return response

        val contentLength = body.contentLength()
        val responseBody = GzipSource(body.source())
        val strippedHeaders = response.headers.newBuilder().build()
        return response.newBuilder().headers(strippedHeaders)
            .body(
                RealResponseBody(
                    body.contentType().toString(),
                    contentLength,
                    responseBody.buffer()
                )
            )
            .build()
    }

}
