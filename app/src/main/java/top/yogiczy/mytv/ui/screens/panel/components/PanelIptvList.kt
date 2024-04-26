package top.yogiczy.mytv.ui.screens.panel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvList
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.theme.MyTVTheme
import kotlin.math.max

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

    TvLazyRow(
        state = state,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(
            start = childPadding.start,
            end = childPadding.end,
        ),
    ) {
        items(iptvList) {
            PanelIptvItem(
                iptv = it,
                epg = epgList.firstOrNull { epg -> epg.channel == it.channelName },
                onIptvSelected = { onIptvSelected(it) },
                onIptvFavoriteToggle = { onIptvFavoriteToggle(it) },
                showProgrammeProgress = showProgrammeProgress,
                initialFocused = it == currentIptv,
            )
        }
    }
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