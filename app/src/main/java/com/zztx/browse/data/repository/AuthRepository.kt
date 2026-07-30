package com.zztx.browse.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zztx.browse.data.entity.LoginRequest
import com.zztx.browse.data.entity.RegisterRequest
import com.zztx.browse.data.entity.User
import com.zztx.browse.data.network.ApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthRepository(private val dataStore: DataStore<Preferences>) {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")
    private val EXPIRES_AT_KEY = stringPreferencesKey("expires_at")

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY] != null
    }

    val currentUser: Flow<User?> = dataStore.data.map { preferences ->
        val token = preferences[TOKEN_KEY]
        if (token != null) {
            User(
                id = preferences[USER_ID_KEY] ?: "",
                email = preferences[USER_EMAIL_KEY] ?: "",
                name = preferences[USER_NAME_KEY] ?: "",
                avatar = preferences[USER_AVATAR_KEY],
                token = token,
                refreshToken = preferences[REFRESH_TOKEN_KEY] ?: "",
                expiresAt = preferences[EXPIRES_AT_KEY]?.toLong() ?: 0
            )
        } else {
            null
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = ApiClient.apiService.login(LoginRequest(email, password))
            if (response.success && response.user != null) {
                saveUser(response.user!!)
                Result.success(response.user!!)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, name: String): Result<User> {
        return try {
            val response = ApiClient.apiService.register(RegisterRequest(email, password, name))
            if (response.success && response.user != null) {
                saveUser(response.user!!)
                Result.success(response.user!!)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_EMAIL_KEY)
            preferences.remove(USER_NAME_KEY)
            preferences.remove(USER_AVATAR_KEY)
            preferences.remove(EXPIRES_AT_KEY)
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data.first()[TOKEN_KEY]
    }

    private suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = user.token
            preferences[REFRESH_TOKEN_KEY] = user.refreshToken
            preferences[USER_ID_KEY] = user.id
            preferences[USER_EMAIL_KEY] = user.email
            preferences[USER_NAME_KEY] = user.name
            preferences[USER_AVATAR_KEY] = user.avatar ?: ""
            preferences[EXPIRES_AT_KEY] = user.expiresAt.toString()
        }
    }
}