package top.yogiczy.mytv.tv.ui.screen.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.launch
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.channel.ChannelList
import top.yogiczy.mytv.core.data.entities.epg.EpgList
import top.yogiczy.mytv.core.data.entities.epg.EpgList.Companion.recentProgramme
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screen.channels.components.ChannelsChannelItem
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.screen.settings.LocalSettings
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids
import top.yogiczy.mytv.tv.ui.utils.backHandler
import top.yogiczy.mytv.tv.ui.utils.ifElse

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DashboardFavoriteList(
    modifier: Modifier = Modifier,
    channelListProvider: () -> ChannelList = { ChannelList() },
    onChannelSelected: (Channel) -> Unit = {},
    onChannelUnFavorite: (Channel) -> Unit = {},
    epgListProvider: () -> EpgList = { EpgList() },
) {
    val channelList = channelListProvider()

    val coroutineScope = rememberCoroutineScope()
    val childPadding = rememberChildPadding()
    val firstItemFocusRequester = remember { FocusRequester() }
    var isFirstItemFocused by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            "我的收藏",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = childPadding.start),
        )

        LazyRow(
            modifier = modifier
                .backHandler({ !isFirstItemFocused }) {
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                        firstItemFocusRequester.requestFocus()
                    }
                }
                .ifElse(
                    LocalSettings.current.uiFocusOptimize,
                    Modifier.focusRestorer { firstItemFocusRequester },
                ),
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(start = childPadding.start, end = childPadding.end),
        ) {
            itemsIndexed(channelList) { index, channel ->
                ChannelsChannelItem(
                    modifier = Modifier
                        .ifElse(
                            index == 0,
                            Modifier
                                .focusRequester(firstItemFocusRequester)
                                .onFocusChanged { isFirstItemFocused = it.isFocused },
                        ),
                    channelProvider = { channel },
                    onChannelSelected = { onChannelSelected(channel) },
                    onChannelFavoriteToggle = { onChannelUnFavorite(channel) },
                    recentEpgProgrammeProvider = { epgListProvider().recentProgramme(channel) },
                )
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun DashboardFavoriteListPreview() {
    MyTvTheme {
        AppScreen {
            DashboardFavoriteList(
                modifier = Modifier.padding(vertical = 20.dp),
                channelListProvider = { ChannelList.EXAMPLE },
                epgListProvider = { EpgList.example(ChannelList.EXAMPLE) },
            )
        }
        PreviewWithLayoutGrids { }
    }
}