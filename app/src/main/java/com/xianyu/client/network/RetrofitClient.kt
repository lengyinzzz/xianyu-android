package com.xianyu.client.network

import com.xianyu.client.data.api.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    @Volatile
    private var baseUrl: String = ""

    @Volatile
    private var token: String? = null

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
        token?.let {
            builder.header("Authorization", "Bearer $it")
        }
        chain.proceed(builder.build())
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Volatile
    private var retrofit: Retrofit? = null

    fun setBaseUrl(url: String) {
        val normalized = url.trim().removeSuffix("/") + "/"
        if (normalized != baseUrl) {
            baseUrl = normalized
            retrofit = null
        }
    }

    fun setToken(newToken: String?) {
        token = newToken
    }

    fun getToken(): String? = token

    fun getBaseUrl(): String = baseUrl

    fun api(): ApiService {
        if (baseUrl.isBlank()) {
            throw IllegalStateException("请先设置服务器地址")
        }
        val r = retrofit ?: Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(buildClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .also { retrofit = it }
        return r.create(ApiService::class.java)
    }
}
