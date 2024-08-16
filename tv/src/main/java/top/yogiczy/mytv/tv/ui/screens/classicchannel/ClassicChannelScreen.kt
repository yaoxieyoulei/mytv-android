package top.yogiczy.mytv.tv.ui.screens.classicchannel

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroup
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList.Companion.channelGroupIdx
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList.Companion.channelList
import top.yogiczy.mytv.core.data.entities.channel.ChannelList
import top.yogiczy.mytv.core.data.entities.epg.EpgList
import top.yogiczy.mytv.core.data.entities.epg.EpgList.Companion.match
import top.yogiczy.mytv.core.data.entities.epg.EpgProgramme
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeReserveList
import top.yogiczy.mytv.tv.ui.material.Visible
import top.yogiczy.mytv.tv.ui.screens.classicchannel.components.ClassicChannelGroupItemList
import top.yogiczy.mytv.tv.ui.screens.classicchannel.components.ClassicChannelItemList
import top.yogiczy.mytv.tv.ui.screens.classicchannel.components.ClassicEpgItemList
import top.yogiczy.mytv.tv.ui.screens.components.rememberScreenAutoCloseState
import top.yogiczy.mytv.tv.ui.screens.videoplayer.player.VideoPlayer
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import kotlin.math.max

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClassicChannelScreen(
    modifier: Modifier = Modifier,
    channelGroupListProvider: () -> ChannelGroupList = { ChannelGroupList() },
    currentChannelProvider: () -> Channel = { Channel() },
    currentChannelUrlIdxProvider: () -> Int = { 0 },
    showChannelLogoProvider: () -> Boolean = { false },
    onChannelSelected: (Channel) -> Unit = {},
    onChannelFavoriteToggle: (Channel) -> Unit = {},
    epgListProvider: () -> EpgList = { EpgList() },
    epgProgrammeReserveListProvider: () -> EpgProgrammeReserveList = { EpgProgrammeReserveList() },
    showEpgProgrammeProgressProvider: () -> Boolean = { false },
    onEpgProgrammePlayback: (Channel, EpgProgramme) -> Unit = { _, _ -> },
    onEpgProgrammeReserve: (Channel, EpgProgramme) -> Unit = { _, _ -> },
    isInTimeShiftProvider: () -> Boolean = { false },
    supportPlaybackProvider: (Channel) -> Boolean = { false },
    currentPlaybackEpgProgrammeProvider: () -> EpgProgramme? = { null },
    videoPlayerMetadataProvider: () -> VideoPlayer.Metadata = { VideoPlayer.Metadata() },
    channelFavoriteEnabledProvider: () -> Boolean = { false },
    channelFavoriteListProvider: () -> ImmutableList<String> = { persistentListOf() },
    channelFavoriteListVisibleProvider: () -> Boolean = { false },
    onChannelFavoriteListVisibleChange: (Boolean) -> Unit = {},
    onClose: () -> Unit = {},
) {
    val screenAutoCloseState = rememberScreenAutoCloseState(onTimeout = onClose)
    val channelGroupList = channelGroupListProvider()

    var focusedChannelGroup by remember {
        mutableStateOf(
            if (channelFavoriteListVisibleProvider())
                ClassicPanelScreenFavoriteChannelGroup
            else
                channelGroupList[max(0, channelGroupList.channelGroupIdx(currentChannelProvider()))]
        )
    }
    var focusedChannel by remember { mutableStateOf(currentChannelProvider()) }
    var epgListVisible by remember { mutableStateOf(false) }

    ClassicChannelScreenWrapper(
        modifier = modifier,
        onClose = onClose,
    ) {
        Row {
            ClassicChannelGroupItemList(
                channelGroupListProvider = {
                    if (channelFavoriteEnabledProvider())
                        ChannelGroupList(listOf(ClassicPanelScreenFavoriteChannelGroup) + channelGroupList)
                    else
                        channelGroupList
                },
                initialChannelGroupProvider = {
                    if (channelFavoriteListVisibleProvider())
                        ClassicPanelScreenFavoriteChannelGroup
                    else
                        channelGroupList.find { it.channelList.contains(currentChannelProvider()) }
                            ?: ChannelGroup()
                },
                onChannelGroupFocused = {
                    focusedChannelGroup = it
                    onChannelFavoriteListVisibleChange(it == ClassicPanelScreenFavoriteChannelGroup)
                },
                onUserAction = { screenAutoCloseState.active() },
            )

            ClassicChannelItemList(
                modifier = Modifier
                    .handleKeyEvents(
                        onRight = { epgListVisible = true },
                        onLeft = { epgListVisible = false }
                    )
                    .focusProperties {
                        exit = {
                            if (epgListVisible && it == FocusDirection.Left) {
                                epgListVisible = false
                                FocusRequester.Cancel
                            } else {
                                FocusRequester.Default
                            }
                        }
                    },
                channelGroupProvider = { focusedChannelGroup },
                channelListProvider = {
                    if (focusedChannelGroup == ClassicPanelScreenFavoriteChannelGroup)
                        ChannelList(channelGroupListProvider().channelList
                            .filter { channelFavoriteListProvider().contains(it.name) })
                    else
                        focusedChannelGroup.channelList
                },
                epgListProvider = epgListProvider,
                initialChannelProvider = currentChannelProvider,
                onChannelSelected = onChannelSelected,
                onChannelFavoriteToggle = onChannelFavoriteToggle,
                onChannelFocused = { channel -> focusedChannel = channel },
                showEpgProgrammeProgressProvider = showEpgProgrammeProgressProvider,
                onUserAction = { screenAutoCloseState.active() },
                inFavoriteModeProvider = { focusedChannelGroup == ClassicPanelScreenFavoriteChannelGroup },
                showChannelLogoProvider = showChannelLogoProvider,
            )

            Visible({ epgListVisible }) {
                ClassicEpgItemList(
                    epgProvider = { epgListProvider().match(focusedChannel) },
                    epgProgrammeReserveListProvider = {
                        EpgProgrammeReserveList(
                            epgProgrammeReserveListProvider().filter { it.channel == focusedChannel.name }
                        )
                    },
                    supportPlaybackProvider = { supportPlaybackProvider(focusedChannel) },
                    currentPlaybackEpgProgrammeProvider = currentPlaybackEpgProgrammeProvider,
                    onEpgProgrammePlayback = { onEpgProgrammePlayback(focusedChannel, it) },
                    onEpgProgrammeReserve = { onEpgProgrammeReserve(focusedChannel, it) },
                    onUserAction = { screenAutoCloseState.active() },
                )
            }
            Visible({ !epgListVisible }) {
                ClassicPanelScreenShowEpgTip(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface.copy(0.7f))
                        .padding(horizontal = 4.dp),
                    onTap = { epgListVisible = true },
                )
            }
        }
    }
}

@Composable
private fun ClassicChannelScreenWrapper(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { onClose() }) }
    ) {
        Box(
            modifier = Modifier
                .pointerInput(Unit) { detectTapGestures(onTap = { }) }
                .padding(24.dp)
                .clip(MaterialTheme.shapes.medium),
        ) {
            content()
        }
    }
}

@Composable
private fun ClassicPanelScreenShowEpgTip(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onTap() })
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        "向右查看节目单".map {
            Text(text = it.toString(), style = MaterialTheme.typography.labelSmall)
        }
    }
}

val ClassicPanelScreenFavoriteChannelGroup = ChannelGroup(name = "我的收藏")

@Preview(device = "id:Android TV (720p)")
@Composable
private fun ClassicChannelScreenPreview() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            ClassicChannelScreen(
                channelGroupListProvider = { ChannelGroupList.EXAMPLE },
                currentChannelProvider = { ChannelGroupList.EXAMPLE.first().channelList.first() },
                currentChannelUrlIdxProvider = { 0 },
                epgListProvider = { EpgList.example(ChannelGroupList.EXAMPLE.channelList) },
                showEpgProgrammeProgressProvider = { true },
            )
        }
    }
}