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

    
    @GET("/api/v1/cookies/stats")
    suspend fun getAccountStats(): ApiResponse<AccountStats>

    @GET("/api/v1/cookies/stats/order-trend")
    suspend fun getOrderTrend(): ApiResponse<OrderTrendData>

    @POST("/api/v1/auto-rate/batch-rate")
    suspend fun batchRateOrders(@Body body: BatchRateRequest): BatchRateResponse

    @GET("/api/v1/risk-control-logs")
    suspend fun getRiskLogs(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("cookie_id") cookieId: String? = null,
        @Query("processing_status") processingStatus: String? = null
    ): RiskLogListResponse

    // ========== Accounts ==========
    @GET("/api/v1/cookies/details")
    suspend fun getAccountDetails(): List<AccountDetail>

    @PUT("/api/v1/cookies/{accountId}/status")
    suspend fun updateAccountStatus(
        @Path("accountId") accountId: String,
        @Body body: AccountStatusUpdate
    ): ApiResponse<EmptyData>

    // ========== Keywords ==========
    @GET("/api/v1/keywords-with-item-id")
    suspend fun getAllKeywords(): List<KeywordDetail>

    @GET("/api/v1/keywords-with-item-id/{accountId}")
    suspend fun getKeywordsByAccount(@Path("accountId") accountId: String): List<KeywordDetail>

    // ========== Blacklist ==========
    @GET("/api/v1/blacklist/personal")
    suspend fun getPersonalBlacklist(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("buyer_id") buyerId: String? = null,
        @Query("buyer_nick") buyerNick: String? = null
    ): BlacklistListResponse

    @POST("/api/v1/blacklist/personal")
    suspend fun createPersonalBlacklist(@Body body: CreateBlacklistRequest): ApiResponse<EmptyData>

    @DELETE("/api/v1/blacklist/personal/{recordId}")
    suspend fun deletePersonalBlacklist(@Path("recordId") recordId: Int): ApiResponse<EmptyData>

    @PATCH("/api/v1/blacklist/personal/{recordId}/toggle")
    suspend fun togglePersonalBlacklist(
        @Path("recordId") recordId: Int,
        @Body body: ToggleBlacklistRequest
    ): ApiResponse<EmptyData>

    // ========== Auto Reply Logs ==========
    @GET("/api/v1/auto-reply-logs")
    suspend fun getAutoReplyLogs(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("account_id") accountId: String? = null,
        @Query("send_status") sendStatus: String? = null,
        @Query("message_type") messageType: String = "auto_reply"
    ): ReplyLogListResponse

    // ========== Quick Phrases ==========
    @GET("/api/v1/chat-new/quick-phrases")
    suspend fun getQuickPhrases(): QuickPhraseListResponse

    // ========== Announcements ==========
    @GET("/api/v1/announcements/public")
    suspend fun getPublicAnnouncements(): AnnouncementListResponse
}
