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
        currentEpgXmlUrl = settingsState.epgXmlUrl,
        defaultEpgXmlUrl = Constants.EPG_XML_URL,
        epgXmlUrlList = settingsState.epgXmlUrlHistoryList.toList(),
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
    currentEpgXmlUrl: String = "",
    defaultEpgXmlUrl: String = Constants.EPG_XML_URL,
    epgXmlUrlList: List<String> = emptyList(),
    onSelected: (String) -> Unit = {},
    onDeleted: (String) -> Unit = {},
) {
    StandardDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        title = { Text(text = "历史节目单") },
        confirmButton = { Text(text = "点按切换；长按删除历史记录") },
    ) {
        TvLazyColumn(modifier = modifier, contentPadding = PaddingValues(vertical = 4.dp)) {
            // 保证默认节目单始终在最前
            val filteredEpgXmlUrlList =
                listOf(defaultEpgXmlUrl) + epgXmlUrlList.filter { it != defaultEpgXmlUrl }

            items(filteredEpgXmlUrlList) { source ->
                ListItem(
                    modifier = modifier
                        .handleDPadKeyEvents(
                            onSelect = { onSelected(source) },
                            onLongSelect = { onDeleted(source) },
                        ),
                    selected = source == currentEpgXmlUrl,
                    onClick = { },
                    headlineContent = {
                        Text(
                            text = if (source == defaultEpgXmlUrl) "默认节目单" else source,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    trailingContent = {
                        if (currentEpgXmlUrl == source) {
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
            epgXmlUrlList = listOf(
                "http://epg.51zmt.top:8000/e.xml",
                Constants.EPG_XML_URL,
                "https://live.fanmingming.com/e.xml"
            ),
            currentEpgXmlUrl = "http://epg.51zmt.top:8000/e.xml",
        )
    }
}
