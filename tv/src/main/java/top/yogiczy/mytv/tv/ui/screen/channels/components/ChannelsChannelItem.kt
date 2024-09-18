package top.yogiczy.mytv.tv.ui.screen.channels.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import coil.compose.SubcomposeAsyncImage
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.epg.EpgProgramme.Companion.progress
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeRecent
import top.yogiczy.mytv.core.util.utils.M3u8AnalysisUtil
import top.yogiczy.mytv.core.util.utils.isIPv6
import top.yogiczy.mytv.core.util.utils.urlHost
import top.yogiczy.mytv.tv.ui.screen.settings.LocalSettings
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.utils.Configs
import top.yogiczy.mytv.tv.ui.utils.gridColumns
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse

@Composable
fun ChannelsChannelItem(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
    onChannelSelected: () -> Unit = {},
    onChannelFavoriteToggle: () -> Unit = {},
    recentEpgProgrammeProvider: () -> EpgProgrammeRecent? = { null },
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .width(2.4f.gridColumns())
            .handleKeyEvents(
                onSelect = onChannelSelected,
                onLongSelect = onChannelFavoriteToggle,
            ),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.onSurface.copy(0.1f),
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)),
        ),
        onClick = {},
    ) {
        Column {
            ChannelsChannelItemLogo(
                channelProvider = channelProvider,
                isFocusedProvider = { isFocused },
            )

            Box(modifier = Modifier.height(56.dp)) {
                ChannelsChannelItemContent(
                    channelProvider = channelProvider,
                    recentEpgProgrammeProvider = recentEpgProgrammeProvider,
                    isFocusedProvider = { isFocused },
                )

                ChannelsChannelItemProgress(
                    recentEpgProgrammeProvider = recentEpgProgrammeProvider,
                    modifier = Modifier.align(Alignment.BottomStart),
                )
            }
        }

        ChannelsChannelItemTagList(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            channelProvider = channelProvider,
            isFocusedProvider = { isFocused },
        )
    }
}

@Composable
private fun ChannelsChannelItemLogo(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
    isFocusedProvider: () -> Boolean = { false },
) {
    if (!LocalSettings.current.uiShowChannelLogo) return

    val channel = channelProvider()
    val isFocused = isFocusedProvider()

    val settings = LocalSettings.current

    var preview by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(channel) {
        preview = null

        if (settings.uiShowChannelPreview) {
            val line = channel.lineList.firstOrNull {
                Configs.iptvPlayableHostList.contains(it.url.urlHost())
            } ?: channel.lineList.first()
            preview = M3u8AnalysisUtil.getFirstFrame(line.url)
        }
    }

    Box(
        modifier = modifier
            .background(
                if (isFocused) MaterialTheme.colorScheme.surface.copy(0.9f)
                else MaterialTheme.colorScheme.surface.copy(0.5f)
            )
            .fillMaxWidth()
            .aspectRatio(16 / 9f),
    ) {
        AnimatedVisibility(
            preview != null,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            val emptyBitmap = ImageBitmap(1, 1)

            Image(
                painter = BitmapPainter(preview?.asImageBitmap() ?: emptyBitmap),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }

        if (preview == null) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(0.6f),
                model = channel.logo,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun ChannelsChannelItemContent(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
    recentEpgProgrammeProvider: () -> EpgProgrammeRecent? = { null },
    isFocusedProvider: () -> Boolean = { false },
) {
    val isFocused = isFocusedProvider()

    val channel = channelProvider()
    val recentEpgProgramme = recentEpgProgrammeProvider()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        Text(
            channel.name,
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.ifElse(isFocused, Modifier.basicMarquee()),
        )

        Text(
            recentEpgProgramme?.now?.title ?: "",
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .alpha(0.8f)
                .ifElse(isFocused, Modifier.basicMarquee()),
        )
    }
}

@Composable
private fun ChannelsChannelItemProgress(
    modifier: Modifier = Modifier,
    recentEpgProgrammeProvider: () -> EpgProgrammeRecent? = { null },
) {
    val recentEpgProgramme = recentEpgProgrammeProvider()
    val showEpgProgrammeProgress = LocalSettings.current.uiShowEpgProgrammeProgress

    recentEpgProgramme?.now?.let { nowProgramme ->
        if (showEpgProgrammeProgress) {
            Box(
                modifier = modifier
                    .fillMaxWidth(nowProgramme.progress())
                    .height(2.dp)
                    .background(LocalContentColor.current.copy(0.8f)),
            )
        }
    }
}

@Composable
private fun ChannelsChannelItemTag(
    modifier: Modifier = Modifier,
    text: String,
    isFocusedProvider: () -> Boolean = { false },
) {
    val isFocused = isFocusedProvider()

    Surface(
        modifier = modifier.height(20.dp),
        colors = SurfaceDefaults.colors(
            containerColor = if (isFocused) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.surface.copy(0.5f),
        ),
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            lineHeight = TextUnit(12f, TextUnitType.Sp),
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 2.dp)
                .align(Alignment.Center),
        )
    }
}

@Composable
private fun ChannelsChannelItemTagList(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
    isFocusedProvider: () -> Boolean = { false },
) {
    if (!LocalSettings.current.uiShowChannelLogo) return

    val channel = channelProvider()

    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (channel.lineList.size > 1) {
            ChannelsChannelItemTag(
                text = "${channel.lineList.size}线路",
                isFocusedProvider = isFocusedProvider,
            )
        }

        if (channel.lineList.all { it.url.isIPv6() }) {
            ChannelsChannelItemTag(
                text = "IPV6",
                isFocusedProvider = isFocusedProvider,
            )
        }
    }
}

@Preview
@Composable
private fun ChannelsChannelItemPreview() {
    MyTvTheme {
        ChannelsChannelItem(
            modifier = Modifier.padding(16.dp),
            channelProvider = { Channel.EXAMPLE },
            recentEpgProgrammeProvider = { EpgProgrammeRecent.EXAMPLE },
        )
    }
}