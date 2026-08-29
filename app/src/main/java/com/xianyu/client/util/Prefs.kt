package com.xianyu.client.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "xianyu_prefs")

class Prefs(private val context: Context) {

    companion object {
        private val KEY_BASE_URL = stringPreferencesKey("base_url")
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_USERNAME = stringPreferencesKey("username")
    }

    val baseUrlFlow: Flow<String> = context.dataStore.data.map { it[KEY_BASE_URL] ?: "" }
    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val usernameFlow: Flow<String?> = context.dataStore.data.map { it[KEY_USERNAME] }

    suspend fun saveBaseUrl(url: String) {
        context.dataStore.edit { it[KEY_BASE_URL] = url }
    }

    suspend fun saveAuth(token: String, refreshToken: String?, username: String?) {
        context.dataStore.edit {
            it[KEY_TOKEN] = token
            if (refreshToken != null) it[KEY_REFRESH_TOKEN] = refreshToken
            if (username != null) it[KEY_USERNAME] = username
        }
    }

    suspend fun clearAuth() {
        context.dataStore.edit {
            it.remove(KEY_TOKEN)
            it.remove(KEY_REFRESH_TOKEN)
            it.remove(KEY_USERNAME)
        }
    }

    suspend fun getBaseUrl(): String {
        return context.dataStore.data.map { it[KEY_BASE_URL] ?: "" }.first()
    }

    suspend fun getToken(): String? {
        return context.dataStore.data.map { it[KEY_TOKEN] }.first()
    }
}
