package com.example.project1.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Lightweight, local-only app settings (dark mode, notifications, avatar color)
 * backed by SharedPreferences. These are device-level preferences and are not
 * synced to the backend.
 */
interface AppSettingsRepository {
    val darkModeEnabled: StateFlow<Boolean>
    val notificationsEnabled: StateFlow<Boolean>

    fun setDarkMode(enabled: Boolean)
    fun setNotifications(enabled: Boolean)

    /** Returns the saved avatar color swatch index (0-based) for the given account id. */
    fun getAvatarColorIndex(accountId: String): Int
    fun setAvatarColorIndex(accountId: String, index: Int)
}

class LocalAppSettingsRepository(context: Context) : AppSettingsRepository {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("eco_app_settings", Context.MODE_PRIVATE)

    private val _darkModeEnabled = MutableStateFlow(prefs.getBoolean(KEY_DARK_MODE, false))
    override val darkModeEnabled: StateFlow<Boolean> = _darkModeEnabled.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean(KEY_NOTIFICATIONS, true))
    override val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    override fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        _darkModeEnabled.value = enabled
    }

    override fun setNotifications(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
        _notificationsEnabled.value = enabled
    }

    override fun getAvatarColorIndex(accountId: String): Int {
        if (accountId.isBlank()) return 0
        return prefs.getInt(KEY_AVATAR_PREFIX + accountId, 0)
    }

    override fun setAvatarColorIndex(accountId: String, index: Int) {
        if (accountId.isBlank()) return
        prefs.edit().putInt(KEY_AVATAR_PREFIX + accountId, index).apply()
    }

    private companion object {
        const val KEY_DARK_MODE = "dark_mode_enabled"
        const val KEY_NOTIFICATIONS = "notifications_enabled"
        const val KEY_AVATAR_PREFIX = "avatar_color_index_"
    }
}