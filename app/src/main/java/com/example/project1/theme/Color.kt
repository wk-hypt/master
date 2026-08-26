package com.example.project1.ui.theme

import androidx.compose.ui.graphics.Color

/** Shared app colors. Screens should use these instead of repeating hex values. */
object EcoColors {
    val PrimaryGreen = Color(0xFF2E7D32)
    val DarkGreen = Color(0xFF1B5E20)
    val SoftGreen = Color(0xFFE8F5E9)
    val MintGreen = Color(0xFFF1F8E9)

    val PageBg = Color(0xFFF4F6F5)
    val AdminBg = Color(0xFFF6F8F5)
    val Cream = Color(0xFFF6F1E8)
    val Surface = Color.White

    val TextDark = Color(0xFF1B1F1C)
    val TextGrey = Color(0xFF8B948E)
    val TextGrey2 = Color(0xFF6C757D)
    val TextMuted = Color(0xFF6B7280)

    val Amber = Color(0xFFEF6C00)
    val Blue = Color(0xFF1565C0)
    val Danger = Color(0xFFC62828)
    val Rejected = Color(0xFFDC3545)
    val NotificationRed = Color(0xFFE53935)

    val CardBorder = Color(0xFFEDF1EC)
    val NavBorder = Color(0xFFE5E5E5)

    val ApprovedBg = Color(0xFFE8F5E9)
    val PendingYellowBg = Color(0xFFFFF8E1)
    val PendingYellowFg = Color(0xFF8D6E00)
    val InProgressBg = Color(0xFFF1F3F5)
    val RejectedBg = Color(0xFFFDECEA)
    val PendingAmberBg = Color(0xFFFFF3E0)
    val ExpiredBg = Color(0xFFFFEBEE)

    val AvatarPalette = listOf(
        PrimaryGreen,
        Blue,
        Amber,
        Color(0xFF6A1B9A),
        Danger,
        Color(0xFF00838F)
    )
}

// Light Scheme Colors
val primaryLight = EcoColors.PrimaryGreen
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFC8E6C9)
val onPrimaryContainerLight = Color(0xFF003300)

val secondaryLight = EcoColors.Blue
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFBBDEFB)
val onSecondaryContainerLight = Color(0xFF001D4A)

val tertiaryLight = EcoColors.Amber
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFFE0B2)
val onTertiaryContainerLight = Color(0xFF4E2600)

val errorLight = EcoColors.Danger
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFCDD2)
val onErrorContainerLight = Color(0xFF4A0002)

val backgroundLight = Color(0xFFF8FBF7)
val onBackgroundLight = Color(0xFF191C19)
val surfaceLight = Color(0xFFFFFFFF)
val onSurfaceLight = Color(0xFF191C19)
val surfaceVariantLight = Color(0xFFE0E4DE)
val onSurfaceVariantLight = Color(0xFF434843)
val outlineLight = Color(0xFF737973)
val outlineVariantLight = Color(0xFFC3C8C2)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2E312E)
val inverseOnSurfaceLight = Color(0xFFF0F1EC)
val inversePrimaryLight = Color(0xFF81C784)

val surfaceDimLight = Color(0xFFD9DBD7)
val surfaceBrightLight = Color(0xFFF8FBF7)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF2F5F1)
val surfaceContainerLight = Color(0xFFECEFEA)
val surfaceContainerHighLight = Color(0xFFE6EAE4)
val surfaceContainerHighestLight = Color(0xFFE1E4DE)

// Dark Scheme Colors
val primaryDark = Color(0xFF81C784)
val onPrimaryDark = Color(0xFF00390A)
val primaryContainerDark = Color(0xFF005317)
val onPrimaryContainerDark = Color(0xFFC8E6C9)

val secondaryDark = Color(0xFF90CAF9)
val onSecondaryDark = Color(0xFF00326B)
val secondaryContainerDark = Color(0xFF0B4993)
val onSecondaryContainerDark = Color(0xFFBBDEFB)

val tertiaryDark = Color(0xFFFFB74D)
val onTertiaryDark = Color(0xFF4D2000)
val tertiaryContainerDark = Color(0xFF873C00)
val onTertiaryContainerDark = Color(0xFFFFE0B2)

val errorDark = Color(0xFFE57373)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFCDD2)

val backgroundDark = Color(0xFF111411)
val onBackgroundDark = Color(0xFFE1E3DF)
val surfaceDark = Color(0xFF111411)
val onSurfaceDark = Color(0xFFE1E3DF)
val surfaceVariantDark = Color(0xFF434843)
val onSurfaceVariantDark = Color(0xFFC3C8C2)
val outlineDark = Color(0xFF8D938C)
val outlineVariantDark = Color(0xFF434843)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE1E3DF)
val inverseOnSurfaceDark = Color(0xFF2E312E)
val inversePrimaryDark = EcoColors.PrimaryGreen

val surfaceDimDark = Color(0xFF111411)
val surfaceBrightDark = Color(0xFF373A36)
val surfaceContainerLowestDark = Color(0xFF0C0F0C)
val surfaceContainerLowDark = Color(0xFF191C19)
val surfaceContainerDark = Color(0xFF1D201D)
val surfaceContainerHighDark = Color(0xFF282B27)
val surfaceContainerHighestDark = Color(0xFF333632)

// Contrast Placeholders to clear compilation errors
val primaryLightMediumContrast = primaryLight
val onPrimaryLightMediumContrast = onPrimaryLight
val primaryContainerLightMediumContrast = primaryContainerLight
val onPrimaryContainerLightMediumContrast = onPrimaryContainerLight
val secondaryLightMediumContrast = secondaryLight
val onSecondaryLightMediumContrast = onSecondaryLight
val secondaryContainerLightMediumContrast = secondaryContainerLight
val onSecondaryContainerLightMediumContrast = onSecondaryContainerLight
val tertiaryLightMediumContrast = tertiaryLight
val onTertiaryLightMediumContrast = onTertiaryLight
val tertiaryContainerLightMediumContrast = tertiaryContainerLight
val onTertiaryContainerLightMediumContrast = onTertiaryContainerLight
val errorLightMediumContrast = errorLight
val onErrorLightMediumContrast = onErrorLight
val errorContainerLightMediumContrast = errorContainerLight
val onErrorContainerLightMediumContrast = onErrorContainerLight
val backgroundLightMediumContrast = backgroundLight
val onBackgroundLightMediumContrast = onBackgroundLight
val surfaceLightMediumContrast = surfaceLight
val onSurfaceLightMediumContrast = onSurfaceLight
val surfaceVariantLightMediumContrast = surfaceVariantLight
val onSurfaceVariantLightMediumContrast = onSurfaceVariantLight
val outlineLightMediumContrast = outlineLight
val outlineVariantLightMediumContrast = outlineVariantLight
val scrimLightMediumContrast = scrimLight
val inverseSurfaceLightMediumContrast = inverseSurfaceLight
val inverseOnSurfaceLightMediumContrast = inverseOnSurfaceLight
val inversePrimaryLightMediumContrast = inversePrimaryLight
val surfaceDimLightMediumContrast = surfaceDimLight
val surfaceBrightLightMediumContrast = surfaceBrightLight
val surfaceContainerLowestLightMediumContrast = surfaceContainerLowestLight
val surfaceContainerLowLightMediumContrast = surfaceContainerLowLight
val surfaceContainerLightMediumContrast = surfaceContainerLight
val surfaceContainerHighLightMediumContrast = surfaceContainerHighLight
val surfaceContainerHighestLightMediumContrast = surfaceContainerHighestLight

val primaryLightHighContrast = primaryLight
val onPrimaryLightHighContrast = onPrimaryLight
val primaryContainerLightHighContrast = primaryContainerLight
val onPrimaryContainerLightHighContrast = onPrimaryContainerLight
val secondaryLightHighContrast = secondaryLight
val onSecondaryLightHighContrast = onSecondaryLight
val secondaryContainerLightHighContrast = secondaryContainerLight
val onSecondaryContainerLightHighContrast = onSecondaryContainerLight
val tertiaryLightHighContrast = tertiaryLight
val onTertiaryLightHighContrast = onTertiaryLight
val tertiaryContainerLightHighContrast = tertiaryContainerLight
val onTertiaryContainerLightHighContrast = onTertiaryContainerLight
val errorLightHighContrast = errorLight
val onErrorLightHighContrast = onErrorLight
val errorContainerLightHighContrast = errorContainerLight
val onErrorContainerLightHighContrast = onErrorContainerLight
val backgroundLightHighContrast = backgroundLight
val onBackgroundLightHighContrast = onBackgroundLight
val surfaceLightHighContrast = surfaceLight
val onSurfaceLightHighContrast = onSurfaceLight
val surfaceVariantLightHighContrast = surfaceVariantLight
val onSurfaceVariantLightHighContrast = onSurfaceVariantLight
val outlineLightHighContrast = outlineLight
val outlineVariantLightHighContrast = outlineVariantLight
val scrimLightHighContrast = scrimLight
val inverseSurfaceLightHighContrast = inverseSurfaceLight
val inverseOnSurfaceLightHighContrast = inverseOnSurfaceLight
val inversePrimaryLightHighContrast = inversePrimaryLight
val surfaceDimLightHighContrast = surfaceDimLight
val surfaceBrightLightHighContrast = surfaceBrightLight
val surfaceContainerLowestLightHighContrast = surfaceContainerLowestLight
val surfaceContainerLowLightHighContrast = surfaceContainerLowLight
val surfaceContainerLightHighContrast = surfaceContainerLight
val surfaceContainerHighLightHighContrast = surfaceContainerHighLight
val surfaceContainerHighestLightHighContrast = surfaceContainerHighestLight

val primaryDarkMediumContrast = primaryDark
val onPrimaryDarkMediumContrast = onPrimaryDark
val primaryContainerDarkMediumContrast = primaryContainerDark
val onPrimaryContainerDarkMediumContrast = onPrimaryContainerDark
val secondaryDarkMediumContrast = secondaryDark
val onSecondaryDarkMediumContrast = onSecondaryDark
val secondaryContainerDarkMediumContrast = secondaryContainerDark
val onSecondaryContainerDarkMediumContrast = onSecondaryContainerDark
val tertiaryDarkMediumContrast = tertiaryDark
val onTertiaryDarkMediumContrast = onTertiaryDark
val tertiaryContainerDarkMediumContrast = tertiaryContainerDark
val onTertiaryContainerDarkMediumContrast = onTertiaryContainerDark
val errorDarkMediumContrast = errorDark
val onErrorDarkMediumContrast = onErrorDark
val errorContainerDarkMediumContrast = errorContainerDark
val onErrorContainerDarkMediumContrast = onErrorContainerDark
val backgroundDarkMediumContrast = backgroundDark
val onBackgroundDarkMediumContrast = onBackgroundDark
val surfaceDarkMediumContrast = surfaceDark
val onSurfaceDarkMediumContrast = onSurfaceDark
val surfaceVariantDarkMediumContrast = surfaceVariantDark
val onSurfaceVariantDarkMediumContrast = onSurfaceVariantDark
val outlineDarkMediumContrast = outlineDark
val outlineVariantDarkMediumContrast = outlineVariantDark
val scrimDarkMediumContrast = scrimDark
val inverseSurfaceDarkMediumContrast = inverseSurfaceDark
val inverseOnSurfaceDarkMediumContrast = inverseOnSurfaceDark
val inversePrimaryDarkMediumContrast = inversePrimaryDark
val surfaceDimDarkMediumContrast = surfaceDimDark
val surfaceBrightDarkMediumContrast = surfaceBrightDark
val surfaceContainerLowestDarkMediumContrast = surfaceContainerLowestDark
val surfaceContainerLowDarkMediumContrast = surfaceContainerLowDark
val surfaceContainerDarkMediumContrast = surfaceContainerDark
val surfaceContainerHighDarkMediumContrast = surfaceContainerHighDark
val surfaceContainerHighestDarkMediumContrast = surfaceContainerHighestDark

val primaryDarkHighContrast = primaryDark
val onPrimaryDarkHighContrast = onPrimaryDark
val primaryContainerDarkHighContrast = primaryContainerDark
val onPrimaryContainerDarkHighContrast = onPrimaryContainerDark
val secondaryDarkHighContrast = secondaryDark
val onSecondaryDarkHighContrast = onSecondaryDark
val secondaryContainerDarkHighContrast = secondaryContainerDark
val onSecondaryContainerDarkHighContrast = onSecondaryContainerDark
val tertiaryDarkHighContrast = tertiaryDark
val onTertiaryDarkHighContrast = onTertiaryDark
val tertiaryContainerDarkHighContrast = tertiaryContainerDark
val onTertiaryContainerDarkHighContrast = onTertiaryContainerDark
val errorDarkHighContrast = errorDark
val onErrorDarkHighContrast = onErrorDark
val errorContainerDarkHighContrast = errorContainerDark
val onErrorContainerDarkHighContrast = onErrorContainerDark
val backgroundDarkHighContrast = backgroundDark
val onBackgroundDarkHighContrast = onBackgroundDark
val surfaceDarkHighContrast = surfaceDark
val onSurfaceDarkHighContrast = onSurfaceDark
val surfaceVariantDarkHighContrast = surfaceVariantDark
val onSurfaceVariantDarkHighContrast = onSurfaceVariantDark
val outlineDarkHighContrast = outlineDark
val outlineVariantDarkHighContrast = outlineVariantDark
val scrimDarkHighContrast = scrimDark
val inverseSurfaceDarkHighContrast = inverseSurfaceDark
val inverseOnSurfaceDarkHighContrast = inverseOnSurfaceDark
val inversePrimaryDarkHighContrast = inversePrimaryDark
val surfaceDimDarkHighContrast = surfaceDimDark
val surfaceBrightDarkHighContrast = surfaceBrightDark
val surfaceContainerLowestDarkHighContrast = surfaceContainerLowestDark
val surfaceContainerLowDarkHighContrast = surfaceContainerLowDark
val surfaceContainerDarkHighContrast = surfaceContainerDark
val surfaceContainerHighDarkHighContrast = surfaceContainerHighDark
val surfaceContainerHighestDarkHighContrast = surfaceContainerHighestDark