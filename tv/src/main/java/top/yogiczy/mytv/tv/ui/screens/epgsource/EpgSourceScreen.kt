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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSource
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSourceList
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.tv.ui.material.Drawer
import top.yogiczy.mytv.tv.ui.material.DrawerPosition
import top.yogiczy.mytv.tv.ui.material.LocalPopupManager
import top.yogiczy.mytv.tv.ui.material.SimplePopup
import top.yogiczy.mytv.tv.ui.screens.epgsource.components.EpgSourceItem
import top.yogiczy.mytv.tv.ui.screens.settings.components.SettingsCategoryPush
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids
import top.yogiczy.mytv.tv.ui.utils.Configs.iptvSourceList
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunchedSaveable
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse
import kotlin.math.max

@Composable
fun EpgSourceScreen(
    modifier: Modifier = Modifier,
    epgSourceListProvider: () -> EpgSourceList = { EpgSourceList() },
    currentEpgSourceProvider: () -> EpgSource = { EpgSource() },
    onEpgSourceSelected: (EpgSource) -> Unit = {},
    onEpgSourceDeleted: (EpgSource) -> Unit = {},
    onClose: () -> Unit = {},
) {
    val epgSourceList = epgSourceListProvider().let { Constants.EPG_SOURCE_LIST + it }
    val currentEpgSource = currentEpgSourceProvider()
    val currentEpgSourceIdx = epgSourceList.indexOf(currentEpgSource)

    val focusManager = LocalFocusManager.current

    Drawer(
        position = DrawerPosition.Bottom,
        onDismissRequest = onClose,
        header = { Text("自定义节目单") },
    ) {
        val listState = rememberLazyListState(max(0, currentEpgSourceIdx - 2))

        LazyColumn(
            modifier = modifier.height(240.dp),
            state = listState,
            contentPadding = PaddingValues(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(epgSourceList) { index, source ->
                EpgSourceItem(
                    modifier = Modifier.ifElse(
                        max(0, currentEpgSourceIdx) == index,
                        Modifier.focusOnLaunchedSaveable(iptvSourceList),
                    ),
                    epgSourceProvider = { source },
                    isSelectedProvider = { index == currentEpgSourceIdx },
                    onSelected = { onEpgSourceSelected(source) },
                    onDeleted = {
                        if (source == epgSourceList.last()) {
                            focusManager.moveFocus(FocusDirection.Up)
                        }
                        onEpgSourceDeleted(source)
                    },
                )
            }

            item {
                val popupManager = LocalPopupManager.current
                val focusRequester = remember { FocusRequester() }
                var showPush by remember { mutableStateOf(false) }

                ListItem(
                    modifier = modifier
                        .focusRequester(focusRequester)
                        .handleKeyEvents(
                            onSelect = {
                                popupManager.push(focusRequester, true)
                                showPush = true
                            },
                        ),
                    selected = false,
                    onClick = {},
                    headlineContent = {
                        Text("添加自定义节目单")
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
private fun EpgSourceScreenPreview() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            EpgSourceScreen(
                epgSourceListProvider = {
                    EpgSourceList(
                        listOf(
                            EpgSource(name = "EPG源1", url = "https://iptv-org.github.io/epg.xml"),
                            EpgSource(name = "EPG源2", url = "https://iptv-org.github.io/epg.xml"),
                        )
                    )
                },
                currentEpgSourceProvider = {
                    EpgSource(name = "EPG源1", url = "https://iptv-org.github.io/epg.xml")
                },
            )
        }
    }
}