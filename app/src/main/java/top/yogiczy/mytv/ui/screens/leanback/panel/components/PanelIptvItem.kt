package top.yogiczy.mytv.ui.screens.leanback.panel.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.CardDefaults
import top.yogiczy.mytv.data.entities.EpgProgramme
import top.yogiczy.mytv.data.entities.EpgProgramme.Companion.progress
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.handleLeanbackKeyEvents

@Composable
fun LeanbackPanelIptvItem(
    modifier: Modifier = Modifier,
    iptvProvider: () -> Iptv = { Iptv() },
    currentProgrammeProvider: () -> EpgProgramme? = { null },
    showProgrammeProgressProvider: () -> Boolean = { false },
    onIptvSelected: () -> Unit = {},
    onIptvFavoriteToggle: () -> Unit = {},
    onShowEpg: () -> Unit = {},
    initialFocusedProvider: () -> Boolean = { false },
    onHasFocused: () -> Unit = {},
    onFocused: () -> Unit = {},
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val iptv = iptvProvider()
    val currentProgramme = currentProgrammeProvider()
    val showProgrammeProgress = showProgrammeProgressProvider()

    LaunchedEffect(Unit) {
        if (initialFocusedProvider()) {
            onHasFocused()
            focusRequester.requestFocus()
        }
    }

    androidx.tv.material3.Card(
        onClick = { },
        modifier = modifier
            .width(130.dp)
            .height(54.dp)
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused || it.hasFocus
                if (isFocused) onFocused()
            }
            .handleLeanbackKeyEvents(
                onSelect = {
                    if (isFocused) onIptvSelected()
                    else focusRequester.requestFocus()
                },
                onLongSelect = {
                    if (isFocused) onIptvFavoriteToggle()
                    else focusRequester.requestFocus()
                },
                onSettings = {
                    if (isFocused) onShowEpg()
                    else focusRequester.requestFocus()
                }
            ),
        colors = CardDefaults.colors(
            containerColor = Color.Transparent,
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onBackground),
            ),
        ),
    ) {
        Box(
            modifier = Modifier.background(
                color = if (isFocused) MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.SpaceAround,
            ) {
                Text(
                    text = iptv.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    color = if (isFocused) MaterialTheme.colorScheme.background
                    else MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = currentProgramme?.title ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    modifier = Modifier.alpha(0.8f),
                    color = if (isFocused) MaterialTheme.colorScheme.background
                    else MaterialTheme.colorScheme.onBackground,
                )
            }

            // 节目进度条
            if (showProgrammeProgress && currentProgramme != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(currentProgramme.progress())
                        .height(3.dp)
                        .background(
                            if (isFocused) MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun LeanbackPanelIptvItemPreview() {
    LeanbackTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            LeanbackPanelIptvItem(
                iptvProvider = { Iptv.EXAMPLE },
                currentProgrammeProvider = {
                    EpgProgramme(
                        startAt = System.currentTimeMillis() - 100000,
                        endAt = System.currentTimeMillis() + 200000,
                        title = "新闻联播",
                    )
                },
                showProgrammeProgressProvider = { true },
            )

            LeanbackPanelIptvItem(
                iptvProvider = { Iptv.EXAMPLE },
                currentProgrammeProvider = {
                    EpgProgramme(
                        startAt = System.currentTimeMillis() - 100000,
                        endAt = System.currentTimeMillis() + 200000,
                        title = "新闻联播",
                    )
                },
                showProgrammeProgressProvider = { true },
                initialFocusedProvider = { true },
            )
        }
    }
}