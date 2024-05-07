package top.yogiczy.mytv.ui.screens.classicpanel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.EpgList.Companion.currentProgrammes
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvList
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents
import kotlin.math.max

@Composable
fun ClassicPanelIptvList(
    modifier: Modifier = Modifier,
    iptvList: IptvList = IptvList(),
    currentIptv: Iptv = Iptv.EMPTY,
    currentIptvIdx: Int = max(0, iptvList.indexOf(currentIptv)),
    onChangeFocused: (Iptv, FocusRequester) -> Unit = { _, _ -> },
    onIptvSelected: (Iptv) -> Unit = {},
    epgList: EpgList = EpgList(),
    state: TvLazyListState = rememberTvLazyListState(currentIptvIdx),
    focusRequesterList: List<FocusRequester> = remember(iptvList) {
        List(iptvList.size) { FocusRequester() }
    },
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    showProgrammeProgress: Boolean = false,
) {
    val childPadding = rememberChildPadding()

    var hasFocused by remember { mutableStateOf(false) }
    LaunchedEffect(iptvList) {
        onChangeFocused(iptvList.first(), focusRequesterList.first())
    }

    Column(
        modifier = modifier.width(220.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "频道列表", style = MaterialTheme.typography.titleMedium)

        TvLazyColumn(
            state = state,
            contentPadding = PaddingValues(top = 8.dp, bottom = childPadding.bottom),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            itemsIndexed(iptvList) { index, iptv ->
                var isFocused by remember { mutableStateOf(false) }
                val focusRequester = focusRequesterList[index]

                val currentProgramme = epgList.currentProgrammes(iptv)?.now

                LaunchedEffect(Unit) {
                    if (index == currentIptvIdx && !hasFocused) {
                        hasFocused = true
                        focusRequester.requestFocus()
                    }
                }

                Box(
                    modifier = Modifier.clip(ListItemDefaults.shape().shape),
                ) {
                    ListItem(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                isFocused = it.isFocused || it.hasFocus
                                if (it.isFocused || it.hasFocus) {
                                    onChangeFocused(iptv, focusRequester)
                                }
                            }
                            .handleDPadKeyEvents(
                                key = iptv.hashCode(),
                                onSelect = {
                                    if (isFocused) onIptvSelected(iptv)
                                    else focusRequester.requestFocus()
                                },
                                onLongSelect = {
                                    if (isFocused) onIptvFavoriteToggle(iptv)
                                    else focusRequester.requestFocus()
                                },
                            ),
                        selected = index == currentIptvIdx,
                        onClick = { },
                        headlineContent = { Text(text = iptv.name, maxLines = 2) },
                        supportingContent = {
                            Text(
                                text = currentProgramme?.title ?: "无节目",
                                maxLines = 1,
                            )
                        },
                    )

                    if (showProgrammeProgress && currentProgramme != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth(0.6f)
                                .height(3.dp)
                                .background(
                                    if (isFocused) MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ClassicPanelIptvListPreview() {
    MyTVTheme {
        ClassicPanelIptvList(
            modifier = Modifier.padding(10.dp),
            iptvList = IptvList.EXAMPLE,
        )
    }
}