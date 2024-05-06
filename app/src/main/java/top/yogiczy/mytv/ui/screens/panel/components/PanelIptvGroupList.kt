package top.yogiczy.mytv.ui.screens.panel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.LocalTextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.data.entities.IptvGroupList.Companion.iptvGroupIdx
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.panel.PanelAutoCloseState
import top.yogiczy.mytv.ui.screens.panel.rememberPanelAutoCloseState
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents
import kotlin.math.max

@Composable
fun PanelIptvGroupList(
    modifier: Modifier = Modifier,
    currentIptv: Iptv = Iptv.EMPTY,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    epgList: EpgList = EpgList(),
    onIptvSelected: (Iptv) -> Unit = {},
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    onChangeToFavoriteList: () -> Unit = {},
    showProgrammeProgress: Boolean = false,
    state: TvLazyListState = rememberTvLazyListState(
        max(0, iptvGroupList.iptvGroupIdx(currentIptv))
    ),
    panelAutoCloseState: PanelAutoCloseState = rememberPanelAutoCloseState(),
) {
    val childPadding = rememberChildPadding()
    LaunchedEffect(state.firstVisibleItemIndex) { panelAutoCloseState.active() }

    TvLazyColumn(
        state = state,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = childPadding.bottom),
    ) {
        itemsIndexed(iptvGroupList) { index, it ->
            Row(modifier = Modifier.padding(start = childPadding.start)) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.labelMedium,
                    LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                ) {
                    Text(text = it.name)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${it.iptvs.size}个频道",
                        color = LocalContentColor.current.copy(alpha = 0.8f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            PanelIptvList(
                modifier = if (index == 0) {
                    Modifier.handleDPadKeyEvents(onUp = { onChangeToFavoriteList() })
                } else Modifier,
                currentIptv = currentIptv,
                iptvList = it.iptvs,
                epgList = epgList,
                onIptvSelected = onIptvSelected,
                onIptvFavoriteToggle = onIptvFavoriteToggle,
                showProgrammeProgress = showProgrammeProgress,
                panelAutoCloseState = panelAutoCloseState,
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun PanelIptvGroupListPreview() {
    MyTVTheme {
        Box(modifier = Modifier.height(150.dp)) {
            PanelIptvGroupList(
                iptvGroupList = IptvGroupList.EXAMPLE,
                currentIptv = Iptv.EXAMPLE,
            )
        }
    }
}