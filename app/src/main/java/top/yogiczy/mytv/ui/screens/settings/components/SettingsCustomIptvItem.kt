package top.yogiczy.mytv.ui.screens.settings.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.tvmaterial.StandardDialog
import top.yogiczy.mytv.ui.screens.settings.SettingsState
import top.yogiczy.mytv.ui.screens.settings.rememberSettingsState
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents

@Composable
fun SettingsCustomIptvItem(
    modifier: Modifier = Modifier,
    settingsState: SettingsState = rememberSettingsState(),
) {
    var showDialog by remember { mutableStateOf(false) }

    SettingsItem(
        modifier = modifier,
        title = "自定义直播源",
        value = if (settingsState.iptvSourceUrl != Constants.IPTV_SOURCE_URL) "已启用" else "未启用",
        description = "点按查看历史直播源",
        onClick = { showDialog = true },
    )

    SettingsIptvSourceHistoryDialog(
        showDialog = showDialog,
        onDismissRequest = { showDialog = false },
        currentIptvSourceUrl = settingsState.iptvSourceUrl,
        defaultIptvSourceUrl = Constants.IPTV_SOURCE_URL,
        iptvSourceUrlList = settingsState.iptvSourceUrlHistoryList.toList(),
        onSelected = {
            if (settingsState.iptvSourceUrl != it) {
                settingsState.iptvLastIptvIdx = 0
                settingsState.iptvSourceCachedAt = 0
                settingsState.iptvSourceUrl = it
            }
            showDialog = false
        },
        onDeleted = {
            if (it != Constants.IPTV_SOURCE_URL) {
                settingsState.iptvSourceUrlHistoryList -= it
            }
        },
    )
}

@OptIn(
    ExperimentalTvMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
private fun SettingsIptvSourceHistoryDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit = {},
    currentIptvSourceUrl: String = "",
    defaultIptvSourceUrl: String = Constants.IPTV_SOURCE_URL,
    iptvSourceUrlList: List<String> = emptyList(),
    onSelected: (String) -> Unit = {},
    onDeleted: (String) -> Unit = {},
) {
    StandardDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        title = { Text(text = "历史直播源") },
        confirmButton = { Text(text = "点按切换；长按删除历史记录") },
    ) {
        TvLazyColumn(modifier = modifier, contentPadding = PaddingValues(vertical = 4.dp)) {
            val filteredIptvSourceUrlList =
                listOf(defaultIptvSourceUrl) + iptvSourceUrlList.filter { it != defaultIptvSourceUrl }

            items(filteredIptvSourceUrlList) { source ->
                var isFocused by remember { mutableStateOf(false) }

                ListItem(
                    modifier = Modifier
                        .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
                        .handleDPadKeyEvents(
                            onSelect = { onSelected(source) },
                            onLongSelect = { onDeleted(source) },
                        ),
                    selected = source == currentIptvSourceUrl,
                    onClick = { },
                    headlineContent = {
                        Text(
                            text = if (source == defaultIptvSourceUrl) "默认直播源（网络需要支持ipv6）" else source,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = if (isFocused) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    trailingContent = {
                        if (currentIptvSourceUrl == source) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "checked")
                        }
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun SettingsIptvSourceHistoryDialogPreview() {
    MyTVTheme {
        SettingsIptvSourceHistoryDialog(
            showDialog = true,
            iptvSourceUrlList = listOf(
                "https://mirror.ghproxy.com/https://raw.githubusercontent.com/joevess/IPTV/main/home.m3u8",
                Constants.IPTV_SOURCE_URL,
                "http://127.0.0.1:8080/iptv.m3u"
            ),
            currentIptvSourceUrl = "https://mirror.ghproxy.com/https://raw.githubusercontent.com/joevess/IPTV/main/home.m3u8",
        )
    }
}