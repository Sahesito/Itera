package com.sahe.itera.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "itera_settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val DARK_THEME         = booleanPreferencesKey("dark_theme")
    private val NOTIFICATIONS_ON   = booleanPreferencesKey("notifications_on")

    val darkTheme = context.dataStore.data
        .map { it[DARK_THEME] ?: false }

    val notificationsOn = context.dataStore.data
        .map { it[NOTIFICATIONS_ON] ?: true }

    suspend fun setDarkTheme(value: Boolean) {
        context.dataStore.edit { it[DARK_THEME] = value }
    }

    suspend fun setNotificationsOn(value: Boolean) {
        context.dataStore.edit { it[NOTIFICATIONS_ON] = value }
    }
}