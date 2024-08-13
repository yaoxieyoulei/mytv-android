package top.yogiczy.mytv.tv.ui.screens.quickop

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.channel.ChannelList
import top.yogiczy.mytv.core.data.entities.epg.EpgList
import top.yogiczy.mytv.core.data.entities.epg.EpgList.Companion.recentProgramme
import top.yogiczy.mytv.core.data.entities.epg.EpgProgramme
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screens.channel.components.ChannelInfo
import top.yogiczy.mytv.tv.ui.screens.channel.components.ChannelNumber
import top.yogiczy.mytv.tv.ui.screens.channel.components.ChannelPlayerInfo
import top.yogiczy.mytv.tv.ui.screens.components.rememberScreenAutoCloseState
import top.yogiczy.mytv.tv.ui.screens.datetime.components.DateTimeDetail
import top.yogiczy.mytv.tv.ui.screens.quickop.components.QuickOpBtnList
import top.yogiczy.mytv.tv.ui.screens.videoplayer.player.VideoPlayer
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids
import top.yogiczy.mytv.tv.ui.utils.captureBackKey

@Composable
fun QuickOpScreen(
    modifier: Modifier = Modifier,
    currentChannelProvider: () -> Channel = { Channel() },
    currentChannelUrlIdxProvider: () -> Int = { 0 },
    currentChannelNumberProvider: () -> String = { "" },
    epgListProvider: () -> EpgList = { EpgList() },
    isInTimeShiftProvider: () -> Boolean = { false },
    currentPlaybackEpgProgrammeProvider: () -> EpgProgramme? = { null },
    videoPlayerMetadataProvider: () -> VideoPlayer.Metadata = { VideoPlayer.Metadata() },
    onShowEpg: () -> Unit = {},
    onShowChannelUrl: () -> Unit = {},
    onShowVideoPlayerController: () -> Unit = {},
    onShowVideoPlayerDisplayMode: () -> Unit = {},
    onShowMoreSettings: () -> Unit = {},
    onClearCache: () -> Unit = {},
    onClose: () -> Unit = {},
) {
    val screenAutoCloseState = rememberScreenAutoCloseState(onTimeout = onClose)

    Box(
        modifier = modifier
            .captureBackKey { onClose() }
            .pointerInput(Unit) { detectTapGestures { onClose() } }
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
    ) {
        QuickOpScreenTopRight(
            channelNumberProvider = {
                currentChannelNumberProvider().padStart(2, '0')
            },
        )

        QuickOpScreenBottom(
            currentChannelProvider = currentChannelProvider,
            currentChannelUrlIdxProvider = currentChannelUrlIdxProvider,
            epgListProvider = epgListProvider,
            isInTimeShiftProvider = isInTimeShiftProvider,
            currentPlaybackEpgProgrammeProvider = currentPlaybackEpgProgrammeProvider,
            videoPlayerMetadataProvider = videoPlayerMetadataProvider,
            onShowEpg = onShowEpg,
            onShowChannelUrl = onShowChannelUrl,
            onShowVideoPlayerController = onShowVideoPlayerController,
            onShowVideoPlayerDisplayMode = onShowVideoPlayerDisplayMode,
            onShowMoreSettings = onShowMoreSettings,
            onClearCache = onClearCache,
            onUserAction = { screenAutoCloseState.active() },
        )
    }
}

@Composable
private fun QuickOpScreenTopRight(
    modifier: Modifier = Modifier,
    channelNumberProvider: () -> String = { "" },
) {
    val childPadding = rememberChildPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = childPadding.top, end = childPadding.end),
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopEnd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChannelNumber(channelNumberProvider = channelNumberProvider)

            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                Spacer(
                    modifier = Modifier
                        .background(Color.White)
                        .width(2.dp)
                        .height(30.dp),
                )
            }

            DateTimeDetail()
        }
    }
}

@Composable
private fun QuickOpScreenBottom(
    modifier: Modifier = Modifier,
    currentChannelProvider: () -> Channel = { Channel() },
    currentChannelUrlIdxProvider: () -> Int = { 0 },
    epgListProvider: () -> EpgList = { EpgList() },
    isInTimeShiftProvider: () -> Boolean = { false },
    currentPlaybackEpgProgrammeProvider: () -> EpgProgramme? = { null },
    videoPlayerMetadataProvider: () -> VideoPlayer.Metadata = { VideoPlayer.Metadata() },
    onShowEpg: () -> Unit = {},
    onShowChannelUrl: () -> Unit = {},
    onShowVideoPlayerController: () -> Unit = {},
    onShowVideoPlayerDisplayMode: () -> Unit = {},
    onShowMoreSettings: () -> Unit = {},
    onClearCache: () -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = childPadding.bottom),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            ChannelInfo(
                modifier = Modifier.padding(start = childPadding.start, end = childPadding.end),
                channelProvider = currentChannelProvider,
                channelUrlIdxProvider = currentChannelUrlIdxProvider,
                recentEpgProgrammeProvider = {
                    epgListProvider().recentProgramme(currentChannelProvider())
                },
                isInTimeShiftProvider = isInTimeShiftProvider,
                currentPlaybackEpgProgrammeProvider = currentPlaybackEpgProgrammeProvider,
            )

            ChannelPlayerInfo(
                modifier = Modifier.padding(start = childPadding.start, end = childPadding.end),
                resolutionProvider = {
                    val metadata = videoPlayerMetadataProvider()
                    metadata.videoWidth to metadata.videoHeight
                },
            )

            QuickOpBtnList(
                onShowEpg = onShowEpg,
                onShowChannelUrl = onShowChannelUrl,
                onShowVideoPlayerController = onShowVideoPlayerController,
                onShowVideoPlayerDisplayMode = onShowVideoPlayerDisplayMode,
                onShowMoreSettings = onShowMoreSettings,
                onClearCache = onClearCache,
                onUserAction = onUserAction,
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun QuickOpScreenPreview() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            QuickOpScreen(
                currentChannelProvider = { Channel.EXAMPLE },
                currentChannelNumberProvider = { "1" },
                epgListProvider = {
                    EpgList.example(ChannelList(listOf(Channel.EXAMPLE)))
                },
            )
        }
    }
}