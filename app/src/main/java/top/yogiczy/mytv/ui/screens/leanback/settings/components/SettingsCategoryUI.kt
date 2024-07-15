package top.yogiczy.mytv.ui.screens.leanback.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.foundation.lazy.list.TvLazyColumn
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.screens.leanback.settings.LeanbackSettingsViewModel
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.SP
import top.yogiczy.mytv.utils.humanizeMs
import java.text.DecimalFormat

@Composable
fun LeanbackSettingsCategoryUI(
    modifier: Modifier = Modifier,
    settingsViewModel: LeanbackSettingsViewModel = viewModel(),
) {
    TvLazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
    ) {
        item {
            LeanbackSettingsCategoryListItem(
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
            LeanbackSettingsCategoryListItem(
                headlineContent = "经典选台界面",
                supportingContent = "将选台界面替换为经典三段式结构",
                trailingContent = {
                    Switch(
                        checked = settingsViewModel.uiUseClassicPanelScreen,
                        onCheckedChange = null
                    )
                },
                onSelected = {
                    settingsViewModel.uiUseClassicPanelScreen =
                        !settingsViewModel.uiUseClassicPanelScreen
                },
            )
        }

        item {
            val timeShowRangeSeconds = Constants.UI_TIME_SHOW_RANGE / 1000

            LeanbackSettingsCategoryListItem(
                headlineContent = "时间显示",
                supportingContent = when (settingsViewModel.uiTimeShowMode) {
                    SP.UiTimeShowMode.HIDDEN -> "不显示时间"
                    SP.UiTimeShowMode.ALWAYS -> "总是显示时间"
                    SP.UiTimeShowMode.EVERY_HOUR -> "整点前后${timeShowRangeSeconds}s显示时间"
                    SP.UiTimeShowMode.HALF_HOUR -> "半点前后${timeShowRangeSeconds}s显示时间"
                },
                trailingContent = when (settingsViewModel.uiTimeShowMode) {
                    SP.UiTimeShowMode.HIDDEN -> "隐藏"
                    SP.UiTimeShowMode.ALWAYS -> "常显"
                    SP.UiTimeShowMode.EVERY_HOUR -> "整点"
                    SP.UiTimeShowMode.HALF_HOUR -> "半点"
                },
                onSelected = {
                    settingsViewModel.uiTimeShowMode =
                        SP.UiTimeShowMode.entries.let { it[(it.indexOf(settingsViewModel.uiTimeShowMode) + 1) % it.size] }
                },
            )
        }

        item {
            LeanbackSettingsCategoryListItem(
                headlineContent = "画中画模式",
                trailingContent = {
                    Switch(
                        checked = settingsViewModel.uiPipMode,
                        onCheckedChange = null
                    )
                },
                onSelected = {
                    settingsViewModel.uiPipMode =
                        !settingsViewModel.uiPipMode
                },
            )
        }

        item {
            LeanbackSettingsCategoryListItem(
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

            LeanbackSettingsCategoryListItem(
                headlineContent = "界面整体缩放比例",
                supportingContent = "短按切换缩放比例，长按恢复默认；部分界面受影响",
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

            LeanbackSettingsCategoryListItem(
                headlineContent = "界面字体缩放比例",
                supportingContent = "短按切换缩放比例，长按恢复默认；部分界面受影响",
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

@Preview
@Composable
private fun LeanbackSettingsCategoryUIPreview() {
    SP.init(LocalContext.current)
    LeanbackTheme {
        LeanbackSettingsCategoryUI(
            modifier = Modifier.padding(20.dp),
            settingsViewModel = LeanbackSettingsViewModel(),
        )
    }
}