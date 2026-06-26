package com.cloutgrid.androidapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.cloutgrid.androidapp.data.model.EmptyResponse
import com.cloutgrid.androidapp.data.model.LoginResponse
import com.cloutgrid.androidapp.data.model.UserContainer
import com.cloutgrid.androidapp.data.network.APIService
import dagger.Lazy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "auth_preferences"
)

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: Lazy<APIService>
) {
    private val dataStore = context.authDataStore
    private val json = Json { ignoreUnknownKeys = true }

    private object PreferencesKeys {
        val ACCESS_TOKEN = stringPreferencesKey("access")
        val REFRESH_TOKEN = stringPreferencesKey("refresh")
        val USER_DATA = stringPreferencesKey("user")
        val USER_TYPE = stringPreferencesKey("type")
    }

    val access: Flow<String?> = dataStore.data.map { it[PreferencesKeys.ACCESS_TOKEN] }
    val refresh: Flow<String?> = dataStore.data.map { it[PreferencesKeys.REFRESH_TOKEN] }
    val type: Flow<String> = dataStore.data.map { it[PreferencesKeys.USER_TYPE] ?: "creator" }

    val isAuth: Flow<Boolean> = access.map { !it.isNullOrEmpty() }

    val user: Flow<UserContainer?> = dataStore.data.map { prefs ->
        val userJson = prefs[PreferencesKeys.USER_DATA] ?: return@map null
        try {
            json.decodeFromString<UserContainer>(userJson)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun login(email: String, password: String, userType: String) {
        val response: LoginResponse = apiService.get().request(
            endpoint = "/login/$userType/",
            method = "POST",
            body = mapOf("email" to email, "password" to password),
            requireAuth = false
        )

        saveSession(response, userType)
    }

    // 🚀 NEW LOGOUT METHOD
    suspend fun logout() {
        val refreshToken = refresh.first() ?: ""
        try {
            apiService.get().request<EmptyResponse>(
                endpoint = "/logout/",
                method = "POST",
                body = mapOf("refresh" to refreshToken),
                requireAuth = true
            )
        } catch (e: Exception) {
            // Log network errors if needed, but we ALWAYS clear local credentials
            // so the user isn't locked into a broken session state.
        } finally {
            clearSession()
        }
    }

    suspend fun saveSession(response: LoginResponse, userType: String) {
        val userJson = json.encodeToString(response.user)
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.ACCESS_TOKEN] = response.access
            prefs[PreferencesKeys.REFRESH_TOKEN] = response.refresh
            prefs[PreferencesKeys.USER_DATA] = userJson
            prefs[PreferencesKeys.USER_TYPE] = userType
        }
    }

    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    suspend fun updateTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.ACCESS_TOKEN] = accessToken
            prefs[PreferencesKeys.REFRESH_TOKEN] = refreshToken
        }
    }
}