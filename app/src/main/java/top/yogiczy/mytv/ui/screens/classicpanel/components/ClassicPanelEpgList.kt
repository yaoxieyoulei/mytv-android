package top.yogiczy.mytv.ui.screens.classicpanel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.entities.Epg
import top.yogiczy.mytv.data.entities.EpgProgramme
import top.yogiczy.mytv.data.entities.EpgProgramme.Companion.isLive
import top.yogiczy.mytv.data.entities.EpgProgrammeList
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.panel.PanelAutoCloseState
import top.yogiczy.mytv.ui.screens.panel.rememberPanelAutoCloseState
import top.yogiczy.mytv.ui.theme.MyTVTheme
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ClassicPanelEpgList(
    modifier: Modifier = Modifier,
    epg: Epg = Epg.EMPTY,
    currentIdx: Int = max(0, epg.programmes.indexOfFirst { it.isLive() }),
    state: TvLazyListState = rememberTvLazyListState(currentIdx),
    exitFocusRequester: FocusRequester = FocusRequester.Default,
    panelAutoCloseState: PanelAutoCloseState = rememberPanelAutoCloseState(),
) {
    val childPadding = rememberChildPadding()
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    LaunchedEffect(state.firstVisibleItemIndex) { panelAutoCloseState.active() }
    LaunchedEffect(currentIdx) { state.scrollToItem(currentIdx) }

    Column(
        modifier = modifier.width(260.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "节目单", style = MaterialTheme.typography.titleMedium)

        TvLazyColumn(
            state = state,
            modifier = Modifier.focusProperties { exit = { exitFocusRequester } },
            contentPadding = PaddingValues(top = 8.dp, bottom = childPadding.bottom),
        ) {
            items(epg.programmes) { programme ->
                var isFocused by remember { mutableStateOf(false) }

                ListItem(
                    modifier = Modifier.onFocusChanged { isFocused = it.isFocused || it.hasFocus },
                    selected = programme.isLive(),
                    onClick = { },
                    headlineContent = {
                        Text(
                            text = programme.title,
                            maxLines = if (isFocused) Int.MAX_VALUE else 2,
                        )
                    },
                    overlineContent = {
                        val start = timeFormat.format(programme.startAt)
                        val end = timeFormat.format(programme.endAt)
                        Text(text = "$start  ~ $end")
                    },
                    trailingContent = {
                        if (programme.isLive()) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "playing")
                        }
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun ClassicPanelEpgPreview() {
    MyTVTheme {
        ClassicPanelEpgList(
            epg = Epg(
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
        )
    }
}