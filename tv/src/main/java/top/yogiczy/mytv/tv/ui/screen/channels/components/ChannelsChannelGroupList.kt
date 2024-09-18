package top.yogiczy.mytv.tv.ui.screen.channels.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroup
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screen.settings.LocalSettings
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.utils.ifElse

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChannelsChannelGroupList(
    modifier: Modifier = Modifier,
    channelGroupListProvider: () -> ChannelGroupList = { ChannelGroupList() },
    currentChannelGroupProvider: () -> ChannelGroup = { ChannelGroup() },
    onChannelGroupSelected: (ChannelGroup) -> Unit = {},
) {
    val channelGroupList = channelGroupListProvider()
    val currentChannelGroup = currentChannelGroupProvider()
    val childPadding = rememberChildPadding()
    val firstItemFocusRequester = remember { FocusRequester() }

    if (channelGroupList.size <= 1) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .ifElse(
                LocalSettings.current.uiFocusOptimize,
                Modifier.focusRestorer { firstItemFocusRequester }
            ),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            contentPadding = PaddingValues(start = childPadding.start, end = childPadding.end),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            itemsIndexed(channelGroupList) { index, channelGroup ->
                ChannelsChannelGroupItem(
                    modifier = Modifier.ifElse(
                        index == 0,
                        Modifier.focusRequester(firstItemFocusRequester)
                    ),
                    channelGroupProvider = { channelGroup },
                    isSelectedProvider = { channelGroup == currentChannelGroup },
                    onChannelGroupSelected = { onChannelGroupSelected(channelGroup) },
                )
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun ChannelsChannelGroupListPreview() {
    MyTvTheme {
        ChannelsChannelGroupList(
            channelGroupListProvider = { ChannelGroupList.EXAMPLE },
            currentChannelGroupProvider = { ChannelGroupList.EXAMPLE.first() }
        )
    }
}