package top.yogiczy.mytv.ui.screens.settings.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.ListItem
import androidx.tv.material3.Switch
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.tvmaterial.StandardDialog
import top.yogiczy.mytv.ui.screens.settings.SettingsState
import top.yogiczy.mytv.ui.screens.settings.rememberSettingsState
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.SP
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents
import java.text.DecimalFormat

@Composable
fun SettingsUIItem(
    modifier: Modifier = Modifier,
    settingsState: SettingsState = rememberSettingsState(),
) {
    var showDialog by remember { mutableStateOf(false) }

    SettingsItem(
        modifier = modifier,
        title = "界面",
        value = "",
        description = "界面相关设置",
        onClick = { showDialog = true },
    )

    SettingsUIDialog(
        showDialog = showDialog,
        onDismissRequest = { showDialog = false },
        settingsState = settingsState,
    )
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun SettingsUIDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit = {},
    settingsState: SettingsState = rememberSettingsState(),
) {
    StandardDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        title = { Text(text = "界面") },
    ) {
        TvLazyColumn(modifier = modifier, contentPadding = PaddingValues(vertical = 4.dp)) {
            item {
                ListItem(
                    modifier = modifier.handleDPadKeyEvents(
                        onSelect = {
                            settingsState.uiShowEpgProgrammeProgress =
                                !settingsState.uiShowEpgProgrammeProgress
                        },
                    ),
                    selected = false,
                    onClick = { },
                    headlineContent = { Text(text = "节目进度") },
                    supportingContent = { Text(text = "在频道项底部显示当前节目进度条") },
                    trailingContent = {
                        Switch(
                            checked = settingsState.uiShowEpgProgrammeProgress,
                            onCheckedChange = null,
                        )
                    },
                )
            }

            item {
                ListItem(
                    modifier = modifier.handleDPadKeyEvents(
                        onSelect = {
                            settingsState.uiUseClassicPanelScreen =
                                !settingsState.uiUseClassicPanelScreen
                        },
                    ),
                    selected = false,
                    onClick = { },
                    headlineContent = { Text(text = "经典选台界面") },
                    supportingContent = { Text(text = "将选台界面替换为经典三段式结构") },
                    trailingContent = {
                        Switch(
                            checked = settingsState.uiUseClassicPanelScreen,
                            onCheckedChange = null,
                        )
                    },
                )
            }

            item {
                val timeShowRangeSeconds = Constants.UI_TIME_SHOW_RANGE / 1000

                ListItem(
                    modifier = modifier.handleDPadKeyEvents(
                        onSelect = {
                            settingsState.uiTimeShowMode =
                                SP.UiTimeShowMode.entries.let { it[(it.indexOf(settingsState.uiTimeShowMode) + 1) % it.size] }
                        },
                    ),
                    selected = false,
                    onClick = { },
                    headlineContent = { Text(text = "时间显示") },
                    supportingContent = {
                        Text(
                            text = when (settingsState.uiTimeShowMode) {
                                SP.UiTimeShowMode.HIDDEN -> "不显示时间"
                                SP.UiTimeShowMode.ALWAYS -> "总是显示时间"
                                SP.UiTimeShowMode.EVERY_HOUR -> "整点前后${timeShowRangeSeconds}s显示时间"
                                SP.UiTimeShowMode.HALF_HOUR -> "半点前后${timeShowRangeSeconds}s显示时间"
                            }
                        )
                    },
                    trailingContent = {
                        when (settingsState.uiTimeShowMode) {
                            SP.UiTimeShowMode.HIDDEN -> Text(text = "隐藏")
                            SP.UiTimeShowMode.ALWAYS -> Text(text = "常显")
                            SP.UiTimeShowMode.EVERY_HOUR -> Text(text = "整点")
                            SP.UiTimeShowMode.HALF_HOUR -> Text(text = "半点")
                        }
                    },
                )
            }

            item {
                val defaultScale = 1f
                val minScale = 1f
                val maxScale = 2f
                val stepScale = 0.1f

                ListItem(
                    modifier = modifier.handleDPadKeyEvents(
                        onSelect = {
                            if (settingsState.uiDensityScaleRatio >= maxScale) {
                                settingsState.uiDensityScaleRatio = minScale
                            } else {
                                settingsState.uiDensityScaleRatio =
                                    (settingsState.uiDensityScaleRatio + stepScale).coerceIn(
                                        minScale, maxScale
                                    )
                            }
                        },
                        onLongSelect = { settingsState.uiDensityScaleRatio = defaultScale },
                    ),
                    selected = false,
                    onClick = { },
                    headlineContent = { Text(text = "界面整体缩放比例") },
                    supportingContent = { Text(text = "短按切换缩放比例，长按恢复默认") },
                    trailingContent = {
                        Text(text = "×${DecimalFormat("#.#").format(settingsState.uiDensityScaleRatio)}")
                    },
                )
            }

            item {
                val defaultScale = 1f
                val minScale = 1f
                val maxScale = 2f
                val stepScale = 0.1f

                ListItem(
                    modifier = modifier.handleDPadKeyEvents(
                        onSelect = {
                            if (settingsState.uiFontScaleRatio >= maxScale) {
                                settingsState.uiFontScaleRatio = minScale
                            } else {
                                settingsState.uiFontScaleRatio =
                                    (settingsState.uiFontScaleRatio + stepScale).coerceIn(
                                        minScale, maxScale
                                    )
                            }
                        },
                        onLongSelect = { settingsState.uiFontScaleRatio = defaultScale },
                    ),
                    selected = false,
                    onClick = { },
                    headlineContent = { Text(text = "界面字体缩放比例") },
                    supportingContent = { Text(text = "短按切换缩放比例，长按恢复默认") },
                    trailingContent = {
                        Text(text = "×${DecimalFormat("#.#").format(settingsState.uiFontScaleRatio)}")
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun SettingsUIDialogPreview() {
    MyTVTheme {
        SettingsUIDialog(
            showDialog = true,
            settingsState = SettingsState(),
        )
    }
}