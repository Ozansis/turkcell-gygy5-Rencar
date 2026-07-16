package com.turkcell.rencar_pair.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding_prefs")

private val HAS_SEEN_ONBOARDING_KEY = booleanPreferencesKey("has_seen_onboarding")

@Singleton
class OnboardingPreferences @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    suspend fun hasSeenOnboarding(): Boolean {
        return context.onboardingDataStore.data
            .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
            .map { it[HAS_SEEN_ONBOARDING_KEY] ?: false }
            .first()
    }

    suspend fun setHasSeenOnboarding(value: Boolean) {
        context.onboardingDataStore.edit { prefs ->
            prefs[HAS_SEEN_ONBOARDING_KEY] = value
        }
    }
}
