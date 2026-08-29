package com.xianyu.client.data.model

import com.google.gson.annotations.SerializedName

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
data class ApiResponse<T>(
    val success: Boolean = true,
    val message: String? = null,
    val data: T? = null
)

// ========== System Control ==========
data class ServiceStatusItem(
    val key: String,
    val label: String,
    val port: Int,
    val online: Boolean
)

data class ServicesStatusData(
    val runtime: String? = null,
    val services: List<ServiceStatusItem> = emptyList()
)

// ========== Account ==========
data class AccountOption(
    val pk: Int,
    val id: String,
    val enabled: Boolean,
    val remark: String? = null,
    @SerializedName("show_browser") val showBrowser: Boolean? = null,
    val online: Boolean? = null
)

data class AccountDetail(
    val pk: Int? = null,
    val id: String,
    val enabled: Boolean = false,
    val online: Boolean? = null,
    @SerializedName("auto_confirm") val autoConfirm: Boolean? = null,
    val remark: String? = null,
    val note: String? = null,
    @SerializedName("disable_reason") val disableReason: String? = null,
    @SerializedName("owner_username") val ownerUsername: String? = null,
    @SerializedName("use_ai_reply") val useAiReply: Boolean? = null
)
