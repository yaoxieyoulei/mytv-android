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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSource
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSourceList
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.tv.ui.material.Drawer
import top.yogiczy.mytv.tv.ui.material.DrawerPosition
import top.yogiczy.mytv.tv.ui.material.LocalPopupManager
import top.yogiczy.mytv.tv.ui.material.SimplePopup
import top.yogiczy.mytv.tv.ui.screens.iptvsource.components.IptvSourceItem
import top.yogiczy.mytv.tv.ui.screens.settings.components.SettingsCategoryPush
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunchedSaveable
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse
import kotlin.math.max

@Composable
fun IptvSourceScreen(
    modifier: Modifier = Modifier,
    iptvSourceListProvider: () -> IptvSourceList = { IptvSourceList() },
    currentIptvSourceProvider: () -> IptvSource = { IptvSource() },
    onIptvSourceSelected: (IptvSource) -> Unit = {},
    onIptvSourceDeleted: (IptvSource) -> Unit = {},
    onClose: () -> Unit = {},
) {
    val iptvSourceList = iptvSourceListProvider().let { Constants.IPTV_SOURCE_LIST + it }
    val currentIptvSource = currentIptvSourceProvider()
    val currentIptvSourceIdx = iptvSourceList.indexOf(currentIptvSource)

    val focusManager = LocalFocusManager.current

    Drawer(
        position = DrawerPosition.Bottom,
        onDismissRequest = onClose,
        header = { Text("自定义直播源") },
    ) {
        val listState = rememberLazyListState(max(0, currentIptvSourceIdx - 2))

        LazyColumn(
            modifier = modifier.height(240.dp),
            state = listState,
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(iptvSourceList) { index, source ->
                IptvSourceItem(
                    modifier = Modifier.ifElse(
                        max(0, currentIptvSourceIdx) == index,
                        Modifier.focusOnLaunchedSaveable(iptvSourceList),
                    ),
                    iptvSourceProvider = { source },
                    isSelectedProvider = { index == currentIptvSourceIdx },
                    onSelected = { onIptvSourceSelected(source) },
                    onDeleted = {
                        if (source == iptvSourceList.last()) {
                            focusManager.moveFocus(FocusDirection.Up)
                        }
                        onIptvSourceDeleted(source)
                    },
                )
            }

            item {
                val popupManager = LocalPopupManager.current
                val focusRequester = remember { FocusRequester() }
                var isFocused by remember { mutableStateOf(false) }
                var showPush by remember { mutableStateOf(false) }

                ListItem(
                    modifier = modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
                        .handleKeyEvents(
                            isFocused = { isFocused },
                            focusRequester = focusRequester,
                            onSelect = {
                                popupManager.push(focusRequester, true)
                                showPush = true
                            },
                        ),
                    selected = false,
                    onClick = {},
                    headlineContent = {
                        Text("添加自定义直播源")
                    },
                )

                SimplePopup(
                    visibleProvider = { showPush },
                    onDismissRequest = { showPush = false },
                ) {
                    SettingsCategoryPush()
                }
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
                iptvSourceListProvider = {
                    IptvSourceList(
                        listOf(
                            IptvSource(name = "直播源1", url = "http://1.2.3.4/iptv.m3u"),
                            IptvSource(name = "直播源2", url = "http://1.2.3.4/iptv.m3u"),
                        )
                    )
                },
                currentIptvSourceProvider = {
                    IptvSource(name = "直播源1", url = "http://1.2.3.4/iptv.m3u")
                },
            )
        }
    }
}