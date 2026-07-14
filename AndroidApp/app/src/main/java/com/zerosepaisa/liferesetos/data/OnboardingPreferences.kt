package com.zerosepaisa.liferesetos.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "life_reset_prefs")

class OnboardingPreferences(private val context: Context) {

    private val isFirstLaunchKey = booleanPreferencesKey("is_first_launch")

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[isFirstLaunchKey] ?: true
    }

    suspend fun completeOnboarding() {
        context.dataStore.edit { prefs ->
            prefs[isFirstLaunchKey] = false
        }
    }
}