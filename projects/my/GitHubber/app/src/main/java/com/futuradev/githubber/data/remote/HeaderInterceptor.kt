package com.futuradev.githubber.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HeaderInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        request = request.newBuilder()
            .header("Accept", "application/vnd.github.v3+json")
            .build()

        val moreHeaders = request.headers.newBuilder()
            .removeAll("Content-Type")
            .build()

        request = request.newBuilder().headers(moreHeaders).build()

        return chain.proceed(request)
    }
}