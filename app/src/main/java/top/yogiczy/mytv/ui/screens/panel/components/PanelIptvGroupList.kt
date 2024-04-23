package top.yogiczy.mytv.ui.screens.panel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.data.entities.IptvGroupList.Companion.iptvGroupIdx
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents
import kotlin.math.max

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelIptvGroupList(
    modifier: Modifier = Modifier,
    currentIptv: Iptv = Iptv.EMPTY,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    epgList: EpgList = EpgList(),
    onIptvSelected: (Iptv) -> Unit = {},
    state: TvLazyListState = rememberTvLazyListState(
        max(0, iptvGroupList.iptvGroupIdx(currentIptv))
    ),
    onIptvFavoriteChange: (Iptv) -> Unit = {},
    onChangeToFavoriteList: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()

    TvLazyColumn(
        state = state,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = childPadding.bottom),
    ) {
        itemsIndexed(iptvGroupList) { index, it ->
            Text(
                text = it.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = childPadding.start),
            )
            Spacer(modifier = Modifier.height(6.dp))
            PanelIptvList(
                modifier = if (index == 0) {
                    Modifier.handleDPadKeyEvents(onUp = { onChangeToFavoriteList() })
                } else Modifier,
                currentIptv = currentIptv,
                iptvList = it.iptvs,
                epgList = epgList,
                onIptvSelected = onIptvSelected,
                onIptvFavoriteChange = onIptvFavoriteChange,
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