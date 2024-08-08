package top.yogiczy.mytv.tv.ui.screens.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.epg.EpgProgramme
import top.yogiczy.mytv.core.data.entities.epg.EpgProgramme.Companion.progress
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeRecent
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screens.channel.components.ChannelInfo
import top.yogiczy.mytv.tv.ui.screens.channel.components.ChannelNumber
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.tooling.PreviewWithLayoutGrids

@Composable
fun ChannelTempScreen(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
    channelUrlIdxProvider: () -> Int = { 0 },
    channelNumberProvider: () -> Int = { 0 },
    recentEpgProgrammeProvider: () -> EpgProgrammeRecent? = { null },
    showEpgProgrammeProgressProvider: () -> Boolean = { false },
    isInTimeShiftProvider: () -> Boolean = { false },
    currentPlaybackEpgProgrammeProvider: () -> EpgProgramme? = { null },
) {
    val childPadding = rememberChildPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(childPadding.paddingValues),
    ) {
        ChannelNumber(
            modifier = Modifier.align(Alignment.TopEnd),
            channelNumberProvider = {
                channelNumberProvider().toString().padStart(2, '0')
            },
        )

        Layout(
            content = {
                ChannelInfo(
                    modifier = Modifier
                        .layoutId("info")
                        .sizeIn(maxWidth = 412.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    channelProvider = channelProvider,
                    channelUrlIdxProvider = channelUrlIdxProvider,
                    recentEpgProgrammeProvider = recentEpgProgrammeProvider,
                    isInTimeShiftProvider = isInTimeShiftProvider,
                    currentPlaybackEpgProgrammeProvider = currentPlaybackEpgProgrammeProvider,
                )

                if (currentPlaybackEpgProgrammeProvider() == null) {
                    recentEpgProgrammeProvider()?.now?.let { nowProgramme ->
                        if (showEpgProgrammeProgressProvider()) {
                            Box(
                                modifier = Modifier
                                    .layoutId("progress")
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth(nowProgramme.progress())
                                    .height(3.dp)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)),
                            )
                        }
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    MaterialTheme.shapes.medium,
                )
                .clip(MaterialTheme.shapes.medium),
        ) { measurables, constraints ->
            val infoPlaceable = measurables.find { it.layoutId == "info" }?.measure(constraints)
            val progressPlaceable = measurables.find { it.layoutId == "progress" }
                ?.measure(Constraints(maxWidth = infoPlaceable?.width ?: 0))

            layout(infoPlaceable?.width ?: 0, infoPlaceable?.height ?: 0) {
                infoPlaceable?.placeRelative(0, 0)
                progressPlaceable?.placeRelative(
                    0,
                    (infoPlaceable?.height ?: 0) - progressPlaceable.height,
                )
            }
        }
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
                showEpgProgrammeProgressProvider = { true },
            )
        }
    }
}