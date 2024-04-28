package top.yogiczy.mytv.ui.screens.panel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import top.yogiczy.mytv.data.entities.Epg
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvList
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents
import kotlin.math.max
import kotlin.math.min

@Composable
fun PanelIptvList(
    modifier: Modifier = Modifier,
    currentIptv: Iptv = Iptv.EMPTY,
    iptvList: IptvList = IptvList(),
    onIptvSelected: (Iptv) -> Unit = {},
    epgList: EpgList = EpgList(),
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    showProgrammeProgress: Boolean = false,
    state: TvLazyListState = rememberTvLazyListState(max(0, iptvList.indexOf(currentIptv))),
) {
    val childPadding = rememberChildPadding()

    var showEpgDialog by remember { mutableStateOf(false) }
    var currentShowEpgIptv by remember { mutableStateOf(Iptv.EMPTY) }
    val currentEpg = remember(currentShowEpgIptv) {
        epgList.firstOrNull { epg ->
            epg.channel == currentShowEpgIptv.channelName
        }
    }

    TvLazyRow(
        state = state,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(
            start = childPadding.start,
            end = childPadding.end,
        ),
    ) {
        items(iptvList) { iptv ->
            PanelIptvItem(
                iptv = iptv,
                epg = epgList.firstOrNull { epg -> epg.channel == iptv.channelName },
                onIptvSelected = { onIptvSelected(iptv) },
                onIptvFavoriteToggle = { onIptvFavoriteToggle(iptv) },
                showProgrammeProgress = showProgrammeProgress,
                onShowEpg = {
                    currentShowEpgIptv = iptv
                    showEpgDialog = true
                },
                initialFocused = iptv == currentIptv,
            )
        }
    }

    PanelIptvItemEpgDialog(
        showDialog = showEpgDialog,
        onDismissRequest = { showEpgDialog = false },
        iptv = currentShowEpgIptv,
        epg = currentEpg ?: Epg.EMPTY,
        modifier = Modifier.handleDPadKeyEvents(
            onLeft = {
                currentShowEpgIptv = iptvList[max(0, iptvList.indexOf(currentShowEpgIptv) - 1)]
            },
            onRight = {
                currentShowEpgIptv =
                    iptvList[min(iptvList.size - 1, iptvList.indexOf(currentShowEpgIptv) + 1)]
            },
        ),
    )
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun PanelIptvListPreview() {
    MyTVTheme {
        PanelIptvList(
            iptvList = IptvList.EXAMPLE,
            currentIptv = Iptv.EXAMPLE,
        )
    }
}