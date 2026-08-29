package com.xianyu.client.data.model

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement

// ========== Auth ==========
data class LoginRequest(
    val username: String? = null,
    val password: String? = null,
    val email: String? = null
)

data class LoginResponse(
    val success: Boolean = false,
    val message: String? = null,
    val token: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("user_id") val userId: Int? = null,
    val username: String? = null,
    @SerializedName("is_admin") val isAdmin: Boolean? = null,
    @SerializedName("account_limit") val accountLimit: Int? = null
)

data class VerifyResponse(
    val authenticated: Boolean = false,
    @SerializedName("user_id") val userId: Int? = null,
    val username: String? = null,
    @SerializedName("is_admin") val isAdmin: Boolean? = null,
    @SerializedName("account_limit") val accountLimit: Int? = null
)

// ========== Common ==========
class EmptyData

data class ApiResponse<T>(
    val success: Boolean = true,
    val message: String? = null,
    val data: T? = null,
    val total: Int? = null
)

// ========== System Control ==========
data class ServiceStatusItem(
    val key: String = "",
    val label: String = "",
    val port: Int = 0,
    val online: Boolean = false
)

data class ServicesStatusData(
    val runtime: String? = null,
    val services: List<ServiceStatusItem> = emptyList()
)

// ========== Account ==========
data class AccountOption(
    val pk: Int = 0,
    val id: String = "",
    val enabled: Boolean = false,
    val remark: String? = null,
    @SerializedName("show_browser") val showBrowser: Boolean? = null,
    val online: Boolean? = null
)

// ========== Chat ==========
data class ChatAccount(
    @SerializedName("account_id") val accountId: String = "",
    @SerializedName("display_name") val displayName: String? = null,
    val remark: String? = null,
    val connected: Boolean = false,
    val status: String? = null,
    val owner: String? = null
)

data class Conversation(
    val cid: String = "",
    val rawCid: String? = null,
    val otherUserId: String? = null,
    val otherUserName: String? = null,
    val otherUserAvatar: String? = null,
    val itemTitle: String? = null,
    val lastMessageSummary: String? = null,
    val lastMessageTime: Long = 0,
    val unreadCount: Int = 0
)

data class ChatMessage(
    val messageId: String? = null,
    val senderId: String? = null,
    val senderName: String? = null,
    val isSelf: Boolean = false,
    val type: String? = null,
    val text: String? = null,
    val images: List<String>? = null,
    val time: Long = 0,
    val failed: Boolean? = null,
    val failReason: String? = null
)

data class SendMessageBody(
    val cid: String,
    val text: String
)

// ========== Items / Products ==========
data class ItemData(
    val id: String? = null,
    @SerializedName("item_id") val itemId: String? = null,
    @SerializedName("cookie_id") val cookieId: String? = null,
    val title: String? = null,
    @SerializedName("item_title") val itemTitle: String? = null,
    val price: String? = null,
    @SerializedName("item_price") val itemPrice: String? = null,
    @SerializedName("is_polished") val isPolished: Boolean? = null,
    @SerializedName("has_card") val hasCard: Boolean? = null,
    @SerializedName("default_reply_enabled") val defaultReplyEnabled: Boolean? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class PaginatedItems(
    val success: Boolean = true,
    val data: List<ItemData> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 20,
    @SerializedName("total_pages") val totalPages: Int = 0
)

// ========== Cards ==========
data class CardData(
    val id: Int? = null,
    val name: String = "",
    val type: String? = null,
    val description: String? = null,
    val enabled: Boolean? = null,
    @SerializedName("delay_seconds") val delaySeconds: Int? = null,
    val price: String? = null,
    @SerializedName("item_id") val itemId: String? = null,
    @SerializedName("text_content") val textContent: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class CardPaginatedResult(
    val list: List<CardData> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 20,
    @SerializedName("total_pages") val totalPages: Int = 0
)

// ========== Orders ==========
data class OrderData(
    val id: String? = null,
    @SerializedName("order_id") val orderId: String? = null,
    @SerializedName("order_no") val orderNo: String? = null,
    @SerializedName("cookie_id") val cookieId: String? = null,
    @SerializedName("item_id") val itemId: String? = null,
    @SerializedName("item_title") val itemTitle: String? = null,
    @SerializedName("buyer_id") val buyerId: String? = null,
    @SerializedName("buyer_fish_nick") val buyerFishNick: String? = null,
    val quantity: Int? = null,
    val amount: String? = null,
    val status: String? = null,
    @SerializedName("delivery_method") val deliveryMethod: String? = null,
    @SerializedName("is_rated") val isRated: Boolean? = null,
    @SerializedName("is_bargain") val isBargain: Boolean? = null,
    @SerializedName("receiver_name") val receiverName: String? = null,
    @SerializedName("receiver_phone") val receiverPhone: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("placed_at") val placedAt: String? = null
)

data class OrderListResponse(
    val success: Boolean = true,
    val data: List<OrderData> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 20,
    @SerializedName("total_pages") val totalPages: Int = 0
)

// ========== Risk Control Logs ==========
data class RiskLogItem(
    val id: Int? = null,
    @SerializedName("cookie_id") val cookieId: String? = null,
    @SerializedName("account_id") val accountId: String? = null,
    @SerializedName("call_type") val callType: String? = null,
    @SerializedName("call_user") val callUser: String? = null,
    @SerializedName("processing_status") val processingStatus: String? = null,
    val message: String? = null,
    val detail: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    val success: Boolean? = null
)

data class RiskLogListResponse(
    val success: Boolean = true,
    val data: List<RiskLogItem> = emptyList(),
    val total: Int = 0,
    val limit: Int = 20,
    val offset: Int = 0,
    val message: String? = null
)
