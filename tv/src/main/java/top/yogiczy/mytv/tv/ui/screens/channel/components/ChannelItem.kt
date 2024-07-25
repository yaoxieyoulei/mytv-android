package top.yogiczy.mytv.tv.ui.screens.channel.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.epg.EpgProgramme.Companion.progress
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeRecent
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents

@Composable
fun ChannelItem(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
    onChannelSelected: () -> Unit = {},
    onChannelFavoriteToggle: () -> Unit = {},
    recentEpgProgrammeProvider: () -> EpgProgrammeRecent? = { null },
    showEpgProgrammeProgressProvider: () -> Boolean = { false },
    initialFocusedProvider: () -> Boolean = { false },
    onInitialFocused: () -> Unit = {},
) {
    val initialFocused = initialFocusedProvider()

    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (initialFocused) {
            onInitialFocused()
            focusRequester.requestFocus()
        }
    }

    Card(
        onClick = {},
        modifier = modifier
            .size(124.dp, 53.dp)
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused || it.hasFocus
            }
            .handleKeyEvents(
                isFocused = { isFocused },
                focusRequester = focusRequester,
                onSelect = onChannelSelected,
                onLongSelect = onChannelFavoriteToggle,
            ),
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface.copy(0.8f),
            focusedContainerColor = MaterialTheme.colorScheme.onSurface,
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface),
            ),
        ),
    ) {
        Box {
            ChannelItemContent(
                channelProvider = channelProvider,
                recentEpgProgrammeProvider = recentEpgProgrammeProvider,
            )

            ChannelItemProgress(
                recentEpgProgrammeProvider = recentEpgProgrammeProvider,
                showEpgProgrammeProgressProvider = showEpgProgrammeProgressProvider,
                isFocusedProvider = { isFocused },
                modifier = Modifier.align(Alignment.BottomStart),
            )
        }
    }
}

@Composable
private fun ChannelItemContent(
    modifier: Modifier = Modifier,
    channelProvider: () -> Channel = { Channel() },
    recentEpgProgrammeProvider: () -> EpgProgrammeRecent? = { null },
) {
    val channel = channelProvider()
    val recentEpgProgramme = recentEpgProgrammeProvider()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        Text(
            channel.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
        )
        Text(
            recentEpgProgramme?.now?.title ?: "",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.alpha(0.8f),
            maxLines = 1,
        )
    }
}

@Composable
fun ChannelItemProgress(
    modifier: Modifier = Modifier,
    recentEpgProgrammeProvider: () -> EpgProgrammeRecent? = { null },
    showEpgProgrammeProgressProvider: () -> Boolean = { false },
    isFocusedProvider: () -> Boolean = { false },
) {
    val recentEpgProgramme = recentEpgProgrammeProvider()
    val showEpgProgrammeProgress = showEpgProgrammeProgressProvider()
    val isFocused = isFocusedProvider()

    recentEpgProgramme?.now?.let { nowProgramme ->
        if (showEpgProgrammeProgress) {
            Box(
                modifier = modifier
                    .fillMaxWidth(nowProgramme.progress())
                    .height(2.dp)
                    .background(
                        if (isFocused) MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun ChannelItemPreview() {
    MyTVTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ChannelItem(
                channelProvider = { Channel.EXAMPLE },
                recentEpgProgrammeProvider = { EpgProgrammeRecent.EXAMPLE },
                showEpgProgrammeProgressProvider = { true },
            )

            ChannelItem(
                channelProvider = { Channel.EXAMPLE },
                recentEpgProgrammeProvider = { EpgProgrammeRecent.EXAMPLE },
                showEpgProgrammeProgressProvider = { true },
                initialFocusedProvider = { true },
            )
        }
    }
}