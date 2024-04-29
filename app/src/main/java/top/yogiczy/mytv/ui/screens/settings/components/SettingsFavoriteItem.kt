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
import androidx.tv.material3.Text
import top.yogiczy.mytv.tvmaterial.StandardDialog
import top.yogiczy.mytv.ui.screens.settings.SettingsState
import top.yogiczy.mytv.ui.screens.settings.rememberSettingsState
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents

@Composable
fun SettingsFavoriteItem(
    modifier: Modifier = Modifier,
    settingsState: SettingsState = rememberSettingsState(),
) {
    var showDialog by remember { mutableStateOf(false) }

    SettingsItem(
        modifier = modifier,
        title = "收藏",
        value = "",
        description = "收藏相关设置",
        onClick = { showDialog = true },
    )

    SettingsFavoriteDialog(
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
fun SettingsFavoriteDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit = {},
    settingsState: SettingsState = rememberSettingsState(),
) {
    StandardDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        title = { Text(text = "收藏") },
    ) {
        TvLazyColumn(modifier = modifier, contentPadding = PaddingValues(vertical = 4.dp)) {
            item {
                ListItem(
                    selected = false,
                    onClick = { },
                    headlineContent = { Text(text = "当前已收藏") },
                    supportingContent = { Text(text = "包括不存在直播源中的频道") },
                    trailingContent = { Text(text = "${settingsState.iptvChannelFavoriteList.size}个频道") },
                )
            }

            item {
                ListItem(
                    modifier = modifier.handleDPadKeyEvents(onSelect = {
                        settingsState.iptvChannelFavoriteList = emptySet()
                    }),
                    selected = false,
                    onClick = { },
                    headlineContent = { Text(text = "清空全部收藏") },
                    supportingContent = { Text(text = "点按立即清空全部收藏") },
                )
            }
        }
    }
}

@Preview
@Composable
private fun SettingsFavoriteDialogPreview() {
    MyTVTheme {
        SettingsFavoriteDialog(
            showDialog = true,
            settingsState = SettingsState(),
        )
    }
}