package com.example.project1.ui.users.profile

import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.UserEntity
import java.util.Calendar

// represents a membership level tier based on total points
data class MemberTier(
    val name: String,
    val level: Int,
    val totalLevels: Int = 5,
    val minPoints: Int,
    val nextThreshold: Int?
) {
    // calculates progress towards the next tier threshold
    fun progress(points: Int): Float {
        val cap = nextThreshold ?: return 1f
        val span = (cap - minPoints).coerceAtLeast(1)
        return (points - minPoints).coerceIn(0, span).toFloat() / span.toFloat()
    }
}

// represents an unlockable achievement badge
data class EcoBadge(
    val id: String,
    val title: String,
    val description: String,
    val unlocked: Boolean,
    val current: Int,
    val goal: Int
) {
    val progress: Float get() = if (goal <= 0) 1f else (current.toFloat() / goal).coerceIn(0f, 1f)
    val progressLabel: String get() = if (unlocked) "Unlocked" else "$current/$goal"
}

// represents a quick daily eco challenge
data class DailyEcoQuest(
    val id: String,
    val title: String,
    val hint: String
)

// list of available daily eco quests
fun dailyEcoQuests(): List<DailyEcoQuest> = listOf(
    DailyEcoQuest("bottle", "Reusable bottle", "Skip a disposable cup or bottle today."),
    DailyEcoQuest("recycle", "Sort recycling", "Put at least one item in the right bin."),
    DailyEcoQuest("walk", "Walk a short trip", "Skip a vehicle for a nearby destination.")
)

// picks today's quest cyclically based on the day of the year
fun todaysEcoQuest(now: Long = System.currentTimeMillis()): DailyEcoQuest {
    val quests = dailyEcoQuests()
    val dayOfYear = Calendar.getInstance().apply { timeInMillis = now }.get(Calendar.DAY_OF_YEAR)
    return quests[dayOfYear % quests.size]
}

// finds the closest locked badge challenge to focus on
fun nextBadgeChallenge(badges: List<EcoBadge>): EcoBadge? =
    badges.filter { !it.unlocked }.maxByOrNull { it.progress }

// represents a major milestone achievement with bonus rewards
data class EcoMilestone(
    val id: String,
    val title: String,
    val bonusPoints: Int,
    val current: Int,
    val goal: Int,
    val detail: String
) {
    val progress: Float get() = if (goal <= 0) 0f else (current.toFloat() / goal).coerceIn(0f, 1f)
    val locked: Boolean get() = current < goal
}

// represents a personalized green goal
data class EcoGoal(
    val title: String,
    val current: Int,
    val target: Int,
    val unit: String
) {
    val progress: Float get() = if (target <= 0) 0f else (current.toFloat() / target).coerceIn(0f, 1f)
    val completed: Boolean get() = current >= target
}

// aggregates profile stats for display
data class EcoProfileStats(
    val approvedActions: Int = 0,
    val submittedActions: Int = 0,
    val completedTasks: Int = 0,
    val monthlyPoints: Int = 0,
    val currentStreak: Int = 0,
    val campusRank: Int = 0,
    val campusTotal: Int = 0,
    val weeklyActivity: List<Int> = List(7) { 0 },
    val weeklyPoints: List<Int> = List(7) { 0 },
    val weeklyLabels: List<String> = listOf("M", "T", "W", "T", "F", "S", "S"),
    val weeklyDays: List<WeeklyDayActivity> = emptyList()
)

enum class WeeklyActivitySource { Submission, Task }

// individual activity entry for weekly logs
data class WeeklyActivityEntry(
    val source: WeeklyActivitySource,
    val title: String,
    val subtitle: String,
    val points: Int,
    val timestamp: Long
)

// summary of user activities for a specific day of the week
data class WeeklyDayActivity(
    val dayIndex: Int,
    val shortLabel: String,
    val fullLabel: String,
    val entries: List<WeeklyActivityEntry>
) {
    val actionCount: Int get() = entries.size
    val totalPoints: Int get() = entries.sumOf { it.points }
    val submissionCount: Int get() = entries.count { it.source == WeeklyActivitySource.Submission }
    val taskCount: Int get() = entries.count { it.source == WeeklyActivitySource.Task }
}

// determines the appropriate membership tier for given points
fun memberTierFor(points: Int): MemberTier {
    val tiers = listOf(
        MemberTier("Seedling", 1, minPoints = 0, nextThreshold = 100),
        MemberTier("Sprout", 2, minPoints = 100, nextThreshold = 300),
        MemberTier("Earth Guardian", 3, minPoints = 300, nextThreshold = 800),
        MemberTier("Forest Keeper", 4, minPoints = 800, nextThreshold = 1500),
        MemberTier("Eco Legend", 5, minPoints = 1500, nextThreshold = null)
    )
    return tiers.last { points >= it.minPoints }
}

// generates the list of badges and their current unlocked states
fun badgesFor(
    points: Int,
    plasticsSaved: Int,
    stats: EcoProfileStats = EcoProfileStats()
): List<EcoBadge> = listOf(
    EcoBadge("leaf_guardian", "Leaf Guardian", "Earn 50 eco points from approved actions.", points >= 50, points, 50),
    EcoBadge("water_saver", "Water Saver", "Save 5 plastic items through your eco actions.", plasticsSaved >= 5, plasticsSaved, 5),
    EcoBadge("pedal_power", "Pedal Power", "Reach 200 eco points.", points >= 200, points, 200),
    EcoBadge("forest_friend", "Forest Friend", "Reach 800 eco points.", points >= 800, points, 800),
    EcoBadge("streak_7", "7-Day Streak", "Log an approved eco action 7 days in a row.", stats.currentStreak >= 7, stats.currentStreak, 7),
    EcoBadge("campus_hero", "Campus Hero", "Reach 3,000 eco points.", points >= 3000, points, 3000),
    EcoBadge("task_master", "Task Master", "Complete 10 approved eco tasks.", stats.completedTasks >= 10, stats.completedTasks, 10),
    EcoBadge("green_regular", "Green Regular", "Get 20 eco actions approved in total.", stats.approvedActions >= 20, stats.approvedActions, 20)
)

/** Friendly illustrative impact equivalents, not a scientific carbon calculator. */
data class EcoImpact(
    val co2GramsSaved: Int,
    val treesEquivalent: Double,
    val waterLitersSaved: Int
)

// calculates estimated environmental impact equivalents
fun impactFor(points: Int, plasticsSaved: Int): EcoImpact {
    val co2Grams = plasticsSaved * 80 + points * 5
    val trees = co2Grams / 21_000.0
    val waterLiters = plasticsSaved * 3
    return EcoImpact(co2Grams, trees, waterLiters)
}

// generates milestones based on points and plastics saved
fun milestonesFor(points: Int, plasticsSaved: Int): List<EcoMilestone> = listOf(
    EcoMilestone("tree_planter", "Tree Planter", 30, plasticsSaved.coerceAtMost(20), 20, "$plasticsSaved/20 plastics saved"),
    EcoMilestone("solar_explorer", "Solar Explorer", 50, points.coerceAtMost(500), 500, "$points/500 points"),
    EcoMilestone("recycle_master", "Recycle Master", 80, plasticsSaved.coerceAtMost(100), 100, "$plasticsSaved/100 items"),
    EcoMilestone("zero_waste_advocate", "Zero Waste Advocate", 150, points.coerceAtMost(1500), 1500, "$points/1500 points")
)

// generates profile goals and progress
fun goalsFor(points: Int, plasticsSaved: Int, stats: EcoProfileStats): List<EcoGoal> = listOf(
    EcoGoal("Save 50 plastic items", plasticsSaved, 50, "items"),
    EcoGoal("Reach 3,000 eco points", points, 3000, "points"),
    EcoGoal("Complete 30 eco actions", stats.approvedActions, 30, "actions")
)

// builds comprehensive profile stats by processing user submissions, tasks, and leaderboard data
fun buildEcoProfileStats(
    currentUser: UserEntity?,
    submissions: List<EcoSubmissionEntity>,
    tasks: List<TaskEntity>,
    allUsers: List<UserEntity>,
    now: Long = System.currentTimeMillis()
): EcoProfileStats {
    val approvedSubmissions = submissions.filter { it.status.equals("Approved", ignoreCase = true) }
    val approvedTasks = tasks.filter { it.status.equals("Approved", ignoreCase = true) }
    val approvedActions = approvedSubmissions.size + approvedTasks.size
    val submittedActions = submissions.size + tasks.size
    val completedTasks = approvedTasks.size

    val events = buildList {
        approvedSubmissions.forEach { add(it.reviewTimestamp ?: it.timestamp) }
        approvedTasks.forEach { add(it.reviewTimestamp ?: it.timestamp) }
    }

    // calculates points earned within the last 30 days
    val thirtyDaysAgo = now - 30L * 24 * 60 * 60 * 1000
    val monthlyPoints =
        approvedSubmissions.filter { (it.reviewTimestamp ?: it.timestamp) >= thirtyDaysAgo }.sumOf { it.points } +
                approvedTasks.filter { (it.reviewTimestamp ?: it.timestamp) >= thirtyDaysAgo }.sumOf { it.points }

    // determines the user's campus rank from the leaderboard
    val rank = currentUser?.let { user ->
        allUsers
            .sortedByDescending { it.totalPoints }
            .indexOfFirst { it.studentId == user.studentId }
            .takeIf { it >= 0 }
            ?.plus(1)
            ?: 0
    } ?: 0

    val weekStart = startOfDay(now) - 6L * 24 * 60 * 60 * 1000
    val shortLabels = buildWeekLabels(now)
    val fullLabels = buildWeekFullLabels(now)
    val dayBuckets = List(7) { mutableListOf<WeeklyActivityEntry>() }

    fun dayIndexFor(timestamp: Long): Int =
        ((startOfDay(timestamp) - weekStart) / (24L * 60 * 60 * 1000)).toInt()

    // populate approved submissions into weekly buckets
    approvedSubmissions.forEach { submission ->
        val timestamp = submission.reviewTimestamp ?: submission.timestamp
        val dayIndex = dayIndexFor(timestamp)
        if (dayIndex in 0..6) {
            dayBuckets[dayIndex] += WeeklyActivityEntry(
                source = WeeklyActivitySource.Submission,
                title = submission.actionType,
                subtitle = submission.stallName.ifBlank { "Eco submission" },
                points = submission.points,
                timestamp = timestamp
            )
        }
    }
    // populate approved tasks into weekly buckets
    approvedTasks.forEach { task ->
        val timestamp = task.reviewTimestamp ?: task.timestamp
        val dayIndex = dayIndexFor(timestamp)
        if (dayIndex in 0..6) {
            dayBuckets[dayIndex] += WeeklyActivityEntry(
                source = WeeklyActivitySource.Task,
                title = task.title,
                subtitle = "Eco task",
                points = task.points,
                timestamp = timestamp
            )
        }
    }

    val weeklyDays = dayBuckets.mapIndexed { index, entries ->
        WeeklyDayActivity(
            dayIndex = index,
            shortLabel = shortLabels.getOrElse(index) { "" },
            fullLabel = fullLabels.getOrElse(index) { shortLabels.getOrElse(index) { "" } },
            entries = entries.sortedByDescending { it.timestamp }
        )
    }

    return EcoProfileStats(
        approvedActions = approvedActions,
        submittedActions = submittedActions,
        completedTasks = completedTasks,
        monthlyPoints = monthlyPoints,
        currentStreak = calculateCurrentStreak(events, now),
        campusRank = rank,
        campusTotal = allUsers.size,
        weeklyActivity = weeklyDays.map { it.actionCount },
        weeklyPoints = weeklyDays.map { it.totalPoints },
        weeklyLabels = shortLabels,
        weeklyDays = weeklyDays
    )
}

// helper to normalize timestamp to the start of the day (00:00:00)
private fun startOfDay(timestamp: Long): Long {
    val c = Calendar.getInstance().apply { timeInMillis = timestamp }
    c.set(Calendar.HOUR_OF_DAY, 0)
    c.set(Calendar.MINUTE, 0)
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MILLISECOND, 0)
    return c.timeInMillis
}

// calculates the current consecutive day activity streak
private fun calculateCurrentStreak(events: List<Long>, now: Long): Int {
    if (events.isEmpty()) return 0
    val eventDays = events.map(::startOfDay).toSet()
    var cursor = startOfDay(now)
    if (cursor !in eventDays) {
        cursor -= 24L * 60 * 60 * 1000
    }

    var streak = 0
    while (cursor in eventDays) {
        streak++
        cursor -= 24L * 60 * 60 * 1000
    }
    return streak
}

// formats full date labels for the past week
private fun buildWeekFullLabels(now: Long): List<String> {
    val labels = mutableListOf<String>()
    val format = java.text.SimpleDateFormat("EEEE, d MMM", java.util.Locale.getDefault())
    for (offset in 6 downTo 0) {
        val c = Calendar.getInstance().apply {
            timeInMillis = now - offset * 24L * 60 * 60 * 1000
        }
        labels += format.format(c.time)
    }
    return labels
}

// formats short single-letter day labels for the past week
private fun buildWeekLabels(now: Long): List<String> {
    val labels = mutableListOf<String>()
    val format = java.text.SimpleDateFormat("EEEEE", java.util.Locale.getDefault())
    for (offset in 6 downTo 0) {
        val c = Calendar.getInstance().apply {
            timeInMillis = now - offset * 24L * 60 * 60 * 1000
        }
        labels += format.format(c.time).take(1).uppercase()
    }
    return labels
}