package top.yogiczy.mytv.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import top.yogiczy.mytv.core.designsystem.theme.Colors
import top.yogiczy.mytv.core.designsystem.theme.LocalColors
import top.yogiczy.mytv.core.designsystem.theme.darkColors
import top.yogiczy.mytv.core.designsystem.theme.lightColors

@Composable
fun MyTVTheme(
    isInDarkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isInDarkTheme) colorSchemeForDarkMode else colorSchemeForLightMode
    val colors = if (isInDarkTheme) darkColors else lightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurface,
            LocalColors provides colors,
        ) {
            content()
        }
    }
}

val MaterialTheme.colors: Colors
    @Composable
    get() = LocalColors.current