package top.yogiczy.mytv.ui.screens.panel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyGridState
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.foundation.lazy.grid.rememberTvLazyGridState
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.LocalTextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
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
fun PanelIptvFavoriteList(
    modifier: Modifier = Modifier,
    currentIptv: Iptv = Iptv.EMPTY,
    iptvList: IptvList = IptvList(),
    epgList: EpgList = EpgList(),
    onIptvSelected: (Iptv) -> Unit = {},
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    onClose: () -> Unit = {},
    showProgrammeProgress: Boolean = false,
    state: TvLazyGridState = rememberTvLazyGridState(max(0, iptvList.indexOf(currentIptv))),
) {
    val favoriteListSize = 6
    val childPadding = rememberChildPadding()

    var showEpgDialog by remember { mutableStateOf(false) }
    var currentShowEpgIptv by remember { mutableStateOf(Iptv.EMPTY) }
    val currentEpg = remember(currentShowEpgIptv) {
        epgList.firstOrNull { epg ->
            epg.channel == currentShowEpgIptv.channelName
        }
    }

    Column(modifier = modifier) {
        Row(modifier = Modifier.padding(start = childPadding.start)) {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.labelMedium,
                LocalContentColor provides MaterialTheme.colorScheme.onBackground,
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
            state = state,
            columns = TvGridCells.Fixed(favoriteListSize),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(
                top = 6.dp,
                start = childPadding.start,
                end = childPadding.end,
                bottom = childPadding.bottom,
            ),
        ) {
            itemsIndexed(iptvList) { index, it ->
                PanelIptvItem(
                    modifier = if (index < favoriteListSize) {
                        Modifier.handleDPadKeyEvents(onUp = { onClose() })
                    } else Modifier,
                    iptv = it,
                    onIptvSelected = { onIptvSelected(it) },
                    epg = epgList.firstOrNull { epg -> epg.channel == it.channelName },
                    onIptvFavoriteToggle = { onIptvFavoriteToggle(it) },
                    showProgrammeProgress = showProgrammeProgress,
                    initialFocused = if (index == 0 && !iptvList.contains(currentIptv)) true else it == currentIptv,
                    onShowEpg = {
                        currentShowEpgIptv = it
                        showEpgDialog = true
                    }
                )
            }
        }
    }

    PanelIptvItemEpgDialog(
        showDialog = showEpgDialog,
        onDismissRequest = { showEpgDialog = false },
        iptv = currentShowEpgIptv,
        epg = currentEpg ?: Epg.EMPTY,
        modifier = Modifier
            .handleDPadKeyEvents(
                onLeft = {
                    currentShowEpgIptv = iptvList[max(0, iptvList.indexOf(currentShowEpgIptv) - 1)]
                },
                onRight = {
                    currentShowEpgIptv =
                        iptvList[min(iptvList.size - 1, iptvList.indexOf(currentShowEpgIptv) + 1)]
                },
            )
    )
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun PanelIptvFavoriteListPreview() {
    MyTVTheme {
        PanelIptvFavoriteList(
            iptvList = IptvList.EXAMPLE,
        )
    }
}