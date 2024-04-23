package top.yogiczy.mytv.ui.screens.panel.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.entities.Epg
import top.yogiczy.mytv.data.entities.Epg.Companion.currentProgrammes
import top.yogiczy.mytv.data.entities.EpgProgramme
import top.yogiczy.mytv.data.entities.EpgProgramme.Companion.isLive
import top.yogiczy.mytv.data.entities.EpgProgrammeList
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.tvmaterial.StandardDialog
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelIptvItem(
    modifier: Modifier = Modifier,
    iptv: Iptv = Iptv.EMPTY,
    onIptvSelected: () -> Unit = {},
    epg: Epg? = null,
    initialFocused: Boolean = false,
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var hasFocused by rememberSaveable { mutableStateOf(false) }

    var showEpgDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (initialFocused && !hasFocused) {
            focusRequester.requestFocus()
            hasFocused = true
        }
    }

    Card(
        modifier = modifier
            .width(130.dp)
            .height(54.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .handleDPadKeyEvents(
                onSelect = {
                    focusRequester.requestFocus()
                    onIptvSelected()
                },
                onLongSelect = {
                    focusRequester.requestFocus()
                    if (epg != null) {
                        showEpgDialog = true
                    }
                },
            ),
        scale = CardDefaults.scale(focusedScale = 1.1f),
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
            contentColor = MaterialTheme.colorScheme.onBackground,
            focusedContainerColor = MaterialTheme.colorScheme.onBackground,
            focusedContentColor = MaterialTheme.colorScheme.background,
        ),
        onClick = { },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Start)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = iptv.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
            )

            Text(
                text = epg?.currentProgrammes()?.now?.title ?: "",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                modifier = Modifier.alpha(0.8f),
            )
        }
    }

    PanelIptvItemEpgDialog(
        showDialog = showEpgDialog,
        onDismissRequest = { showEpgDialog = false },
        epg = epg ?: Epg.EMPTY,
    )
}

@Preview
@Composable
private fun PanelIptvItemPreview() {
    MyTVTheme {
        Column(
            modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PanelIptvItem(
                iptv = Iptv.EXAMPLE,
            )

            PanelIptvItem(
                iptv = Iptv.EXAMPLE,
                initialFocused = true,
            )
        }
    }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
private fun PanelIptvItemEpgDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean = false,
    onDismissRequest: () -> Unit = {},
    epg: Epg = Epg.EMPTY,
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    StandardDialog(
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        title = { Text(text = "节目单") },
    ) {
        val listState =
            rememberTvLazyListState(initialFirstVisibleItemIndex = epg.programmes.indexOfFirst { it.isLive() })

        TvLazyColumn(modifier = modifier, state = listState) {
            items(epg.programmes) { programme ->
                ListItem(
                    modifier = modifier.padding(vertical = 1.dp),
                    selected = false,
                    onClick = { },
                    headlineContent = {
                        Text(
                            text = programme.title,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                        )
                    },
                    overlineContent = {
                        Text(
                            text = timeFormat.format(programme.startAt) + " ~ " + timeFormat.format(
                                programme.endAt
                            )
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
    }
}

@Preview
@Composable
private fun PanelIptvItemEpgDialogPreview() {
    MyTVTheme {
        PanelIptvItemEpgDialog(
            showDialog = true, epg = Epg(
                "CCTV1", EpgProgrammeList(
                    listOf(
                        EpgProgramme(
                            startAt = 1713850800000, endAt = 1713854400000, title = "新闻联播1"
                        ),
                        EpgProgramme(
                            startAt = 1713861600000, endAt = 1713865200000, title = "新闻联播"
                        ),
                    )
                )
            )
        )
    }
}