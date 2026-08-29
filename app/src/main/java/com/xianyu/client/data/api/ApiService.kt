package com.xianyu.client.data.api

import com.xianyu.client.data.model.*
import retrofit2.http.*

interface ApiService {

    // ---------- Auth ----------
    @POST("/api/v1/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("/api/v1/auth/verify")
    suspend fun verifyToken(): VerifyResponse

    @POST("/api/v1/auth/logout")
    suspend fun logout(): ApiResponse<Any>

    // ---------- System Control ----------
    @GET("/api/v1/system-control/status")
    suspend fun getServicesStatus(): ApiResponse<ServicesStatusData>

    @POST("/api/v1/system-control/restart/{key}")
    suspend fun restartService(@Path("key") key: String): ApiResponse<Map<String, String>>

    // ---------- Accounts ----------
    @GET("/api/v1/cookies/options")
    suspend fun getAccountOptions(): List<AccountOption>

    // ---------- Chat ----------
    @GET("/api/v1/chat-new/accounts")
    suspend fun getChatAccounts(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 50
    ): Map<String, @JvmSuppressWildcards Any>

    @POST("/api/v1/chat-new/connect/{accountId}")
    suspend fun connectChatAccount(@Path("accountId") accountId: String): ApiResponse<Any>

    @POST("/api/v1/chat-new/disconnect/{accountId}")
    suspend fun disconnectChatAccount(@Path("accountId") accountId: String): ApiResponse<Any>

    @GET("/api/v1/chat-new/conversations/{accountId}")
    suspend fun getConversations(
        @Path("accountId") accountId: String,
        @Query("limit") limit: Int = 30
    ): Map<String, @JvmSuppressWildcards Any>

    @GET("/api/v1/chat-new/messages/{accountId}/{cid}")
    suspend fun getMessages(
        @Path("accountId") accountId: String,
        @Path("cid") cid: String,
        @Query("limit") limit: Int = 50
    ): Map<String, @JvmSuppressWildcards Any>

    @POST("/api/v1/chat-new/send-message/{accountId}")
    suspend fun sendTextMessage(
        @Path("accountId") accountId: String,
        @Body body: Map<String, String>
    ): ApiResponse<Map<String, String>>

    // ---------- Items / Products ----------
    @GET("/api/v1/items/paginated")
    suspend fun getItemsPaginated(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("cookie_id") cookieId: String? = null,
        @Query("keyword") keyword: String? = null
    ): PaginatedItems

    // ---------- Cards ----------
    @GET("/api/v1/cards")
    suspend fun getCards(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("search") search: String? = null,
        @Query("type") type: String? = null
    ): CardPaginatedResult

    // ---------- Orders ----------
    @GET("/api/v1/orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("cookie_id") cookieId: String? = null,
        @Query("status") status: String? = null,
        @Query("search") search: String? = null
    ): OrderListResponse

    // ---------- Risk Control Logs ----------
    @GET("/api/v1/risk-control-logs")
    suspend fun getRiskLogs(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("cookie_id") cookieId: String? = null,
        @Query("processing_status") processingStatus: String? = null
    ): RiskLogListResponse
}
