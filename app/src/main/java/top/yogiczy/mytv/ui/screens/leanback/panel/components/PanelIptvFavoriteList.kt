package top.yogiczy.mytv.ui.screens.leanback.panel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.foundation.lazy.grid.rememberTvLazyGridState
import kotlinx.coroutines.flow.distinctUntilChanged
import top.yogiczy.mytv.data.entities.Epg
import top.yogiczy.mytv.data.entities.Epg.Companion.currentProgrammes
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvList
import top.yogiczy.mytv.ui.rememberLeanbackChildPadding
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.handleLeanbackKeyEvents
import kotlin.math.max
import kotlin.math.min

@Composable
fun LeanbackPanelIptvFavoriteList(
    modifier: Modifier = Modifier,
    iptvListProvider: () -> IptvList = { IptvList() },
    epgListProvider: () -> EpgList = { EpgList() },
    currentIptvProvider: () -> Iptv = { Iptv() },
    showProgrammeProgressProvider: () -> Boolean = { false },
    onIptvSelected: (Iptv) -> Unit = {},
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    onClose: () -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    val favoriteListSize = 6
    val childPadding = rememberLeanbackChildPadding()

    var key by remember { mutableIntStateOf(0) }
    val iptvList = remember(key) { iptvListProvider() }
    val listState = rememberTvLazyGridState(max(0, iptvList.indexOf(currentIptvProvider())))

    var hasFocused by rememberSaveable { mutableStateOf(false) }

    var showEpgDialog by remember { mutableStateOf(false) }
    var currentShowEpgIptv by remember { mutableStateOf(Iptv()) }

    LaunchedEffect(iptvList) {
        if (iptvList.isEmpty()) onClose()
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { _ -> onUserAction() }
    }

    Column(modifier = modifier) {
        Row(modifier = Modifier.padding(start = childPadding.start)) {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.labelMedium,
            ) {
                Text(text = "收藏")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${iptvList.size}个频道",
                    color = LocalContentColor.current.copy(alpha = 0.8f),
                )
            }
        }

        TvLazyVerticalGrid(
            state = listState,
            columns = TvGridCells.Fixed(favoriteListSize),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(
                top = 6.dp,
                start = childPadding.start,
                end = childPadding.end,
                bottom = childPadding.bottom,
            ),
        ) {
            itemsIndexed(iptvList) { index, iptv ->
                LeanbackPanelIptvItem(
                    modifier = if (index < favoriteListSize) {
                        Modifier.handleLeanbackKeyEvents(onUp = { onClose() })
                    } else Modifier,
                    iptvProvider = { iptv },
                    currentProgrammeProvider = {
                        epgListProvider().firstOrNull { epg -> epg.channel == iptv.channelName }
                            ?.currentProgrammes()?.now
                    },
                    showProgrammeProgressProvider = { showProgrammeProgressProvider() },
                    onIptvSelected = { onIptvSelected(iptv) },
                    onIptvFavoriteToggle = {
                        key++
                        onIptvFavoriteToggle(iptv)
                    },
                    onShowEpg = {
                        currentShowEpgIptv = iptv
                        showEpgDialog = true
                    },
                    initialFocusedProvider = {
                        if (hasFocused) false
                        else if (index == 0 && !iptvList.contains(currentIptvProvider())) true
                        else iptv == currentIptvProvider()
                    },
                    onHasFocused = { hasFocused = true },
                )
            }
        }
    }

    LeanbackPanelIptvEpgDialog(
        showDialogProvider = { showEpgDialog },
        onDismissRequest = { showEpgDialog = false },
        iptvProvider = { currentShowEpgIptv },
        epgProvider = {
            epgListProvider().firstOrNull { epg ->
                epg.channel == currentShowEpgIptv.channelName
            } ?: Epg()
        },
        modifier = Modifier
            .handleLeanbackKeyEvents(
                onLeft = {
                    currentShowEpgIptv = iptvList[max(0, iptvList.indexOf(currentShowEpgIptv) - 1)]
                },
                onRight = {
                    currentShowEpgIptv =
                        iptvList[min(iptvList.size - 1, iptvList.indexOf(currentShowEpgIptv) + 1)]
                },
            ),
        onUserAction = onUserAction,
    )
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun LeanbackPanelIptvFavoriteListPreview() {
    LeanbackTheme {
        LeanbackPanelIptvFavoriteList(
            iptvListProvider = { IptvList.EXAMPLE },
        )
    }
}