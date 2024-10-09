package top.yogiczy.mytv.tv.ui.screens.channel.components

import android.net.TrafficStats
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
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
import androidx.tv.material3.Text
import kotlinx.coroutines.delay
import top.yogiczy.mytv.core.data.entities.channel.Channel
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
import top.yogiczy.mytv.tv.ui.screens.videoplayer.player.VideoPlayer
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChannelInfo(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
    channelUrlIdxProvider: () -> Int = { 0 },
    recentEpgProgrammeProvider: () -> EpgProgrammeRecent? = { null },
    isInTimeShiftProvider: () -> Boolean = { false },
    currentPlaybackEpgProgrammeProvider: () -> EpgProgramme? = { null },
    videoPlayerMetadataProvider: () -> VideoPlayer.Metadata = { VideoPlayer.Metadata() },
    showChannelLogoProvider: () -> Boolean = { false },
    dense: Boolean = false,
) {
    val currentPlaybackEpgProgramme = currentPlaybackEpgProgrammeProvider()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (showChannelLogoProvider()) {
            ChannelItemLogo(
                modifier = Modifier
                    .height(94.dp)
                    .aspectRatio(16 / 9f),
                logoProvider = { channelProvider().logo },
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ChannelInfoTitle(
                channelProvider = channelProvider,
                channelLineIdxProvider = channelUrlIdxProvider,
                isInTimeShiftProvider = isInTimeShiftProvider,
                currentPlaybackEpgProgrammeProvider = currentPlaybackEpgProgrammeProvider,
                playerMetadataProvider = videoPlayerMetadataProvider,
                dense = dense,
            )

            if (currentPlaybackEpgProgramme != null) {
                ChannelInfoEpgProgramme(
                    programmeProvider = { currentPlaybackEpgProgramme },
                    showProgress = false,
                )

            } else {
                ChannelInfoEpgProgramme(
                    programmeProvider = { recentEpgProgrammeProvider()?.now },
                    showProgress = true,
                )
                ChannelInfoEpgProgramme(
                    programmeProvider = { recentEpgProgrammeProvider()?.next },
                    showProgress = false,
                )
            }
        }
    }
}

@Composable
private fun ChannelInfoTags(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
    channelLineIdxProvider: () -> Int = { 0 },
    isInTimeShiftProvider: () -> Boolean = { false },
    currentPlaybackEpgProgrammeProvider: () -> EpgProgramme? = { null },
    playerMetadataProvider: () -> VideoPlayer.Metadata = { VideoPlayer.Metadata() },
) {
    val channel = channelProvider()
    val channelLineIdx = channelLineIdxProvider()
    val line = channel.urlList[channelLineIdx]
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

        if (channel.urlList.size > 1) {
            Tag("${channelLineIdx + 1}/${channel.urlList.size}", colors = tagColors)
        }

        if (ChannelUtil.isHybridWebViewUrl(line)) {
            Tag(ChannelUtil.getHybridWebViewUrlProvider(line), colors = tagColors)
        } else {
            if (line.isIPv6()) Tag("IPv6", colors = tagColors)
        }

        if (playerMetadata.videoWidth * playerMetadata.videoHeight > 0) {
            Tag(
                when (playerMetadata.videoHeight) {
                    240 -> "240p"
                    360 -> "360p"
                    480 -> "480p"
                    720 -> "HD"
                    1080 -> "FHD"
                    1440 -> "QHD"
                    2160 -> "4K UHD"
                    4320 -> "8K UHD"
                    else -> "${playerMetadata.videoWidth}x${playerMetadata.videoHeight}"
                },
                colors = tagColors,
            )
        }

        if (playerMetadata.videoFrameRate > 0) {
            Tag("${playerMetadata.videoFrameRate.toInt()}FPS", colors = tagColors)
        }

        if (playerMetadata.audioChannels > 0) {
            Tag(
                when (playerMetadata.audioChannels) {
                    1 -> "单声道"
                    2 -> "立体声"
                    3 -> "2.1 声道"
                    4 -> "4.0 四声道"
                    5 -> "5.0 环绕声"
                    6 -> "5.1 环绕声"
                    7 -> "6.1 环绕声"
                    8 -> "7.1 环绕声"
                    10 -> "7.1.2 杜比全景声"
                    12 -> "7.1.4 杜比全景声"
                    else -> "${playerMetadata.audioChannels}声道"
                },
                colors = tagColors,
            )
        }
    }
}

@Composable
private fun ChannelInfoExtra(
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
        ChannelInfoTags(
            channelProvider = channelProvider,
            channelLineIdxProvider = channelLineIdxProvider,
            isInTimeShiftProvider = isInTimeShiftProvider,
            currentPlaybackEpgProgrammeProvider = currentPlaybackEpgProgrammeProvider,
            playerMetadataProvider = playerMetadataProvider,
        )

        ChannelInfoNetSpeed()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChannelInfoTitle(
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

            ChannelInfoExtra(
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
                modifier = Modifier.sizeIn(maxWidth = 340.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            ChannelInfoExtra(
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
private fun ChannelInfoEpgProgramme(
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
fun ChannelInfoNetSpeed(
    modifier: Modifier = Modifier,
    netSpeed: Long = rememberNetSpeed(),
) {
    Text(
        text = if (netSpeed < 1024 * 999) "${netSpeed / 1024}KB/s"
        else "${DecimalFormat("#.#").format(netSpeed / 1024 / 1024f)}MB/s",
        modifier = modifier.sizeIn(minWidth = 60.dp),
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
private fun ChannelInfoPreview() {
    MyTVTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ChannelInfo(
                channelProvider = { Channel.EXAMPLE },
                channelUrlIdxProvider = { 1 },
                recentEpgProgrammeProvider = { EpgProgrammeRecent.EXAMPLE },
                isInTimeShiftProvider = { true },
                showChannelLogoProvider = { true },
            )

            ChannelInfo(channelProvider = { Channel.EXAMPLE },
                channelUrlIdxProvider = { 0 },
                recentEpgProgrammeProvider = { EpgProgrammeRecent.EXAMPLE },
                currentPlaybackEpgProgrammeProvider = { EpgProgramme(title = "回放电视节目") })

            ChannelInfo(
                channelProvider = {
                    Channel.EXAMPLE.copy(
                        urlList = ChannelUtil.getHybridWebViewUrl("cctv1") ?: emptyList()
                    )
                },
                channelUrlIdxProvider = { 0 },
                recentEpgProgrammeProvider = { EpgProgrammeRecent.EXAMPLE },
            )
        }
    }
}