package com.xianyu.client.data.api

import com.xianyu.client.data.model.*
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("/api/v1/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("/api/v1/auth/verify")
    suspend fun verifyToken(): VerifyResponse

    @POST("/api/v1/auth/logout")
    suspend fun logout(): ApiResponse<Any>

    // System Control
    @GET("/api/v1/system-control/status")
    suspend fun getServicesStatus(): ApiResponse<ServicesStatusData>

    @POST("/api/v1/system-control/restart/{key}")
    suspend fun restartService(@Path("key") key: String): ApiResponse<Map<String, String>>

    // Accounts / Cookies
    @GET("/api/v1/cookies/options")
    suspend fun getAccountOptions(): List<AccountOption>

    @GET("/api/v1/cookies")
    suspend fun getAccounts(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 50
    ): Any  // 实际返回可能是分页结构，先用 Any 再解析

    // 简单开关账号（根据实际后端接口调整）
    @PUT("/api/v1/cookies/{pk}/enabled")
    suspend fun setAccountEnabled(
        @Path("pk") pk: Int,
        @Body body: Map<String, Boolean>
    ): ApiResponse<Any>
}
