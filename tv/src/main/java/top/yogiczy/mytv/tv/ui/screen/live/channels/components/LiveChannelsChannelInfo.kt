package top.yogiczy.mytv.tv.ui.screen.live.channels.components

import android.net.TrafficStats
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.delay
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.channel.ChannelLine
import top.yogiczy.mytv.core.data.entities.epg.EpgProgramme
import top.yogiczy.mytv.core.data.entities.epg.EpgProgramme.Companion.progress
import top.yogiczy.mytv.core.data.entities.epg.EpgProgramme.Companion.remainingMinutes
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeRecent
import top.yogiczy.mytv.core.data.utils.ChannelUtil
import top.yogiczy.mytv.core.util.utils.isIPv6
import top.yogiczy.mytv.tv.ui.material.ProgressBar
import top.yogiczy.mytv.tv.ui.material.ProgressBarColors
import top.yogiczy.mytv.tv.ui.material.Tag
import top.yogiczy.mytv.tv.ui.material.TagDefaults
import top.yogiczy.mytv.tv.ui.screen.settings.settingsVM
import top.yogiczy.mytv.tv.ui.screensold.videoplayer.player.VideoPlayer
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.utils.gridColumns
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LiveChannelsChannelInfo(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
    channelLineIdxProvider: () -> Int = { 0 },
    recentEpgProgrammeProvider: () -> EpgProgrammeRecent? = { null },
    isInTimeShiftProvider: () -> Boolean = { false },
    currentPlaybackEpgProgrammeProvider: () -> EpgProgramme? = { null },
    playerMetadataProvider: () -> VideoPlayer.Metadata = { VideoPlayer.Metadata() },
    dense: Boolean = false,
    showChannelLogo: Boolean = settingsVM.uiShowChannelLogo,
) {
    val currentPlaybackEpgProgramme = currentPlaybackEpgProgrammeProvider()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if(showChannelLogo) {
            LiveChannelsChannelInfoLogo(
                channelProvider = channelProvider,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            LiveChannelsChannelInfoTitle(
                channelProvider = channelProvider,
                channelLineIdxProvider = channelLineIdxProvider,
                isInTimeShiftProvider = isInTimeShiftProvider,
                currentPlaybackEpgProgrammeProvider = currentPlaybackEpgProgrammeProvider,
                playerMetadataProvider = playerMetadataProvider,
                dense = dense,
            )

            if (currentPlaybackEpgProgramme != null) {
                LiveChannelsChannelInfoEpgProgramme(
                    programmeProvider = { currentPlaybackEpgProgramme },
                    showProgress = false,
                )

            } else {
                LiveChannelsChannelInfoEpgProgramme(
                    programmeProvider = { recentEpgProgrammeProvider()?.now },
                    showProgress = true,
                )
                LiveChannelsChannelInfoEpgProgramme(
                    programmeProvider = { recentEpgProgrammeProvider()?.next },
                    showProgress = false,
                )
            }
        }
    }
}

@Composable
private fun LiveChannelsChannelInfoLogo(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
) {
    val channel = channelProvider()

    SubcomposeAsyncImage(
        modifier = modifier
            .height(94.dp)
            .aspectRatio(16 / 9f),
        model = channel.logo,
        contentDescription = null,
        loading = { LiveChannelsChannelInfoNo(channelProvider = channelProvider) },
        error = { LiveChannelsChannelInfoNo(channelProvider = channelProvider) },
    )
}

@Composable
private fun LiveChannelsChannelInfoNo(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
) {
    val channel = channelProvider()

    Surface(
        modifier = modifier.fillMaxSize(),
        colors = SurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.inverseOnSurface.copy(0.5f),
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
            channel.no,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
private fun LiveChannelsChannelInfoTags(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
    channelLineIdxProvider: () -> Int = { 0 },
    isInTimeShiftProvider: () -> Boolean = { false },
    currentPlaybackEpgProgrammeProvider: () -> EpgProgramme? = { null },
    playerMetadataProvider: () -> VideoPlayer.Metadata = { VideoPlayer.Metadata() },
) {
    val channel = channelProvider()
    val channelLineIdx = channelLineIdxProvider()
    val line = channel.lineList[channelLineIdx]
    val isInTimeShift = isInTimeShiftProvider()
    val currentPlaybackEpgProgramme = currentPlaybackEpgProgrammeProvider()
    val playerMetadata = playerMetadataProvider()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val tagColors = TagDefaults.colors(
            containerColor = MaterialTheme.colorScheme.inverseSurface.copy(0.8f),
            contentColor = MaterialTheme.colorScheme.surface,
        )

        if (isInTimeShift) {
            Tag("时移", colors = tagColors)
        }

        if (currentPlaybackEpgProgramme != null) {
            Tag("回放", colors = tagColors)
        }

        if (channel.lineList.size > 1) {
            Tag("${channelLineIdx + 1}/${channel.lineList.size}", colors = tagColors)
        }

        if (line.hybridType == ChannelLine.HybridType.WebView) {
            Tag(ChannelUtil.getHybridWebViewUrlProvider(line.url), colors = tagColors)
        } else {
            if (line.url.isIPv6()) Tag("IPv6", colors = tagColors)
        }

        if (playerMetadata.videoWidth * playerMetadata.videoHeight > 0) {
            when (playerMetadata.videoWidth to playerMetadata.videoHeight) {
                426 to 240 -> Tag("240p", colors = tagColors)
                640 to 360 -> Tag("360p", colors = tagColors)
                854 to 480 -> Tag("480p", colors = tagColors)
                1280 to 720 -> Tag("HD", colors = tagColors)
                1920 to 1080 -> Tag("FHD", colors = tagColors)
                2560 to 1440 -> Tag("QHD", colors = tagColors)
                3840 to 2160 -> Tag("4K UHD", colors = tagColors)
                7680 to 4320 -> Tag("8K UHD", colors = tagColors)
                else -> Tag(
                    "${playerMetadata.videoWidth}x${playerMetadata.videoHeight}",
                    colors = tagColors
                )
            }
        }

        if (playerMetadata.audioChannels > 0) {
            when (playerMetadata.audioChannels) {
                1 -> Tag("单声道", colors = tagColors)
                2 -> Tag("立体声", colors = tagColors)
                3 -> Tag("2.1 声道", colors = tagColors)
                4 -> Tag("4.0 四声道", colors = tagColors)
                5 -> Tag("5.0 环绕声", colors = tagColors)
                6 -> Tag("5.1 环绕声", colors = tagColors)
                7 -> Tag("6.1 环绕声", colors = tagColors)
                8 -> Tag("7.1 环绕声", colors = tagColors)
                10 -> Tag("7.1.2 杜比全景声", colors = tagColors)
                12 -> Tag("7.1.4 杜比全景声", colors = tagColors)
                else -> Tag("${playerMetadata.audioChannels}声道", colors = tagColors)
            }
        }
    }
}

@Composable
private fun LiveChannelsChannelInfoExtra(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
    channelLineIdxProvider: () -> Int = { 0 },
    isInTimeShiftProvider: () -> Boolean = { false },
    currentPlaybackEpgProgrammeProvider: () -> EpgProgramme? = { null },
    playerMetadataProvider: () -> VideoPlayer.Metadata = { VideoPlayer.Metadata() },
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        LiveChannelsChannelInfoTags(
            channelProvider = channelProvider,
            channelLineIdxProvider = channelLineIdxProvider,
            isInTimeShiftProvider = isInTimeShiftProvider,
            currentPlaybackEpgProgrammeProvider = currentPlaybackEpgProgrammeProvider,
            playerMetadataProvider = playerMetadataProvider,
        )

        LiveChannelsChannelInfoNetSpeed()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LiveChannelsChannelInfoTitle(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
    channelLineIdxProvider: () -> Int = { 0 },
    isInTimeShiftProvider: () -> Boolean = { false },
    currentPlaybackEpgProgrammeProvider: () -> EpgProgramme? = { null },
    playerMetadataProvider: () -> VideoPlayer.Metadata = { VideoPlayer.Metadata() },
    dense: Boolean = false,
) {
    val channel = channelProvider()

    if (dense) {
        FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val density = LocalDensity.current
            var heightPx by remember { mutableIntStateOf(0) }
            val heightDp = remember(heightPx) { with(density) { heightPx.toDp() } }

            Text(
                channel.name,
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.onSizeChanged { heightPx = it.height },
            )

            LiveChannelsChannelInfoExtra(
                modifier = Modifier
                    .height(heightDp)
                    .padding(bottom = 5.dp),
                channelProvider = channelProvider,
                channelLineIdxProvider = channelLineIdxProvider,
                isInTimeShiftProvider = isInTimeShiftProvider,
                currentPlaybackEpgProgrammeProvider = currentPlaybackEpgProgrammeProvider,
                playerMetadataProvider = playerMetadataProvider,
            )
        }
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                channel.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.sizeIn(maxWidth = 5.gridColumns()),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            LiveChannelsChannelInfoExtra(
                modifier = Modifier.padding(bottom = 5.dp),
                channelProvider = channelProvider,
                channelLineIdxProvider = channelLineIdxProvider,
                isInTimeShiftProvider = isInTimeShiftProvider,
                currentPlaybackEpgProgrammeProvider = currentPlaybackEpgProgrammeProvider,
                playerMetadataProvider = playerMetadataProvider,
            )
        }
    }
}

@Composable
private fun LiveChannelsChannelInfoEpgProgramme(
    modifier: Modifier = Modifier,
    programmeProvider: () -> EpgProgramme? = { null },
    showProgress: Boolean = false,
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val programme = programmeProvider()

    if (programme == null) {
        if (showProgress) {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "00:00 — 23:59",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.alpha(0.8f),
                    maxLines = 1,
                )

                Text(
                    "精彩节目",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.alpha(0.8f),
                    maxLines = 1,
                )
            }
        }

        return
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "${timeFormat.format(programme.startAt)} — ${timeFormat.format(programme.endAt)}",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.alpha(0.8f),
            maxLines = 1,
        )

        if (showProgress) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProgressBar(
                    process = programme.progress(),
                    modifier = Modifier.size(60.dp, 5.dp),
                    colors = ProgressBarColors(
                        barColor = MaterialTheme.colorScheme.onSurface.copy(0.2f),
                        progressColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )

                Text(
                    "${programme.remainingMinutes()}分钟",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.alpha(0.8f),
                )
            }
        }

        Text(
            programme.title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.alpha(0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun LiveChannelsChannelInfoNetSpeed(
    modifier: Modifier = Modifier,
    netSpeed: Long = rememberNetSpeed(),
) {
    Text(
        text = if (netSpeed < 1024 * 999) "${netSpeed / 1024}KB/s"
        else "${DecimalFormat("#.#").format(netSpeed / 1024 / 1024f)}MB/s",
        modifier = modifier,
    )
}

@Composable
private fun rememberNetSpeed(): Long {
    var netSpeed by remember { mutableLongStateOf(0) }

    LaunchedEffect(Unit) {
        var lastTotalRxBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid())
        var lastTimeStamp = System.currentTimeMillis()

        while (true) {
            delay(1000)
            val nowTotalRxBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid())
            val nowTimeStamp = System.currentTimeMillis()
            val speed = (nowTotalRxBytes - lastTotalRxBytes) / (nowTimeStamp - lastTimeStamp) * 1000
            lastTimeStamp = nowTimeStamp
            lastTotalRxBytes = nowTotalRxBytes

            netSpeed = speed
        }
    }

    return netSpeed
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun LiveChannelsChannelInfoPreview() {
    MyTvTheme {
        LiveChannelsChannelInfo(
            channelProvider = { Channel.EXAMPLE.copy(name = "CCTV-1 法治与法治".repeat(3)) },
            channelLineIdxProvider = { 1 },
            recentEpgProgrammeProvider = {
                EpgProgrammeRecent.EXAMPLE.copy(
                    now = EpgProgrammeRecent.EXAMPLE.now?.copy(
                        title = "2023/2024赛季中国男子篮球职业联赛季后赛12进8第五场".repeat(
                            2
                        )
                    )
                )
            },
            playerMetadataProvider = {
                VideoPlayer.Metadata(
                    videoWidth = 7680,
                    videoHeight = 4320,
                    audioChannels = 12,
                )
            },
            showChannelLogo = true,
        )
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun LiveChannelsChannelInfoPlaybackPreview() {
    MyTvTheme {
        LiveChannelsChannelInfo(
            channelProvider = { Channel.EXAMPLE },
            channelLineIdxProvider = { 1 },
            currentPlaybackEpgProgrammeProvider = { EpgProgramme.EXAMPLE },
            showChannelLogo = true,
        )
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun LiveChannelsChannelInfoNoLogoPreview() {
    MyTvTheme {
        LiveChannelsChannelInfo(
            modifier = Modifier.width(6.gridColumns()),
            channelProvider = { Channel.EXAMPLE.copy(name = "中文") },
            channelLineIdxProvider = { 1 },
            recentEpgProgrammeProvider = { EpgProgrammeRecent.EXAMPLE },
            playerMetadataProvider = {
                VideoPlayer.Metadata(
                    videoWidth = 7680,
                    videoHeight = 4320,
                )
            },
            dense = true,
        )
    }
}