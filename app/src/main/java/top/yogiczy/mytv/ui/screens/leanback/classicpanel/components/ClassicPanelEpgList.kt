package top.yogiczy.mytv.ui.screens.leanback.classicpanel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ListItemDefaults
import kotlinx.coroutines.flow.distinctUntilChanged
import top.yogiczy.mytv.data.entities.Epg
import top.yogiczy.mytv.data.entities.EpgProgramme
import top.yogiczy.mytv.data.entities.EpgProgramme.Companion.isLive
import top.yogiczy.mytv.data.entities.EpgProgrammeList
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.handleLeanbackKeyEvents
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalComposeUiApi::class, ExperimentalTvFoundationApi::class)
@Composable
fun LeanbackClassicPanelEpgList(
    modifier: Modifier = Modifier,
    epgProvider: () -> Epg? = { Epg() },
    exitFocusRequesterProvider: () -> FocusRequester = { FocusRequester.Default },
    onUserAction: () -> Unit = {},
) {
    val dateFormat = SimpleDateFormat("E MM-dd", Locale.getDefault())
    val epg = epgProvider()

    if (epg != null && epg.programmes.isNotEmpty()) {
        val programmesGroup = remember(epg) {
            epg.programmes.groupBy { dateFormat.format(it.startAt) }
        }
        var currentDay by remember(programmesGroup) { mutableStateOf(dateFormat.format(System.currentTimeMillis())) }
        val programmes = remember(currentDay, programmesGroup) {
            programmesGroup.getOrElse(currentDay) { emptyList() }
        }

        val programmesListState = remember(programmes) {
            TvLazyListState(max(0, programmes.indexOfFirst { it.isLive() } - 2))
        }
        val daysListState = remember(programmesGroup) {
            TvLazyListState(max(0, programmesGroup.keys.indexOf(currentDay) - 2))
        }

        LaunchedEffect(programmesListState) {
            snapshotFlow { programmesListState.isScrollInProgress }
                .distinctUntilChanged()
                .collect { _ -> onUserAction() }
        }
        LaunchedEffect(daysListState) {
            snapshotFlow { daysListState.isScrollInProgress }
                .distinctUntilChanged()
                .collect { _ -> onUserAction() }
        }

        Row {
            TvLazyColumn(
                state = programmesListState,
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier
                    .fillMaxHeight()
                    .width(240.dp)
                    .background(MaterialTheme.colorScheme.background.copy(0.7f))
                    .focusProperties {
                        exit = {
                            if (it == FocusDirection.Left) exitFocusRequesterProvider()
                            else FocusRequester.Default
                        }
                    },
            ) {
                items(programmes) { programme ->
                    LeanbackClassicPanelEpgItem(
                        epgProgrammeProvider = { programme },
                    )
                }
            }

            if (programmesGroup.size > 1) {
                TvLazyColumn(
                    state = daysListState,
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = modifier
                        .fillMaxHeight()
                        .width(100.dp)
                        .background(MaterialTheme.colorScheme.background.copy(0.7f))
                ) {

                    items(programmesGroup.keys.toList()) {
                        LeanbackClassicPanelEpgDayItem(
                            dayProvider = { it },
                            currentDayProvider = { currentDay },
                            onChangeCurrentDay = { currentDay = it },
                        )
                    }
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

@Composable
private fun LeanbackClassicPanelEpgDayItem(
    modifier: Modifier = Modifier,
    dayProvider: () -> String = { "" },
    currentDayProvider: () -> String = { "" },
    onChangeCurrentDay: () -> Unit = {},
) {
    val day = dayProvider()

    val dateFormat = SimpleDateFormat("E MM-dd", Locale.getDefault())
    val today = dateFormat.format(System.currentTimeMillis())
    val tomorrow = dateFormat.format(System.currentTimeMillis() + 24 * 3600 * 1000)
    val dayAfterTomorrow =
        dateFormat.format(System.currentTimeMillis() + 48 * 3600 * 1000)

    val focusRequester = remember { FocusRequester() }
    val isSelected by remember(currentDayProvider()) { derivedStateOf { day == currentDayProvider() } }
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
                        if (isFocused) onChangeCurrentDay()
                        else focusRequester.requestFocus()
                    }
                ),
            colors = ListItemDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.onBackground,
                selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.5f
                ),
            ),
            selected = isSelected,
            onClick = {},
            headlineContent = {
                Column {
                    val key = day.split(" ")

                    Text(
                        text = when (day) {
                            today -> "今天"
                            tomorrow -> "明天"
                            dayAfterTomorrow -> "后天"
                            else -> key[0]
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = key[1],
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
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
                        List(200) { index ->
                            EpgProgramme(
                                title = "节目$index",
                                startAt = System.currentTimeMillis() - 3600 * 1000 * 24 * 5 + index * 3600 * 1000,
                                endAt = System.currentTimeMillis() - 3600 * 1000 * 24 * 5 + index * 3600 * 1000 + 3600 * 1000
                            )
                        }
                    )
                )
            }
        )
    }
}