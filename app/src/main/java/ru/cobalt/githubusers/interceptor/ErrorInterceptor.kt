package ru.cobalt.githubusers.interceptor

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import ru.cobalt.githubusers.model.Error

class ErrorInterceptor(
    private val gson: Gson,
) : Interceptor {

    var errorListener: ((Error) -> (Unit))? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (!response.isSuccessful) {
            val body = response.body()?.string()
            val error = gson.fromJson(body, Error::class.java)
            if (error != null) errorListener?.invoke(error)
        }
        return response
    }

}