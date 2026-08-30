package com.xianyu.client.data.api

import com.xianyu.client.data.model.*
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {

    @POST("/api/v1/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("/api/v1/auth/verify")
    suspend fun verifyToken(): VerifyResponse

    
    @GET("/api/v1/geetest/register")
    suspend fun geetestRegister(): Map<String, @JvmSuppressWildcards Any>

    @POST("/api/v1/geetest/validate")
    suspend fun geetestValidate(@Body body: Map<String, String>): Map<String, @JvmSuppressWildcards Any>

    @POST("/api/v1/auth/logout")
    suspend fun logout(): ApiResponse<EmptyData>

    @GET("/api/v1/system-control/status")
    suspend fun getServicesStatus(): ApiResponse<ServicesStatusData>

    @POST("/api/v1/system-control/restart/{key}")
    suspend fun restartService(@Path("key") key: String): ApiResponse<Map<String, String>>

    @GET("/api/v1/cookies/options")
    suspend fun getAccountOptions(): List<AccountOption>

    // Chat - use ResponseBody and parse in UI to avoid complex generics
    @GET("/api/v1/chat-new/accounts")
    suspend fun getChatAccountsRaw(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 50
    ): ResponseBody

    @POST("/api/v1/chat-new/connect/{accountId}")
    suspend fun connectChatAccount(@Path("accountId") accountId: String): ApiResponse<EmptyData>

    @POST("/api/v1/chat-new/disconnect/{accountId}")
    suspend fun disconnectChatAccount(@Path("accountId") accountId: String): ApiResponse<EmptyData>

    @GET("/api/v1/chat-new/conversations/{accountId}")
    suspend fun getConversationsRaw(
        @Path("accountId") accountId: String,
        @Query("limit") limit: Int = 30
    ): ResponseBody

    @GET("/api/v1/chat-new/messages/{accountId}/{cid}")
    suspend fun getMessagesRaw(
        @Path("accountId") accountId: String,
        @Path("cid") cid: String,
        @Query("limit") limit: Int = 50
    ): ResponseBody

    @POST("/api/v1/chat-new/send-message/{accountId}")
    suspend fun sendTextMessage(
        @Path("accountId") accountId: String,
        @Body body: Map<String, String>
    ): ApiResponse<Map<String, String>>

    @GET("/api/v1/items/paginated")
    suspend fun getItemsPaginated(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("cookie_id") cookieId: String? = null,
        @Query("keyword") keyword: String? = null
    ): PaginatedItems

    @GET("/api/v1/cards")
    suspend fun getCards(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("search") search: String? = null,
        @Query("type") type: String? = null
    ): CardPaginatedResult

    @GET("/api/v1/orders")
    suspend fun getOrders(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("cookie_id") cookieId: String? = null,
        @Query("status") status: String? = null,
        @Query("search") search: String? = null
    ): OrderListResponse

    @GET("/api/v1/risk-control-logs")
    suspend fun getRiskLogs(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("cookie_id") cookieId: String? = null,
        @Query("processing_status") processingStatus: String? = null
    ): RiskLogListResponse
}
