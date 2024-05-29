package top.yogiczy.mytv.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

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
            ),
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