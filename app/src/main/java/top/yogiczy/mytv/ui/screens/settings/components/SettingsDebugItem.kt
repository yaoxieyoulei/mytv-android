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
import top.yogiczy.mytv.tvmaterial.StandardDialog
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents

@Composable
fun SettingsDebugItem(
    modifier: Modifier = Modifier,
    settingsState: SettingsState = rememberSettingsState(),
) {
    var showDialog by remember { mutableStateOf(false) }

    SettingsItem(
        modifier = modifier,
        title = "调试",
        value = "",
        description = "调试相关设置",
        onClick = { showDialog = true },
    )

    SettingsDebugDialog(
        showDialog = showDialog,
        onDismissRequest = { showDialog = false },
        settingsState = settingsState,
    )
}

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun SettingsDebugDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit = {},
    settingsState: SettingsState = rememberSettingsState(),
) {
    StandardDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        title = { Text(text = "调试") },
    ) {
        TvLazyColumn(modifier = modifier, contentPadding = PaddingValues(vertical = 4.dp)) {
            item {
                ListItem(
                    modifier = modifier.handleDPadKeyEvents(
                        onSelect = {
                            settingsState.debugShowFps = !settingsState.debugShowFps
                        }
                    ),
                    selected = false,
                    onClick = { },
                    headlineContent = { Text(text = "显示FPS") },
                    supportingContent = { Text(text = "在屏幕左上角显示fps和柱状图") },
                    trailingContent = {
                        Switch(
                            checked = settingsState.debugShowFps,
                            onCheckedChange = null,
                        )
                    },
                )
            }

            item {
                ListItem(
                    modifier = modifier.handleDPadKeyEvents(
                        onSelect = {
                            settingsState.debugShowPlayerInfo = !settingsState.debugShowPlayerInfo
                        }
                    ),
                    selected = false,
                    onClick = { },
                    headlineContent = { Text(text = "显示播放器信息") },
                    supportingContent = { Text(text = "显示播放器详细信息（编码、解码器、采样率等）") },
                    trailingContent = {
                        Switch(
                            checked = settingsState.debugShowPlayerInfo,
                            onCheckedChange = null,
                        )
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun SettingsDebugDialogPreview() {
    MyTVTheme {
        SettingsDebugDialog(
            showDialog = true,
            settingsState = SettingsState(),
        )
    }
}