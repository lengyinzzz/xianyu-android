package com.xianyu.client.data.model

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement

// ========== Auth ==========
data class LoginRequest(
    val username: String? = null,
    val password: String? = null,
    val email: String? = null,
    @SerializedName("geetest_challenge") val geetestChallenge: String? = null,
    @SerializedName("geetest_validate") val geetestValidate: String? = null,
    @SerializedName("geetest_seccode") val geetestSeccode: String? = null
)

data class GeetestResult(
    val challenge: String,
    val validate: String,
    val seccode: String
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


// ========== Dashboard Stats ==========
data class AccountStats(
    @SerializedName("total_accounts") val totalAccounts: Int = 0,
    @SerializedName("active_accounts") val activeAccounts: Int = 0,
    @SerializedName("total_keywords") val totalKeywords: Int = 0,
    @SerializedName("total_orders") val totalOrders: Int = 0,
    @SerializedName("today_reply_count") val todayReplyCount: Int = 0,
    @SerializedName("yesterday_reply_count") val yesterdayReplyCount: Int = 0,
    @SerializedName("account_limit") val accountLimit: Int? = null,
    @SerializedName("used_account_count") val usedAccountCount: Int = 0,
    @SerializedName("remaining_account_count") val remainingAccountCount: Int? = null
)

data class OrderTrendData(
    val trend: List<OrderTrendItem> = emptyList()
)

data class OrderTrendItem(
    val date: String = "",
    val amount: Double = 0.0,
    val count: Int = 0
)

data class BatchRateRequest(
    @SerializedName("account_ids") val accountIds: List<String>
)

data class BatchRateResponse(
    val success: Boolean = false,
    val message: String? = null,
    val data: BatchRateData? = null
)

data class BatchRateData(
    @SerializedName("total_rated") val totalRated: Int = 0,
    @SerializedName("total_failed") val totalFailed: Int = 0,
    @SerializedName("success_accounts") val successAccounts: Int = 0,
    @SerializedName("total_accounts") val totalAccounts: Int = 0
)

// ========== Account Detail ==========
data class AccountDetail(
    val pk: Int = 0,
    val id: String = "",
    val value: String? = null,
    val enabled: Boolean = false,
    @SerializedName("auto_confirm") val autoConfirm: Boolean = false,
    @SerializedName("scheduled_redelivery") val scheduledRedelivery: Boolean = false,
    @SerializedName("scheduled_rate") val scheduledRate: Boolean = false,
    @SerializedName("auto_polish") val autoPolish: Boolean = false,
    @SerializedName("confirm_before_send") val confirmBeforeSend: Boolean = false,
    @SerializedName("send_before_confirm") val sendBeforeConfirm: Boolean = false,
    @SerializedName("only_send_card") val onlySendCard: Boolean = false,
    @SerializedName("auto_red_flower") val autoRedFlower: Boolean = false,
    @SerializedName("ai_reply_block_ordered_users") val aiReplyBlockOrderedUsers: Boolean = false,
    @SerializedName("delivery_disabled") val deliveryDisabled: Boolean = false,
    @SerializedName("delivery_disabled_reason") val deliveryDisabledReason: String? = null,
    val remark: String? = null,
    @SerializedName("pause_duration") val pauseDuration: Int? = null,
    @SerializedName("message_expire_time") val messageExpireTime: Int? = null,
    @SerializedName("reply_delay_seconds") val replyDelaySeconds: Int? = null,
    val username: String? = null,
    @SerializedName("disable_reason") val disableReason: String? = null,
    @SerializedName("filter_count") val filterCount: Int = 0
)

data class AccountStatusUpdate(
    val enabled: Boolean
)

// ========== Keywords ==========
data class KeywordDetail(
    val id: String? = null,
    val keyword: String = "",
    val reply: String = "",
    @SerializedName("item_id") val itemId: String? = null,
    val type: String = "text",
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("item_title") val itemTitle: String? = null,
    @SerializedName("account_id") val accountId: String? = null
)

// ========== Blacklist ==========
data class BlacklistItem(
    val id: Int = 0,
    @SerializedName("owner_id") val ownerId: Int? = null,
    @SerializedName("account_id") val accountId: String? = null,
    @SerializedName("buyer_id") val buyerId: String? = null,
    @SerializedName("buyer_nick") val buyerNick: String? = null,
    @SerializedName("item_id") val itemId: String? = null,
    val reason: String? = null,
    @SerializedName("is_enabled") val isEnabled: Boolean = true,
    @SerializedName("created_at") val createdAt: String? = null
)

data class BlacklistListResponse(
    val success: Boolean = true,
    val data: List<BlacklistItem> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 20
)

data class CreateBlacklistRequest(
    @SerializedName("account_id") val accountId: String? = null,
    @SerializedName("buyer_ids") val buyerIds: String,
    @SerializedName("item_id") val itemId: String? = null,
    val reason: String? = null,
    @SerializedName("is_enabled") val isEnabled: Boolean = true
)

data class ToggleBlacklistRequest(
    @SerializedName("is_enabled") val isEnabled: Boolean
)

// ========== Auto Reply Logs ==========
data class ReplyLogItem(
    val id: Int? = null,
    @SerializedName("account_id") val accountId: String? = null,
    @SerializedName("account_name") val accountName: String? = null,
    @SerializedName("item_id") val itemId: String? = null,
    @SerializedName("item_title") val itemTitle: String? = null,
    @SerializedName("sender_user_id") val senderUserId: String? = null,
    @SerializedName("sender_user_name") val senderUserName: String? = null,
    @SerializedName("source_message") val sourceMessage: String? = null,
    @SerializedName("reply_text") val replyText: String? = null,
    @SerializedName("matched_rule_type") val matchedRuleType: String? = null,
    @SerializedName("matched_keyword") val matchedKeyword: String? = null,
    @SerializedName("reply_strategy") val replyStrategy: String? = null,
    @SerializedName("send_status") val sendStatus: String? = null,
    @SerializedName("send_fail_reason") val sendFailReason: String? = null,
    @SerializedName("error_message") val errorMessage: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class ReplyLogListResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: List<ReplyLogItem> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 20,
    @SerializedName("total_pages") val totalPages: Int = 0
)

// ========== Quick Phrases ==========
data class QuickPhrase(
    val id: Int = 0,
    val content: String = "",
    val title: String? = null,
    @SerializedName("sort_order") val sortOrder: Int = 0,
    @SerializedName("account_id") val accountId: String? = null
)

data class QuickPhraseListResponse(
    val success: Boolean = true,
    val data: List<QuickPhrase> = emptyList(),
    val message: String? = null
)

// ========== Announcements ==========
data class AnnouncementItem(
    val id: Int = 0,
    val title: String? = null,
    val content: String? = null,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class AnnouncementDataWrapper(
    val items: List<AnnouncementItem> = emptyList()
)

data class AnnouncementListResponse(
    val success: Boolean = true,
    val data: AnnouncementDataWrapper? = null,
    val message: String? = null
)
