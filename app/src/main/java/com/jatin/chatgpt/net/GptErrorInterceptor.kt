package com.jatin.chatgpt.net

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Handle the gpt service error code so that the entity can obtain the error message
 *
 * @author Jatin
 * @time 30/04/2023
 */
class GptErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        var response = chain.proceed(request)
        if (response.code == 401 || response.code == 429 || response.code == 500) {
            val json = response.body!!.string()
            response = Response.Builder().code(200).message("OK").request(request)
                .protocol(response.protocol)
                .body(json.toResponseBody("application/json".toMediaTypeOrNull())).build()
        }
        return response
    }
}