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
import top.yogiczy.mytv.ui.screens.settings.SettingsState
import top.yogiczy.mytv.ui.screens.settings.rememberSettingsState
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents

@Composable
fun SettingsUpdateItem(
    modifier: Modifier = Modifier,
    settingsState: SettingsState = rememberSettingsState(),
) {
    var showDialog by remember { mutableStateOf(false) }

    SettingsItem(
        modifier = modifier,
        title = "更新",
        value = "",
        description = "更新相关设置",
        onClick = { showDialog = true },
    )

    SettingsUpdateDialog(
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
fun SettingsUpdateDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit = {},
    settingsState: SettingsState = rememberSettingsState(),
) {
    StandardDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        title = { Text(text = "更新") },
    ) {
        TvLazyColumn(modifier = modifier, contentPadding = PaddingValues(vertical = 4.dp)) {
            item {
                ListItem(
                    modifier = modifier.handleDPadKeyEvents(onSelect = {
                        settingsState.updateForceRemind = !settingsState.updateForceRemind
                    }),
                    selected = false,
                    onClick = { },
                    headlineContent = { Text(text = "更新强提醒") },
                    supportingContent = {
                        Text(
                            text = if (settingsState.updateForceRemind) "在检测到新版本时会弹窗提醒"
                            else "在检测到新版本时仅在左上角提示",
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = settingsState.updateForceRemind,
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
private fun SettingsUpdateDialogPreview() {
    MyTVTheme {
        SettingsUpdateDialog(
            showDialog = true,
            settingsState = SettingsState(),
        )
    }
}