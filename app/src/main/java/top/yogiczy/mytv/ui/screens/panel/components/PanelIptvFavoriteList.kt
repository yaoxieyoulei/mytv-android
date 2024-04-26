package top.yogiczy.mytv.ui.screens.panel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvList
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents

@OptIn(ExperimentalTvMaterial3Api::class)
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
) {
    val favoriteListSize = 6
    val childPadding = rememberChildPadding()

    Column(modifier = modifier) {
        Text(
            text = "收藏",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = childPadding.start),
        )

        TvLazyVerticalGrid(
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
                )
            }
        }
    }
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