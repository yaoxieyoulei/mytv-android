package top.yogiczy.mytv.ui.screens.leanback.classicpanel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ListItemDefaults
import kotlinx.coroutines.flow.distinctUntilChanged
import top.yogiczy.mytv.data.entities.Epg
import top.yogiczy.mytv.data.entities.EpgProgramme
import top.yogiczy.mytv.data.entities.EpgProgramme.Companion.isLive
import top.yogiczy.mytv.data.entities.EpgProgrammeList
import top.yogiczy.mytv.ui.rememberLeanbackChildPadding
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.handleLeanbackKeyEvents
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LeanbackClassicPanelEpgList(
    modifier: Modifier = Modifier,
    epgProvider: () -> Epg? = { Epg() },
    exitFocusRequesterProvider: () -> FocusRequester = { FocusRequester.Default },
    onUserAction: () -> Unit = {},
) {
    val childPadding = rememberLeanbackChildPadding()

    val epg = epgProvider()

    if (epg != null && epg.programmes.isNotEmpty()) {
        val listState = remember(epg) {
            TvLazyListState(max(0, epg.programmes.indexOfFirst { it.isLive() } - 2))
        }

        LaunchedEffect(listState) {
            snapshotFlow { listState.isScrollInProgress }
                .distinctUntilChanged()
                .collect { _ -> onUserAction() }
        }

        Column(
            modifier = modifier.width(240.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = "节目单", style = MaterialTheme.typography.titleMedium)

            TvLazyColumn(
                state = listState,
                contentPadding = PaddingValues(top = 8.dp, bottom = childPadding.bottom),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .focusProperties {
                        exit = {
                            exitFocusRequesterProvider()
                        }
                    },
            ) {
                items(epg.programmes) { programme ->
                    LeanbackClassicPanelEpgItem(
                        epgProgrammeProvider = { programme },
                    )
                }
            }
        }
    }
}

@Composable
private fun LeanbackClassicPanelEpgItem(
    modifier: Modifier = Modifier,
    epgProgrammeProvider: () -> EpgProgramme = { EpgProgramme() },
) {
    val programme = epgProgrammeProvider()
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalContentColor provides if (isFocused) MaterialTheme.colorScheme.background
        else MaterialTheme.colorScheme.onBackground
    ) {
        androidx.tv.material3.ListItem(
            modifier = modifier
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused || it.hasFocus
                }
                .handleLeanbackKeyEvents(
                    onSelect = {
                        focusRequester.requestFocus()
                    },
                ),
            colors = ListItemDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.onBackground,
                selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.5f
                ),
            ),
            selected = programme.isLive(),
            onClick = { },
            headlineContent = {
                Text(
                    text = programme.title,
                    maxLines = if (isFocused) Int.MAX_VALUE else 1,
                )
            },
            overlineContent = {
                val start = timeFormat.format(programme.startAt)
                val end = timeFormat.format(programme.endAt)
                Text(
                    text = "$start  ~ $end",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.alpha(0.8f),
                )
            },
            trailingContent = {
                if (programme.isLive()) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "playing")
                }
            },
        )
    }
}

@Preview
@Composable
private fun LeanbackClassicPanelEpgListPreview() {
    LeanbackTheme {
        LeanbackClassicPanelEpgList(
            epgProvider = {
                Epg(
                    channel = "CCTV1", programmes = EpgProgrammeList(
                        listOf(
                            EpgProgramme(
                                title = "节目1".repeat(10),
                                startAt = System.currentTimeMillis(),
                                endAt = System.currentTimeMillis() + 3600_000,
                            ),
                            EpgProgramme(
                                title = "节目2",
                                startAt = System.currentTimeMillis() + 3600_000,
                                endAt = System.currentTimeMillis() + 7200_000,
                            ),
                            EpgProgramme(
                                title = "节目3",
                                startAt = System.currentTimeMillis() + 7200_000,
                                endAt = System.currentTimeMillis() + 10800_000,
                            ),
                        )
                    )
                )
            }
        )
    }
}