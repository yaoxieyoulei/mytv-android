package top.yogiczy.mytv.ui.screens.leanback.panel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import kotlinx.coroutines.flow.distinctUntilChanged
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.data.entities.IptvGroupList.Companion.iptvGroupIdx
import top.yogiczy.mytv.ui.rememberLeanbackChildPadding
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.handleLeanbackKeyEvents
import kotlin.math.max

@Composable
fun LeanbackPanelIptvGroupList(
    modifier: Modifier = Modifier,
    iptvGroupListProvider: () -> IptvGroupList = { IptvGroupList() },
    epgListProvider: () -> EpgList = { EpgList() },
    currentIptvProvider: () -> Iptv = { Iptv() },
    showProgrammeProgressProvider: () -> Boolean = { false },
    onIptvSelected: (Iptv) -> Unit = {},
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    onToFavorite: () -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    val iptvGroupList = iptvGroupListProvider()

    val listState =
        rememberTvLazyListState(max(0, iptvGroupList.iptvGroupIdx(currentIptvProvider())))
    val childPadding = rememberLeanbackChildPadding()

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { _ -> onUserAction() }
    }

    TvLazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = childPadding.bottom),
    ) {
        itemsIndexed(iptvGroupList) { index, iptvGroup ->
            Row(
                modifier = Modifier.padding(start = childPadding.start),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.labelMedium,
                ) {
                    Text(text = iptvGroup.name)
                    Text(
                        text = "${iptvGroup.iptvList.size}个频道",
                        color = LocalContentColor.current.copy(alpha = 0.8f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LeanbackPanelIptvList(
                modifier = if (index == 0) {
                    Modifier.handleLeanbackKeyEvents(onUp = { onToFavorite() })
                } else Modifier,
                iptvListProvider = { iptvGroup.iptvList },
                epgListProvider = epgListProvider,
                currentIptvProvider = currentIptvProvider,
                showProgrammeProgressProvider = showProgrammeProgressProvider,
                onIptvSelected = onIptvSelected,
                onIptvFavoriteToggle = onIptvFavoriteToggle,
                onUserAction = onUserAction,
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun LeanbackPanelIptvGroupListPreview() {
    LeanbackTheme {
        Box(modifier = Modifier.height(150.dp)) {
            LeanbackPanelIptvGroupList(
                iptvGroupListProvider = { IptvGroupList.EXAMPLE },
                currentIptvProvider = { Iptv.EXAMPLE },
            )
        }
    }
}