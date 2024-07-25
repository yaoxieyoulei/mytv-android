package top.yogiczy.mytv.tv.ui.screens.epgsource

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.tv.ui.material.Drawer
import top.yogiczy.mytv.tv.ui.material.DrawerPosition
import top.yogiczy.mytv.tv.ui.material.LocalPopupManager
import top.yogiczy.mytv.tv.ui.screens.components.QrcodeDialog
import top.yogiczy.mytv.tv.ui.screens.epgsource.components.EpgSourceItem
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.utlis.HttpServer
import kotlin.math.max

@Composable
fun EpgSourceScreen(
    modifier: Modifier = Modifier,
    epgXmlUrlListProvider: () -> ImmutableList<String> = { persistentListOf() },
    currentEpgXmlUrlProvider: () -> String = { "" },
    onEpgXmlUrlSelected: (String) -> Unit = {},
    onEpgXmlUrlDeleted: (String) -> Unit = {},
    onClose: () -> Unit = {},
) {
    val epgXmlUrlList = epgXmlUrlListProvider()
        .filter { it != Constants.EPG_XML_URL }
        .let { listOf(Constants.EPG_XML_URL).plus(it) }
    val currentEpgXmlUrl = currentEpgXmlUrlProvider()

    Drawer(
        position = DrawerPosition.Bottom,
        onDismissRequest = onClose,
        header = { Text("自定义节目单") },
    ) {
        val listState =
            rememberLazyListState(max(0, epgXmlUrlList.indexOf(currentEpgXmlUrl) - 2))

        LazyColumn(
            modifier = modifier.height(240.dp),
            state = listState,
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(epgXmlUrlList) { index, url ->
                EpgSourceItem(
                    epgXmlUrlProvider = { url },
                    selectedProvider = {
                        if (epgXmlUrlList.contains(url)) url == currentEpgXmlUrl
                        else index == 0
                    },
                    onSelected = { onEpgXmlUrlSelected(url) },
                    onDeleted = { onEpgXmlUrlDeleted(url) },
                )
            }

            item {
                val popupManager = LocalPopupManager.current
                val focusRequester = remember { FocusRequester() }
                var isFocused by remember { mutableStateOf(false) }
                var showDialog by remember { mutableStateOf(false) }

                ListItem(
                    modifier = modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
                        .handleKeyEvents(
                            isFocused = { isFocused },
                            focusRequester = focusRequester,
                            onSelect = {
                                popupManager.push(focusRequester, true)
                                showDialog = true
                            },
                        ),
                    selected = false,
                    onClick = {},
                    headlineContent = {
                        Text("添加自定义节目单")
                    },
                )

                QrcodeDialog(
                    textProvider = { HttpServer.serverUrl },
                    descriptionProvider = { "扫码前往设置页面" },
                    showDialogProvider = { showDialog },
                    onDismissRequest = { showDialog = false },
                )
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun EpgSourceScreenPreview() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            EpgSourceScreen(
                epgXmlUrlListProvider = {
                    persistentListOf(
                        "https://iptv-org.github.io/epg.xml",
                        "https://iptv-org.github.io/epg2.xml",
                        "https://iptv-org.github.io/epg3.xml",
                    )
                },
                currentEpgXmlUrlProvider = { "https://iptv-org.github.io/epg.xml" },
            )
        }
    }
}