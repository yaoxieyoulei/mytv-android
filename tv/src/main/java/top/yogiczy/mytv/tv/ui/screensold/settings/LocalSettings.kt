package top.yogiczy.mytv.tv.ui.screensold.settings

import androidx.compose.runtime.compositionLocalOf

data class LocalSettingsCurrent(
    val uiFocusOptimize: Boolean = true,
    val uiShowEpgProgrammeProgress: Boolean = true,
    val uiShowChannelLogo: Boolean = true,
    val uiShowChannelPreview: Boolean = false,
)

val LocalSettings = compositionLocalOf { LocalSettingsCurrent() }