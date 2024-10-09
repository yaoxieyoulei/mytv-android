package top.yogiczy.mytv.tv.ui.screens.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.epg.EpgProgramme
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeRecent
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screens.channel.components.ChannelInfo
import top.yogiczy.mytv.tv.ui.screens.channel.components.ChannelNumber
import top.yogiczy.mytv.tv.ui.screens.videoplayer.player.VideoPlayer
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids

@Composable
fun ChannelTempScreen(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
    channelUrlIdxProvider: () -> Int = { 0 },
    channelNumberProvider: () -> Int = { 0 },
    showChannelLogoProvider: () -> Boolean = { false },
    recentEpgProgrammeProvider: () -> EpgProgrammeRecent? = { null },
    isInTimeShiftProvider: () -> Boolean = { false },
    currentPlaybackEpgProgrammeProvider: () -> EpgProgramme? = { null },
    videoPlayerMetadataProvider: () -> VideoPlayer.Metadata = { VideoPlayer.Metadata() },
) {
    val childPadding = rememberChildPadding()

    Box(modifier = modifier.fillMaxSize()) {
        ChannelNumber(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = childPadding.top, end = childPadding.end),
            channelNumberProvider = { channelNumberProvider().toString().padStart(2, '0') },
        )

        ChannelInfo(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x00000000),
                            Color(0xB4000000),
                            Color(0xF3000000),
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY),
                    )
                )
                .padding(childPadding.paddingValues),
            channelProvider = channelProvider,
            channelUrlIdxProvider = channelUrlIdxProvider,
            recentEpgProgrammeProvider = recentEpgProgrammeProvider,
            isInTimeShiftProvider = isInTimeShiftProvider,
            currentPlaybackEpgProgrammeProvider = currentPlaybackEpgProgrammeProvider,
            videoPlayerMetadataProvider = videoPlayerMetadataProvider,
            showChannelLogoProvider = showChannelLogoProvider,
        )
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun ChannelTempScreenPreview() {
    MyTVTheme {
        PreviewWithLayoutGrids {
            ChannelTempScreen(
                channelProvider = { Channel.EXAMPLE.copy(name = "长标题".repeat(4)) },
                channelUrlIdxProvider = { 0 },
                channelNumberProvider = { 8 },
                recentEpgProgrammeProvider = { EpgProgrammeRecent.EXAMPLE },
            )
        }
    }
}