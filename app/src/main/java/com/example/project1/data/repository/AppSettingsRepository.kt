package com.example.project1.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * Lightweight, local-only app settings (dark mode, notifications, avatar color,
 * profile photo) backed by SharedPreferences. These are device-level preferences
 * and are not synced to the backend.
 *
 * Note: dark mode is still used by the student-facing side of the app (see
 * MainActivity / user Profile screen). The admin profile screen no longer
 * exposes a dark mode toggle, but the underlying setting is kept here so the
 * rest of the app keeps working.
 */
interface AppSettingsRepository {
    val darkModeEnabled: StateFlow<Boolean>
    val notificationsEnabled: StateFlow<Boolean>

    fun setDarkMode(enabled: Boolean)
    fun setNotifications(enabled: Boolean)

    /** Returns the saved avatar color swatch index (0-based) for the given account id. */
    fun getAvatarColorIndex(accountId: String): Int
    fun setAvatarColorIndex(accountId: String, index: Int)

    /** Returns the local file path of the saved profile photo for the given account id, if any. */
    fun getProfilePhotoPath(accountId: String): String?

    /** Copies the picked image into local app storage and remembers it for the account. Returns the saved path. */
    fun saveProfilePhoto(accountId: String, sourceUri: Uri): String?

    /** Removes the saved profile photo for the given account id. */
    fun clearProfilePhoto(accountId: String)
}

class LocalAppSettingsRepository(context: Context) : AppSettingsRepository {

    private val appContext = context.applicationContext

    private val prefs: SharedPreferences =
        appContext.getSharedPreferences("eco_app_settings", Context.MODE_PRIVATE)

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

    override fun getProfilePhotoPath(accountId: String): String? {
        if (accountId.isBlank()) return null
        val path = prefs.getString(KEY_PROFILE_PHOTO_PREFIX + accountId, null) ?: return null
        return if (File(path).exists()) path else null
    }

    override fun saveProfilePhoto(accountId: String, sourceUri: Uri): String? {
        if (accountId.isBlank()) return null
        return try {
            val dir = File(appContext.filesDir, "profile_photos").apply { mkdirs() }
            val destFile = File(dir, "$accountId.jpg")
            appContext.contentResolver.openInputStream(sourceUri)?.use { input ->
                destFile.outputStream().use { output -> input.copyTo(output) }
            }
            prefs.edit().putString(KEY_PROFILE_PHOTO_PREFIX + accountId, destFile.absolutePath).apply()
            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    override fun clearProfilePhoto(accountId: String) {
        if (accountId.isBlank()) return
        val path = prefs.getString(KEY_PROFILE_PHOTO_PREFIX + accountId, null)
        if (path != null) {
            File(path).delete()
        }
        prefs.edit().remove(KEY_PROFILE_PHOTO_PREFIX + accountId).apply()
    }

    private companion object {
        const val KEY_DARK_MODE = "dark_mode_enabled"
        const val KEY_NOTIFICATIONS = "notifications_enabled"
        const val KEY_AVATAR_PREFIX = "avatar_color_index_"
        const val KEY_PROFILE_PHOTO_PREFIX = "profile_photo_path_"
    }
}