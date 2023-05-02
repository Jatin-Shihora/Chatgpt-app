package com.jatin.template.common.net

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * network request
 *
 * @author Jatin
 * @time 14/7/2022
 */
object Network {
    private val mClient: OkHttpClient by lazy {
//        val cacheDir = File(getProjectCachePath(App.instance), "okhttp")
//        val mCache = Cache(cacheDir, 8 * 1024 * 1024)
//        val httpLoggingInterceptor =
//            HttpLoggingInterceptor { message: String -> Log.d("Network", message) }
//        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        OkHttpClient.Builder()
            .connectTimeout(99999, TimeUnit.SECONDS)
            // Add an interceptor to all requests
//            .addInterceptor(Interceptor { chain -> // get our request
//                val original = chain.request()
//                // re-build
//                val builder: Request.Builder = original.newBuilder()
//                //  builder.addHeader("Content-Type", "application/json")
//                val newRequest: Request = builder.build()
//                // return
//                chain.proceed(newRequest)
//            })
//            .addInterceptor(httpLoggingInterceptor)
//            .cache(mCache)
            .build()

    }

    fun getClient(): OkHttpClient {
        return mClient
    }
}