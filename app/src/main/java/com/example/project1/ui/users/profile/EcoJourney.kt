package com.example.project1.ui.users.profile

import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.UserEntity
import java.util.Calendar

data class MemberTier(
    val name: String,
    val level: Int,
    val totalLevels: Int = 5,
    val minPoints: Int,
    val nextThreshold: Int?
) {
    fun progress(points: Int): Float {
        val cap = nextThreshold ?: return 1f
        val span = (cap - minPoints).coerceAtLeast(1)
        return (points - minPoints).coerceIn(0, span).toFloat() / span.toFloat()
    }
}

data class EcoBadge(
    val title: String,
    val unlocked: Boolean
)

data class EcoMilestone(
    val title: String,
    val current: Int,
    val goal: Int,
    val detail: String
) {
    val progress: Float get() = if (goal <= 0) 0f else (current.toFloat() / goal).coerceIn(0f, 1f)
    val locked: Boolean get() = current < goal
}

data class EcoGoal(
    val title: String,
    val current: Int,
    val target: Int,
    val unit: String
) {
    val progress: Float get() = if (target <= 0) 0f else (current.toFloat() / target).coerceIn(0f, 1f)
    val completed: Boolean get() = current >= target
}

data class EcoProfileStats(
    val approvedActions: Int = 0,
    val submittedActions: Int = 0,
    val completedTasks: Int = 0,
    val monthlyPoints: Int = 0,
    val currentStreak: Int = 0,
    val campusRank: Int = 0,
    val campusTotal: Int = 0,
    val weeklyActivity: List<Int> = List(7) { 0 },
    val weeklyLabels: List<String> = listOf("M", "T", "W", "T", "F", "S", "S")
)

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

fun badgesFor(
    points: Int,
    plasticsSaved: Int,
    stats: EcoProfileStats = EcoProfileStats()
): List<EcoBadge> = listOf(
    EcoBadge("Leaf Guardian", points >= 50),
    EcoBadge("Water Saver", plasticsSaved >= 5),
    EcoBadge("Pedal Power", points >= 200),
    EcoBadge("Forest Friend", points >= 800),
    EcoBadge("7-Day Streak", stats.currentStreak >= 7),
    EcoBadge("Campus Hero", points >= 3000),
    EcoBadge("Task Master", stats.completedTasks >= 10),
    EcoBadge("Green Regular", stats.approvedActions >= 20)
)

/** Friendly illustrative impact equivalents, not a scientific carbon calculator. */
data class EcoImpact(
    val co2GramsSaved: Int,
    val treesEquivalent: Double,
    val waterLitersSaved: Int
)

fun impactFor(points: Int, plasticsSaved: Int): EcoImpact {
    val co2Grams = plasticsSaved * 80 + points * 5
    val trees = co2Grams / 21_000.0
    val waterLiters = plasticsSaved * 3
    return EcoImpact(co2Grams, trees, waterLiters)
}

fun milestonesFor(points: Int, plasticsSaved: Int): List<EcoMilestone> = listOf(
    EcoMilestone("Tree Planter", plasticsSaved.coerceAtMost(20), 20, "$plasticsSaved/20 plastics saved"),
    EcoMilestone("Solar Explorer", points.coerceAtMost(500), 500, "$points/500 points"),
    EcoMilestone("Recycle Master", plasticsSaved.coerceAtMost(100), 100, "$plasticsSaved/100 items"),
    EcoMilestone("Zero Waste Advocate", points.coerceAtMost(1500), 1500, "$points/1500 points")
)

fun goalsFor(points: Int, plasticsSaved: Int, stats: EcoProfileStats): List<EcoGoal> = listOf(
    EcoGoal("Save 50 plastic items", plasticsSaved, 50, "items"),
    EcoGoal("Reach 3,000 eco points", points, 3000, "points"),
    EcoGoal("Complete 30 eco actions", stats.approvedActions, 30, "actions")
)

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

    val thirtyDaysAgo = now - 30L * 24 * 60 * 60 * 1000
    val monthlyPoints =
        approvedSubmissions.filter { (it.reviewTimestamp ?: it.timestamp) >= thirtyDaysAgo }.sumOf { it.points } +
                approvedTasks.filter { (it.reviewTimestamp ?: it.timestamp) >= thirtyDaysAgo }.sumOf { it.points }

    val rank = currentUser?.let { user ->
        allUsers
            .sortedByDescending { it.totalPoints }
            .indexOfFirst { it.studentId == user.studentId }
            .takeIf { it >= 0 }
            ?.plus(1)
            ?: 0
    } ?: 0

    val weekStart = startOfDay(now) - 6L * 24 * 60 * 60 * 1000
    val weeklyActivity = MutableList(7) { 0 }
    events.forEach { timestamp ->
        val dayIndex = ((startOfDay(timestamp) - weekStart) / (24L * 60 * 60 * 1000)).toInt()
        if (dayIndex in 0..6) weeklyActivity[dayIndex]++
    }

    return EcoProfileStats(
        approvedActions = approvedActions,
        submittedActions = submittedActions,
        completedTasks = completedTasks,
        monthlyPoints = monthlyPoints,
        currentStreak = calculateCurrentStreak(events, now),
        campusRank = rank,
        campusTotal = allUsers.size,
        weeklyActivity = weeklyActivity,
        weeklyLabels = buildWeekLabels(now)
    )
}

private fun startOfDay(timestamp: Long): Long {
    val c = Calendar.getInstance().apply { timeInMillis = timestamp }
    c.set(Calendar.HOUR_OF_DAY, 0)
    c.set(Calendar.MINUTE, 0)
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MILLISECOND, 0)
    return c.timeInMillis
}

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
