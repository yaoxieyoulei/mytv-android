package top.yogiczy.mytv.ui.screens.settings.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.tvmaterial.StandardDialog
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents

@Composable
fun SettingsCustomEpgItem(
    modifier: Modifier = Modifier,
    settingsState: SettingsState = rememberSettingsState(),
) {
    var showDialog by remember { mutableStateOf(false) }

    SettingsItem(
        modifier = modifier,
        title = "自定义节目单",
        value = if (settingsState.epgXmlUrl != Constants.EPG_XML_URL) "已启用" else "未启用",
        description = "点按查看历史节目单",
        onClick = { showDialog = true },
    )

    SettingsEpgHistoryDialog(
        showDialog = showDialog,
        onDismissRequest = { showDialog = false },
        epgList = settingsState.epgXmlUrlHistoryList.toList(),
        currentEpg = settingsState.epgXmlUrl,
        onSelected = {
            if (settingsState.epgXmlUrl != it) {
                settingsState.epgXmlCachedAt = 0
                settingsState.epgCachedHash = 0
                settingsState.epgXmlUrl = it
            }
            showDialog = false
        },
        onDeleted = {
            if (it != Constants.EPG_XML_URL) {
                settingsState.epgXmlUrlHistoryList -= it
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
private fun SettingsEpgHistoryDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit = {},
    epgList: List<String> = emptyList(),
    currentEpg: String = "",
    onSelected: (String) -> Unit = {},
    onDeleted: (String) -> Unit = {},
) {
    StandardDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        confirmButton = { Text(text = "点按切换；长按删除历史记录") },
        title = { Text(text = "历史节目单") },
    ) {
        TvLazyColumn(modifier = modifier) {
            items(epgList) { source ->
                ListItem(
                    modifier = modifier
                        .padding(vertical = 1.dp)
                        .handleDPadKeyEvents(
                            onSelect = { onSelected(source) },
                            onLongSelect = { onDeleted(source) },
                        ),
                    selected = source == currentEpg,
                    onClick = { },
                    headlineContent = {
                        Text(
                            text = if (source == Constants.IPTV_SOURCE_URL) "默认节目单" else source,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                        )
                    },
                    trailingContent = {
                        if (currentEpg == source) {
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
private fun SettingsEpgHistoryDialogPreview() {
    MyTVTheme {
        SettingsEpgHistoryDialog(
            showDialog = true,
            epgList = listOf("默认节目单", "自定义节目单", "自定义节目单1"),
            currentEpg = "自定义节目单",
        )
    }
}
