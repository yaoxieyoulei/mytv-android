package top.yogiczy.mytv.ui.screens.classicpanel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.EpgList.Companion.currentProgrammes
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvList
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.panel.PanelAutoCloseState
import top.yogiczy.mytv.ui.screens.panel.rememberPanelAutoCloseState
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents
import kotlin.math.max

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ClassicPanelIptvList(
    modifier: Modifier = Modifier,
    iptvList: IptvList = IptvList(),
    currentIptv: Iptv = Iptv.EMPTY,
    currentIptvIdx: Int = max(0, iptvList.indexOf(currentIptv)),
    onChangeFocused: (Iptv, FocusRequester) -> Unit = { _, _ -> },
    onIptvSelected: (Iptv) -> Unit = {},
    epgList: EpgList = EpgList(),
    state: TvLazyListState = rememberTvLazyListState(currentIptvIdx),
    focusRequesterList: List<FocusRequester> = remember(iptvList) {
        List(iptvList.size) { FocusRequester() }
    },
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    panelAutoCloseState: PanelAutoCloseState = rememberPanelAutoCloseState(),
) {
    val childPadding = rememberChildPadding()

    LaunchedEffect(state.firstVisibleItemIndex) { panelAutoCloseState.active() }

    var hasFocused by remember { mutableStateOf(false) }
    LaunchedEffect(iptvList) {
        onChangeFocused(iptvList.first(), focusRequesterList.first())
    }

    Column(
        modifier = modifier.width(220.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "频道列表", style = MaterialTheme.typography.titleMedium)

        TvLazyColumn(
            state = state,
            contentPadding = PaddingValues(top = 8.dp, bottom = childPadding.bottom),
        ) {
            itemsIndexed(iptvList) { index, iptv ->
                LaunchedEffect(Unit) {
                    if (index == currentIptvIdx && !hasFocused) {
                        hasFocused = true
                        focusRequesterList[index].requestFocus()
                    }
                }

                ListItem(
                    modifier = Modifier
                        .focusRequester(focusRequesterList[index])
                        .onFocusChanged {
                            if (it.isFocused || it.hasFocus) {
                                onChangeFocused(iptv, focusRequesterList[index])
                            }
                        }
                        .handleDPadKeyEvents(
                            key = iptv.hashCode(),
                            onSelect = { onIptvSelected(iptv) },
                            onLongSelect = { onIptvFavoriteToggle(iptv) },
                        ),
                    selected = index == currentIptvIdx,
                    onClick = { },
                    headlineContent = { Text(text = iptv.name, maxLines = 2) },
                    supportingContent = {
                        Text(
                            text = epgList.currentProgrammes(iptv)?.now?.title ?: "无节目",
                            maxLines = 1,
                        )
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun ClassicPanelIptvListPreview() {
    MyTVTheme {
        ClassicPanelIptvList(
            iptvList = IptvList.EXAMPLE,
        )
    }
}