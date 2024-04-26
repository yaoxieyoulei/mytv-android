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
                    modifier = modifier.handleDPadKeyEvents(onSelect = {
                        settingsState.uiShowEpgProgrammeProgress =
                            !settingsState.uiShowEpgProgrammeProgress
                    }),
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