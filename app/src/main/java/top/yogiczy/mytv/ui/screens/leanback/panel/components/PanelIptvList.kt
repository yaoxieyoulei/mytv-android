package top.yogiczy.mytv.ui.screens.leanback.panel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
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
fun LeanbackPanelIptvList(
    modifier: Modifier = Modifier,
    iptvListProvider: () -> IptvList = { IptvList() },
    epgListProvider: () -> EpgList = { EpgList() },
    currentIptvProvider: () -> Iptv = { Iptv() },
    showProgrammeProgressProvider: () -> Boolean = { false },
    onIptvSelected: (Iptv) -> Unit = {},
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    val iptvList = iptvListProvider()

    val listState = rememberTvLazyListState(max(0, iptvList.indexOf(currentIptvProvider()) - 2))
    val childPadding = rememberLeanbackChildPadding()

    var hasFocused by rememberSaveable { mutableStateOf(false) }

    var showEpgDialog by remember { mutableStateOf(false) }
    var currentShowEpgIptv by remember { mutableStateOf(Iptv()) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { _ -> onUserAction() }
    }

    TvLazyRow(
        state = listState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(
            start = childPadding.start,
            end = childPadding.end,
        ),
    ) {
        items(iptvList) { iptv ->
            LeanbackPanelIptvItem(
                iptvProvider = { iptv },
                currentProgrammeProvider = {
                    epgListProvider().firstOrNull { epg -> epg.channel == iptv.channelName }
                        ?.currentProgrammes()?.now
                },
                showProgrammeProgressProvider = { showProgrammeProgressProvider() },
                onIptvSelected = { onIptvSelected(iptv) },
                onIptvFavoriteToggle = { onIptvFavoriteToggle(iptv) },
                onShowEpg = {
                    currentShowEpgIptv = iptv
                    showEpgDialog = true
                },
                initialFocusedProvider = { iptv == currentIptvProvider() && !hasFocused },
                onHasFocused = { hasFocused = true },
            )
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

@Preview
@Composable
private fun LeanbackPanelIptvListPreview() {
    LeanbackTheme {
        LeanbackPanelIptvList(
            modifier = Modifier.padding(20.dp),
            iptvListProvider = { IptvList.EXAMPLE },
            currentIptvProvider = { Iptv.EXAMPLE },
        )
    }
}