package com.example.project1.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
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
    val notificationsEnabled: StateFlow<Boolean>

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

    /** Returns the local file path of the saved profile background/cover photo for the given account id, if any. */
    fun getBackgroundPhotoPath(accountId: String): String?

    /** Copies the picked image into local app storage as the profile background/cover photo. Returns the saved path. */
    fun saveBackgroundPhoto(accountId: String, sourceUri: Uri): String?

    /** Removes the saved profile background/cover photo for the given account id. */
    fun clearBackgroundPhoto(accountId: String)

    /** Returns the set of milestone ids for which this account has already claimed the bonus-point reward. */
    fun getClaimedMilestones(accountId: String): Set<String>

    /** Marks a milestone reward as claimed for this account, so it cannot be claimed again. */
    fun markMilestoneClaimed(accountId: String, milestoneId: String)

    fun getCollectedBadges(accountId: String): Set<String>
    fun markBadgeCollected(accountId: String, badgeId: String)

    fun getShowcaseBadgeId(accountId: String): String?
    fun setShowcaseBadgeId(accountId: String, badgeId: String?)

    fun getDailyQuestDate(accountId: String): String?
    fun getDailyQuestId(accountId: String): String?
    fun markDailyQuestCompleted(accountId: String, date: String, questId: String)
}

class LocalAppSettingsRepository(context: Context) : AppSettingsRepository {

    private val appContext = context.applicationContext

    private val prefs: SharedPreferences =
        appContext.getSharedPreferences("eco_app_settings", Context.MODE_PRIVATE)

    private val _notificationsEnabled =
        MutableStateFlow(prefs.getBoolean(KEY_NOTIFICATIONS, true))

    override val notificationsEnabled: StateFlow<Boolean> =
        _notificationsEnabled.asStateFlow()

    override fun setNotifications(enabled: Boolean) {
        prefs.edit {
            putBoolean(KEY_NOTIFICATIONS, enabled)
        }

        _notificationsEnabled.value = enabled
    }

    override fun getAvatarColorIndex(accountId: String): Int {
        if (accountId.isBlank()) return 0

        return prefs.getInt(
            KEY_AVATAR_PREFIX + accountId,
            0
        )
    }

    override fun setAvatarColorIndex(accountId: String, index: Int) {
        if (accountId.isBlank()) return

        prefs.edit {
            putInt(
                KEY_AVATAR_PREFIX + accountId,
                index
            )
        }
    }

    override fun getProfilePhotoPath(accountId: String): String? {
        if (accountId.isBlank()) return null

        val path = prefs.getString(
            KEY_PROFILE_PHOTO_PREFIX + accountId,
            null
        ) ?: return null

        return if (File(path).exists()) path else null
    }

    override fun saveProfilePhoto(
        accountId: String,
        sourceUri: Uri
    ): String? {
        if (accountId.isBlank()) return null

        return try {
            val dir = File(
                appContext.filesDir,
                "profile_photos"
            ).apply {
                mkdirs()
            }

            val destFile = File(
                dir,
                "$accountId.jpg"
            )

            appContext.contentResolver
                .openInputStream(sourceUri)
                ?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                ?: return null

            prefs.edit {
                putString(
                    KEY_PROFILE_PHOTO_PREFIX + accountId,
                    destFile.absolutePath
                )
            }

            destFile.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    override fun clearProfilePhoto(accountId: String) {
        if (accountId.isBlank()) return

        val path = prefs.getString(
            KEY_PROFILE_PHOTO_PREFIX + accountId,
            null
        )

        if (path != null) {
            File(path).delete()
        }

        prefs.edit {
            remove(KEY_PROFILE_PHOTO_PREFIX + accountId)
        }
    }

    override fun getBackgroundPhotoPath(accountId: String): String? {
        if (accountId.isBlank()) return null

        val path = prefs.getString(
            KEY_BACKGROUND_PHOTO_PREFIX + accountId,
            null
        ) ?: return null

        return if (File(path).exists()) path else null
    }

    override fun saveBackgroundPhoto(
        accountId: String,
        sourceUri: Uri
    ): String? {
        if (accountId.isBlank()) return null

        return try {
            val dir = File(
                appContext.filesDir,
                "background_photos"
            ).apply {
                mkdirs()
            }

            val destFile = File(
                dir,
                "$accountId.jpg"
            )

            appContext.contentResolver
                .openInputStream(sourceUri)
                ?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                ?: return null

            prefs.edit {
                putString(
                    KEY_BACKGROUND_PHOTO_PREFIX + accountId,
                    destFile.absolutePath
                )
            }

            destFile.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    override fun clearBackgroundPhoto(accountId: String) {
        if (accountId.isBlank()) return

        val path = prefs.getString(
            KEY_BACKGROUND_PHOTO_PREFIX + accountId,
            null
        )

        if (path != null) {
            File(path).delete()
        }

        prefs.edit {
            remove(KEY_BACKGROUND_PHOTO_PREFIX + accountId)
        }
    }

    override fun getClaimedMilestones(accountId: String): Set<String> {
        if (accountId.isBlank()) return emptySet()
        return prefs.getStringSet(KEY_CLAIMED_MILESTONES_PREFIX + accountId, emptySet()).orEmpty()
    }

    override fun markMilestoneClaimed(accountId: String, milestoneId: String) {
        if (accountId.isBlank()) return
        val key = KEY_CLAIMED_MILESTONES_PREFIX + accountId
        val updated = prefs.getStringSet(key, emptySet()).orEmpty().toMutableSet().apply { add(milestoneId) }
        prefs.edit {
            putStringSet(key, updated)
        }
    }

    override fun getCollectedBadges(accountId: String): Set<String> {
        if (accountId.isBlank()) return emptySet()
        return prefs.getStringSet(KEY_COLLECTED_BADGES_PREFIX + accountId, emptySet()).orEmpty()
    }

    override fun markBadgeCollected(accountId: String, badgeId: String) {
        if (accountId.isBlank()) return
        val key = KEY_COLLECTED_BADGES_PREFIX + accountId
        val updated = prefs.getStringSet(key, emptySet()).orEmpty().toMutableSet().apply { add(badgeId) }
        prefs.edit { putStringSet(key, updated) }
    }

    override fun getShowcaseBadgeId(accountId: String): String? {
        if (accountId.isBlank()) return null
        return prefs.getString(KEY_SHOWCASE_BADGE_PREFIX + accountId, null)
    }

    override fun setShowcaseBadgeId(accountId: String, badgeId: String?) {
        if (accountId.isBlank()) return
        prefs.edit {
            if (badgeId.isNullOrBlank()) remove(KEY_SHOWCASE_BADGE_PREFIX + accountId)
            else putString(KEY_SHOWCASE_BADGE_PREFIX + accountId, badgeId)
        }
    }

    override fun getDailyQuestDate(accountId: String): String? {
        if (accountId.isBlank()) return null
        return prefs.getString(KEY_DAILY_QUEST_DATE_PREFIX + accountId, null)
    }

    override fun getDailyQuestId(accountId: String): String? {
        if (accountId.isBlank()) return null
        return prefs.getString(KEY_DAILY_QUEST_ID_PREFIX + accountId, null)
    }

    override fun markDailyQuestCompleted(accountId: String, date: String, questId: String) {
        if (accountId.isBlank()) return
        prefs.edit {
            putString(KEY_DAILY_QUEST_DATE_PREFIX + accountId, date)
            putString(KEY_DAILY_QUEST_ID_PREFIX + accountId, questId)
        }
    }

    private companion object {
        const val KEY_DARK_MODE = "dark_mode_enabled"
        const val KEY_NOTIFICATIONS = "notifications_enabled"
        const val KEY_AVATAR_PREFIX = "avatar_color_index_"
        const val KEY_PROFILE_PHOTO_PREFIX = "profile_photo_path_"
        const val KEY_BACKGROUND_PHOTO_PREFIX = "background_photo_path_"
        const val KEY_CLAIMED_MILESTONES_PREFIX = "claimed_milestones_"
        const val KEY_COLLECTED_BADGES_PREFIX = "collected_badges_"
        const val KEY_SHOWCASE_BADGE_PREFIX = "showcase_badge_"
        const val KEY_DAILY_QUEST_DATE_PREFIX = "daily_quest_date_"
        const val KEY_DAILY_QUEST_ID_PREFIX = "daily_quest_id_"
    }
}