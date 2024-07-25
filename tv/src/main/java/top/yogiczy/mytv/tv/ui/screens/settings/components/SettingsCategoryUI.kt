package top.yogiczy.mytv.tv.ui.screens.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Switch
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.core.util.utils.humanizeMs
import top.yogiczy.mytv.tv.ui.screens.settings.SettingsViewModel
import top.yogiczy.mytv.tv.ui.utils.Configs
import java.text.DecimalFormat

@Composable
fun SettingsCategoryUI(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    SettingsContentList(modifier) {
        item {
            SettingsListItem(
                headlineContent = "节目进度",
                supportingContent = "在频道项底部显示当前节目进度条",
                trailingContent = {
                    Switch(
                        checked = settingsViewModel.uiShowEpgProgrammeProgress,
                        onCheckedChange = null
                    )
                },
                onSelected = {
                    settingsViewModel.uiShowEpgProgrammeProgress =
                        !settingsViewModel.uiShowEpgProgrammeProgress
                },
            )
        }

        item {
            SettingsListItem(
                headlineContent = "经典选台界面",
                supportingContent = "将选台界面替换为经典三段式结构",
                trailingContent = {
                    Switch(
                        checked = settingsViewModel.uiUseClassicPanelScreen, onCheckedChange = null
                    )
                },
                onSelected = {
                    settingsViewModel.uiUseClassicPanelScreen =
                        !settingsViewModel.uiUseClassicPanelScreen
                },
            )
        }

        item {
            val timeShowRangeSeconds = Constants.UI_TIME_SCREEN_SHOW_DURATION / 1000

            SettingsListItem(
                headlineContent = "时间显示",
                supportingContent = when (settingsViewModel.uiTimeShowMode) {
                    Configs.UiTimeShowMode.HIDDEN -> "不显示时间"
                    Configs.UiTimeShowMode.ALWAYS -> "总是显示时间"
                    Configs.UiTimeShowMode.EVERY_HOUR -> "整点前后${timeShowRangeSeconds}s显示时间"
                    Configs.UiTimeShowMode.HALF_HOUR -> "半点前后${timeShowRangeSeconds}s显示时间"
                },
                trailingContent = when (settingsViewModel.uiTimeShowMode) {
                    Configs.UiTimeShowMode.HIDDEN -> "隐藏"
                    Configs.UiTimeShowMode.ALWAYS -> "常显"
                    Configs.UiTimeShowMode.EVERY_HOUR -> "整点"
                    Configs.UiTimeShowMode.HALF_HOUR -> "半点"
                },
                onSelected = {
                    settingsViewModel.uiTimeShowMode =
                        Configs.UiTimeShowMode.entries.let {
                            it[(it.indexOf(settingsViewModel.uiTimeShowMode) + 1) % it.size]
                        }
                },
            )
        }

        item {
            SettingsListItem(
                headlineContent = "超时自动关闭界面",
                supportingContent = "影响选台界面，快捷操作界面",
                trailingContent = Constants.UI_SCREEN_AUTO_CLOSE_DELAY.humanizeMs(),
                locK = true,
            )
        }

        item {
            val defaultScale = 1f
            val minScale = 1f
            val maxScale = 2f
            val stepScale = 0.1f

            SettingsListItem(
                headlineContent = "界面整体缩放比例",
                supportingContent = "短按切换缩放比例，长按恢复默认；",
                trailingContent = "×${DecimalFormat("#.#").format(settingsViewModel.uiDensityScaleRatio)}",
                onSelected = {
                    if (settingsViewModel.uiDensityScaleRatio >= maxScale) {
                        settingsViewModel.uiDensityScaleRatio = minScale
                    } else {
                        settingsViewModel.uiDensityScaleRatio =
                            (settingsViewModel.uiDensityScaleRatio + stepScale).coerceIn(
                                minScale, maxScale
                            )
                    }
                },
                onLongSelected = {
                    settingsViewModel.uiDensityScaleRatio = defaultScale
                },
            )
        }

        item {
            val defaultScale = 1f
            val minScale = 1f
            val maxScale = 2f
            val stepScale = 0.1f

            SettingsListItem(
                headlineContent = "界面字体缩放比例",
                supportingContent = "短按切换缩放比例，长按恢复默认；",
                trailingContent = "×${DecimalFormat("#.#").format(settingsViewModel.uiFontScaleRatio)}",
                onSelected = {
                    if (settingsViewModel.uiFontScaleRatio >= maxScale) {
                        settingsViewModel.uiFontScaleRatio = minScale
                    } else {
                        settingsViewModel.uiFontScaleRatio =
                            (settingsViewModel.uiFontScaleRatio + stepScale).coerceIn(
                                minScale, maxScale
                            )
                    }
                },
                onLongSelected = {
                    settingsViewModel.uiFontScaleRatio = defaultScale
                },
            )
        }
    }
}