package top.yogiczy.mytv.tv.ui.screensold.settings

import androidx.compose.runtime.compositionLocalOf
import top.yogiczy.mytv.tv.ui.screen.components.AppThemeDef

data class LocalSettingsCurrent(
    val uiFocusOptimize: Boolean = true,
    val uiShowEpgProgrammeProgress: Boolean = true,
    val uiShowChannelLogo: Boolean = true,
    val uiShowChannelPreview: Boolean = false,
    val themeAppCurrent: AppThemeDef? = null,
)

val LocalSettings = compositionLocalOf { LocalSettingsCurrent() }