package com.zerosepaisa.liferesetos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zerosepaisa.liferesetos.data.OnboardingPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = OnboardingPreferences(application)

    // null = still loading from disk, true/false = the real persisted value
    val isFirstLaunch: StateFlow<Boolean?> = preferences.isFirstLaunch
        .map<Boolean, Boolean?> { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun completeOnboarding() {
        viewModelScope.launch {
            preferences.completeOnboarding()
        }
    }
}