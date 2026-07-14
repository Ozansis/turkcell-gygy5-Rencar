package com.turkcell.rencar_pair.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")

private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

@Singleton
class TokenStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    // AuthInterceptor senkron çalıştığından DataStore'un Flow API'sini bekleyemez;
    // bu yüzden erişim token'ı bellekte de tutulur.
    private val cachedAccessToken = AtomicReference(
        runBlocking { readAccessTokenFromDisk() }
    )

    val accessToken: String? get() = cachedAccessToken.get()

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.tokenDataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
        cachedAccessToken.set(accessToken)
    }

    suspend fun readRefreshToken(): String? {
        return context.tokenDataStore.data
            .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { it[REFRESH_TOKEN_KEY] }
            .first()
    }

    suspend fun clear() {
        context.tokenDataStore.edit { it.clear() }
        cachedAccessToken.set(null)
    }

    private suspend fun readAccessTokenFromDisk(): String? {
        return context.tokenDataStore.data
            .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { it[ACCESS_TOKEN_KEY] }
            .first()
    }
}
