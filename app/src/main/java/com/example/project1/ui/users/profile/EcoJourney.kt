package com.example.project1.ui.users.profile

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

fun badgesFor(points: Int, plasticsSaved: Int): List<EcoBadge> = listOf(
    EcoBadge("Leaf Guardian", points >= 50),
    EcoBadge("Water Saver", plasticsSaved >= 5),
    EcoBadge("Pedal Power", points >= 200)
)

fun milestonesFor(points: Int, plasticsSaved: Int): List<EcoMilestone> = listOf(
    EcoMilestone("Tree Planter", plasticsSaved.coerceAtMost(20), 20, "$plasticsSaved/20 plastics saved"),
    EcoMilestone("Solar Explorer", points.coerceAtMost(500), 500, "$points/500 points"),
    EcoMilestone("Recycle Master", plasticsSaved.coerceAtMost(100), 100, "$plasticsSaved/100 items"),
    EcoMilestone("Zero Waste Advocate", points.coerceAtMost(1500), 1500, "$points/1500 points")
)
