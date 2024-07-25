package top.yogiczy.mytv.tv.ui.screens.iptvsource

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
import top.yogiczy.mytv.tv.ui.screens.iptvsource.components.IptvSourceItem
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.utlis.HttpServer
import kotlin.math.max

@Composable
fun IptvSourceScreen(
    modifier: Modifier = Modifier,
    iptvSourceUrlListProvider: () -> ImmutableList<String> = { persistentListOf() },
    currentIptvSourceUrlProvider: () -> String = { "" },
    onIptvSourceUrlSelected: (String) -> Unit = {},
    onIptvSourceUrlDeleted: (String) -> Unit = {},
    onClose: () -> Unit = {},
) {
    val iptvSourceUrlList = iptvSourceUrlListProvider()
        .filter { it != Constants.IPTV_SOURCE_URL }
        .let { listOf(Constants.IPTV_SOURCE_URL).plus(it) }
    val currentIptvSourceUrl = currentIptvSourceUrlProvider()

    Drawer(
        position = DrawerPosition.Bottom,
        onDismissRequest = onClose,
        header = { Text("自定义直播源") },
    ) {
        val listState =
            rememberLazyListState(max(0, iptvSourceUrlList.indexOf(currentIptvSourceUrl) - 2))

        LazyColumn(
            modifier = modifier.height(240.dp),
            state = listState,
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(iptvSourceUrlList) { index, url ->
                IptvSourceItem(
                    iptvSourceUrlProvider = { url },
                    selectedProvider = {
                        if (iptvSourceUrlList.contains(url)) url == currentIptvSourceUrl
                        else index == 0
                    },
                    onSelected = { onIptvSourceUrlSelected(url) },
                    onDeleted = { onIptvSourceUrlDeleted(url) },
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
                        Text("添加自定义直播源")
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
private fun IptvSourceScreenPreview() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            IptvSourceScreen(
                iptvSourceUrlListProvider = {
                    persistentListOf(
                        "https://iptv-org.github.io/epg.xml",
                        "https://iptv-org.github.io/epg2.xml",
                        "https://iptv-org.github.io/epg3.xml",
                    )
                },
                currentIptvSourceUrlProvider = { "https://iptv-org.github.io/epg.xml" },
            )
        }
    }
}