package com.example.project1.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

// interface for managing local app settings and user preferences
interface AppSettingsRepository {
    val notificationsEnabled: StateFlow<Boolean>

    fun setNotifications(enabled: Boolean)

    // get saved avatar color swatch index for user
    fun getAvatarColorIndex(accountId: String): Int
    // save avatar color swatch index for user
    fun setAvatarColorIndex(accountId: String, index: Int)

    // get local file path of saved profile photo
    fun getProfilePhotoPath(accountId: String): String?

    // copy picked image into local storage as profile photo
    fun saveProfilePhoto(accountId: String, sourceUri: Uri): String?

    // remove saved profile photo file and path
    fun clearProfilePhoto(accountId: String)

    // get local file path of saved background photo
    fun getBackgroundPhotoPath(accountId: String): String?

    // copy picked image into local storage as background photo
    fun saveBackgroundPhoto(accountId: String, sourceUri: Uri): String?

    // remove saved background photo file and path
    fun clearBackgroundPhoto(accountId: String)

    // get set of claimed milestone IDs for user
    fun getClaimedMilestones(accountId: String): Set<String>

    // mark milestone reward as claimed for user
    fun markMilestoneClaimed(accountId: String, milestoneId: String)

    // get set of collected badge IDs for user
    fun getCollectedBadges(accountId: String): Set<String>
    // mark badge as collected for user
    fun markBadgeCollected(accountId: String, badgeId: String)

    // get badge ID selected to showcase on profile
    fun getShowcaseBadgeId(accountId: String): String?
    // set or clear showcased badge ID for profile
    fun setShowcaseBadgeId(accountId: String, badgeId: String?)

    // get date string of last completed daily quest
    fun getDailyQuestDate(accountId: String): String?
    // get ID of last completed daily quest
    fun getDailyQuestId(accountId: String): String?
    // mark daily quest as completed for date and ID
    fun markDailyQuestCompleted(accountId: String, date: String, questId: String)

    // get timestamp of last seen approved task notification
    fun getLastSeenApprovedTaskAt(accountId: String): Long
    // save timestamp of last seen approved task notification
    fun setLastSeenApprovedTaskAt(accountId: String, at: Long)
}

// local implementation of app settings repository using shared preferences
class LocalAppSettingsRepository(context: Context) : AppSettingsRepository {

    private val appContext = context.applicationContext

    private val prefs: SharedPreferences =
        appContext.getSharedPreferences("eco_app_settings", Context.MODE_PRIVATE)

    private val _notificationsEnabled =
        MutableStateFlow(prefs.getBoolean(KEY_NOTIFICATIONS, true))

    override val notificationsEnabled: StateFlow<Boolean> =
        _notificationsEnabled.asStateFlow()

    // save notification preference state
    override fun setNotifications(enabled: Boolean) {
        prefs.edit {
            putBoolean(KEY_NOTIFICATIONS, enabled)
        }

        _notificationsEnabled.value = enabled
    }

    // get selected avatar color index for user
    override fun getAvatarColorIndex(accountId: String): Int {
        if (accountId.isBlank()) return 0

        return prefs.getInt(
            KEY_AVATAR_PREFIX + accountId,
            0
        )
    }

    // save selected avatar color index for user
    override fun setAvatarColorIndex(accountId: String, index: Int) {
        if (accountId.isBlank()) return

        prefs.edit {
            putInt(
                KEY_AVATAR_PREFIX + accountId,
                index
            )
        }
    }

    // get local file path of saved profile photo
    override fun getProfilePhotoPath(accountId: String): String? {
        if (accountId.isBlank()) return null

        val path = prefs.getString(
            KEY_PROFILE_PHOTO_PREFIX + accountId,
            null
        ) ?: return null

        return if (File(path).exists()) path else null
    }

    // copy picked photo into app storage and save path
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

    // delete profile photo file and clear saved path
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

    // get local file path of saved background photo
    override fun getBackgroundPhotoPath(accountId: String): String? {
        if (accountId.isBlank()) return null

        val path = prefs.getString(
            KEY_BACKGROUND_PHOTO_PREFIX + accountId,
            null
        ) ?: return null

        return if (File(path).exists()) path else null
    }

    // copy picked cover photo into app storage and save path
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

    // delete background photo file and clear saved path
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

    // get set of claimed milestone IDs for user
    override fun getClaimedMilestones(accountId: String): Set<String> {
        if (accountId.isBlank()) return emptySet()
        return prefs.getStringSet(KEY_CLAIMED_MILESTONES_PREFIX + accountId, emptySet()).orEmpty()
    }

    // save milestone ID as claimed for user
    override fun markMilestoneClaimed(accountId: String, milestoneId: String) {
        if (accountId.isBlank()) return
        val key = KEY_CLAIMED_MILESTONES_PREFIX + accountId
        val updated = prefs.getStringSet(key, emptySet()).orEmpty().toMutableSet().apply { add(milestoneId) }
        prefs.edit {
            putStringSet(key, updated)
        }
    }

    // get set of collected badge IDs for user
    override fun getCollectedBadges(accountId: String): Set<String> {
        if (accountId.isBlank()) return emptySet()
        return prefs.getStringSet(KEY_COLLECTED_BADGES_PREFIX + accountId, emptySet()).orEmpty()
    }

    // save badge ID as collected for user
    override fun markBadgeCollected(accountId: String, badgeId: String) {
        if (accountId.isBlank()) return
        val key = KEY_COLLECTED_BADGES_PREFIX + accountId
        val updated = prefs.getStringSet(key, emptySet()).orEmpty().toMutableSet().apply { add(badgeId) }
        prefs.edit { putStringSet(key, updated) }
    }

    // get badge ID selected to showcase on profile
    override fun getShowcaseBadgeId(accountId: String): String? {
        if (accountId.isBlank()) return null
        return prefs.getString(KEY_SHOWCASE_BADGE_PREFIX + accountId, null)
    }

    // set or clear showcased badge ID for profile
    override fun setShowcaseBadgeId(accountId: String, badgeId: String?) {
        if (accountId.isBlank()) return
        prefs.edit {
            if (badgeId.isNullOrBlank()) remove(KEY_SHOWCASE_BADGE_PREFIX + accountId)
            else putString(KEY_SHOWCASE_BADGE_PREFIX + accountId, badgeId)
        }
    }

    // get date string of last completed daily quest
    override fun getDailyQuestDate(accountId: String): String? {
        if (accountId.isBlank()) return null
        return prefs.getString(KEY_DAILY_QUEST_DATE_PREFIX + accountId, null)
    }

    // get ID of last completed daily quest
    override fun getDailyQuestId(accountId: String): String? {
        if (accountId.isBlank()) return null
        return prefs.getString(KEY_DAILY_QUEST_ID_PREFIX + accountId, null)
    }

    // save daily quest completion date and quest ID
    override fun markDailyQuestCompleted(accountId: String, date: String, questId: String) {
        if (accountId.isBlank()) return
        prefs.edit {
            putString(KEY_DAILY_QUEST_DATE_PREFIX + accountId, date)
            putString(KEY_DAILY_QUEST_ID_PREFIX + accountId, questId)
        }
    }

    // get timestamp of last seen approved task notification
    override fun getLastSeenApprovedTaskAt(accountId: String): Long {
        if (accountId.isBlank()) return 0L
        val key = KEY_LAST_SEEN_APPROVED_TASK_PREFIX + accountId
        if (!prefs.contains(key)) {
            val now = System.currentTimeMillis()
            prefs.edit { putLong(key, now) }
            return now
        }
        return prefs.getLong(key, 0L)
    }

    // save timestamp of last seen approved task notification
    override fun setLastSeenApprovedTaskAt(accountId: String, at: Long) {
        if (accountId.isBlank()) return
        prefs.edit {
            putLong(KEY_LAST_SEEN_APPROVED_TASK_PREFIX + accountId, at)
        }
    }

    // shared preference key definitions
    private companion object {
        const val KEY_NOTIFICATIONS = "notifications_enabled"
        const val KEY_AVATAR_PREFIX = "avatar_color_index_"
        const val KEY_PROFILE_PHOTO_PREFIX = "profile_photo_path_"
        const val KEY_BACKGROUND_PHOTO_PREFIX = "background_photo_path_"
        const val KEY_CLAIMED_MILESTONES_PREFIX = "claimed_milestones_"
        const val KEY_COLLECTED_BADGES_PREFIX = "collected_badges_"
        const val KEY_SHOWCASE_BADGE_PREFIX = "showcase_badge_"
        const val KEY_DAILY_QUEST_DATE_PREFIX = "daily_quest_date_"
        const val KEY_DAILY_QUEST_ID_PREFIX = "daily_quest_id_"
        const val KEY_LAST_SEEN_APPROVED_TASK_PREFIX = "last_seen_approved_task_"
    }
}