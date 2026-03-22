package com.sahe.itera.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahe.itera.data.preferences.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {

    val darkTheme = repo.darkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val notificationsOn = repo.notificationsOn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun setDarkTheme(value: Boolean) {
        viewModelScope.launch { repo.setDarkTheme(value) }
    }

    fun setNotificationsOn(value: Boolean) {
        viewModelScope.launch { repo.setNotificationsOn(value) }
    }
}