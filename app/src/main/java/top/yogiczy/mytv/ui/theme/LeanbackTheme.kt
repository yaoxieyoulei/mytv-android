package top.yogiczy.mytv.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.sp
import top.yogiczy.mytv.R

private val HarmonyOSSans = FontFamily(
    Font(R.font.harmonyos_sans_black, FontWeight.Black),
    Font(R.font.harmonyos_sans_bold, FontWeight.Bold),
    Font(R.font.harmonyos_sans_light, FontWeight.Light),
    Font(R.font.harmonyos_sans_medium, FontWeight.Medium),
    Font(R.font.harmonyos_sans_regular, FontWeight.Normal),
    Font(R.font.harmonyos_sans_thin, FontWeight.Thin)
)

private val Typography = Typography(
    displayLarge = TextStyle(
        fontSize = 57.sp,
        lineHeight = 64.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.25).sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
    displayMedium = TextStyle(
        fontSize = 45.sp,
        lineHeight = 52.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
    displaySmall = TextStyle(
        fontSize = 36.sp,
        lineHeight = 44.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
    headlineLarge = TextStyle(
        fontSize = 32.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
    headlineMedium = TextStyle(
        fontSize = 28.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
    headlineSmall = TextStyle(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
    titleLarge = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.15.sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.25.sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.5.sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.25.sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.2.sp,
        textMotion = TextMotion.Animated,
        fontFamily = HarmonyOSSans,
    ),
)

private val darkColorScheme
    @Composable get() = darkColorScheme(
        primary = Color(0xFFA8C8FF),
        onPrimary = Color(0xFF003062),
        primaryContainer = Color(0xFF00468A),
        onPrimaryContainer = Color(0xFFD6E3FF),
        secondary = Color(0xFFBDC7DC),
        onSecondary = Color(0xFF273141),
        secondaryContainer = Color(0xFF3E4758),
        onSecondaryContainer = Color(0xFFD9E3F8),
        tertiary = Color(0xFFDCBCE1),
        onTertiary = Color(0xFF3E2845),
        tertiaryContainer = Color(0xFF563E5C),
        onTertiaryContainer = Color(0xFFF9D8FE),
        background = Color(0xFF000000),
        onBackground = Color(0xFFFFFFFF),
        surface = Color(0xFF1A1C1E),
        onSurface = Color(0xFFE3E2E6),
        surfaceVariant = Color(0xFF43474E),
        onSurfaceVariant = Color(0xFFC4C6CF),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFB4AB),
    )

@Composable
fun LeanbackTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = darkColorScheme,
        typography = Typography,
    ) {
        androidx.tv.material3.MaterialTheme(
            androidx.tv.material3.darkColorScheme(
                primary = MaterialTheme.colorScheme.primary,
                onPrimary = MaterialTheme.colorScheme.onPrimary,
                primaryContainer = MaterialTheme.colorScheme.primaryContainer,
                onPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer,
                secondary = MaterialTheme.colorScheme.secondary,
                onSecondary = MaterialTheme.colorScheme.onSecondary,
                secondaryContainer = MaterialTheme.colorScheme.secondaryContainer,
                onSecondaryContainer = MaterialTheme.colorScheme.onSecondaryContainer,
                tertiary = MaterialTheme.colorScheme.tertiary,
                onTertiary = MaterialTheme.colorScheme.onTertiary,
                tertiaryContainer = MaterialTheme.colorScheme.tertiaryContainer,
                onTertiaryContainer = MaterialTheme.colorScheme.onTertiaryContainer,
                background = MaterialTheme.colorScheme.background,
                onBackground = MaterialTheme.colorScheme.onBackground,
                surface = MaterialTheme.colorScheme.surface,
                onSurface = MaterialTheme.colorScheme.onSurface,
                surfaceVariant = MaterialTheme.colorScheme.surfaceVariant,
                onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant,
                error = MaterialTheme.colorScheme.error,
                onError = MaterialTheme.colorScheme.onError,
                errorContainer = MaterialTheme.colorScheme.errorContainer,
                onErrorContainer = MaterialTheme.colorScheme.onErrorContainer,
            )
        ) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                androidx.tv.material3.LocalContentColor provides androidx.tv.material3.MaterialTheme.colorScheme.onBackground,
            ) {
                content()
            }
        }
    }
}