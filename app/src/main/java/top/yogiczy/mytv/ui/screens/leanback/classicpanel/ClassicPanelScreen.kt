package top.yogiczy.mytv.ui.screens.leanback.classicpanel

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import top.yogiczy.mytv.data.entities.Epg
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.EpgProgramme
import top.yogiczy.mytv.data.entities.EpgProgrammeList
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvGroup
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.data.entities.IptvGroupList.Companion.iptvGroupIdx
import top.yogiczy.mytv.data.entities.IptvGroupList.Companion.iptvList
import top.yogiczy.mytv.data.entities.IptvList
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.rememberLeanbackChildPadding
import top.yogiczy.mytv.ui.screens.leanback.classicpanel.components.LeanbackClassicPanelEpgList
import top.yogiczy.mytv.ui.screens.leanback.classicpanel.components.LeanbackClassicPanelIptvGroupList
import top.yogiczy.mytv.ui.screens.leanback.classicpanel.components.LeanbackClassicPanelIptvList
import top.yogiczy.mytv.ui.screens.leanback.components.LeanbackVisible
import top.yogiczy.mytv.ui.screens.leanback.panel.PanelAutoCloseState
import top.yogiczy.mytv.ui.screens.leanback.panel.rememberPanelAutoCloseState
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.handleLeanbackKeyEvents
import kotlin.math.max

@Composable
fun LeanbackClassicPanelScreen(
    modifier: Modifier = Modifier,
    iptvGroupListProvider: () -> IptvGroupList = { IptvGroupList() },
    epgListProvider: () -> EpgList = { EpgList() },
    currentIptvProvider: () -> Iptv = { Iptv() },
    showProgrammeProgressProvider: () -> Boolean = { false },
    iptvFavoriteEnableProvider: () -> Boolean = { true },
    iptvFavoriteListProvider: () -> ImmutableList<String> = { persistentListOf() },
    iptvFavoriteListVisibleProvider: () -> Boolean = { false },
    onIptvFavoriteListVisibleChange: (Boolean) -> Unit = {},
    onIptvSelected: (Iptv) -> Unit = {},
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    onClose: () -> Unit = {},
    autoCloseState: PanelAutoCloseState = rememberPanelAutoCloseState(
        timeout = Constants.UI_SCREEN_AUTO_CLOSE_DELAY,
        onTimeout = onClose,
    ),
) {
    LaunchedEffect(Unit) {
        autoCloseState.active()
    }

    LeanbackClassicPanelScreenWrapper(
        modifier = modifier,
        onClose = onClose,
    ) {
        LeanbackClassicPanelScreenContent(
            iptvGroupListProvider = iptvGroupListProvider,
            epgListProvider = epgListProvider,
            currentIptvProvider = currentIptvProvider,
            showProgrammeProgressProvider = showProgrammeProgressProvider,
            onIptvSelected = onIptvSelected,
            iptvFavoriteEnableProvider = iptvFavoriteEnableProvider,
            iptvFavoriteListProvider = iptvFavoriteListProvider,
            iptvFavoriteListVisibleProvider = iptvFavoriteListVisibleProvider,
            onIptvFavoriteListVisibleChange = onIptvFavoriteListVisibleChange,
            onIptvFavoriteToggle = onIptvFavoriteToggle,
            onUserAction = { autoCloseState.active() },
        )
    }
}

@Composable
private fun LeanbackClassicPanelScreenWrapper(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val childPadding = rememberLeanbackChildPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { onClose() }) }
    ) {
        Box(
            modifier = Modifier
                .pointerInput(Unit) { detectTapGestures(onTap = { }) }
                .padding(
                    top = childPadding.top,
                    start = childPadding.start,
                    bottom = childPadding.bottom,
                    end = childPadding.end,
                )
                .clip(MaterialTheme.shapes.small),
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LeanbackClassicPanelScreenContent(
    modifier: Modifier = Modifier,
    iptvGroupListProvider: () -> IptvGroupList = { IptvGroupList() },
    epgListProvider: () -> EpgList = { EpgList() },
    currentIptvProvider: () -> Iptv = { Iptv() },
    showProgrammeProgressProvider: () -> Boolean = { false },
    onIptvSelected: (Iptv) -> Unit = {},
    iptvFavoriteEnableProvider: () -> Boolean = { true },
    iptvFavoriteListProvider: () -> ImmutableList<String> = { persistentListOf() },
    iptvFavoriteListVisibleProvider: () -> Boolean = { false },
    onIptvFavoriteListVisibleChange: (Boolean) -> Unit = {},
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    val iptvGroupList = iptvGroupListProvider()

    var focusedIptvGroup by remember {
        mutableStateOf(
            if (iptvFavoriteListVisibleProvider())
                LeanbackClassicPanelScreenFavoriteIptvGroup
            else
                iptvGroupList[max(0, iptvGroupList.iptvGroupIdx(currentIptvProvider()))]
        )
    }

    var focusedIptv by remember { mutableStateOf(currentIptvProvider()) }
    var focusedIptvFocusRequester by remember { mutableStateOf(FocusRequester.Default) }

    var epgListVisible by remember { mutableStateOf(false) }

    Row(modifier = modifier) {
        LeanbackClassicPanelIptvGroupList(
            iptvGroupListProvider = {
                if (iptvFavoriteEnableProvider())
                    IptvGroupList(listOf(LeanbackClassicPanelScreenFavoriteIptvGroup) + iptvGroupList)
                else
                    iptvGroupList
            },
            initialIptvGroupProvider = {
                if (iptvFavoriteListVisibleProvider())
                    LeanbackClassicPanelScreenFavoriteIptvGroup
                else
                    iptvGroupList.find { it.iptvList.contains(currentIptvProvider()) }
                        ?: IptvGroup()
            },
            onIptvGroupFocused = {
                focusedIptvGroup = it
                onIptvFavoriteListVisibleChange(it == LeanbackClassicPanelScreenFavoriteIptvGroup)
            },
            exitFocusRequesterProvider = { focusedIptvFocusRequester },
            onUserAction = onUserAction,
        )

        LeanbackClassicPanelIptvList(
            modifier = Modifier
                .handleLeanbackKeyEvents(
                    onRight = { epgListVisible = true },
                    onLeft = { epgListVisible = false }
                )
                .focusProperties {
                    exit = {
                        if (epgListVisible && it == FocusDirection.Left) {
                            epgListVisible = false
                            FocusRequester.Cancel
                        } else {
                            FocusRequester.Default
                        }
                    }
                },
            iptvGroupProvider = { focusedIptvGroup },
            iptvListProvider = {
                if (focusedIptvGroup == LeanbackClassicPanelScreenFavoriteIptvGroup)
                    IptvList(iptvGroupListProvider().iptvList
                        .filter { iptvFavoriteListProvider().contains(it.channelName) })
                else
                    focusedIptvGroup.iptvList
            },
            epgListProvider = epgListProvider,
            initialIptvProvider = currentIptvProvider,
            onIptvSelected = onIptvSelected,
            onIptvFavoriteToggle = onIptvFavoriteToggle,
            onIptvFocused = { iptv, focusRequester ->
                focusedIptv = iptv
                focusedIptvFocusRequester = focusRequester
            },
            showProgrammeProgressProvider = showProgrammeProgressProvider,
            onUserAction = onUserAction,
            isFavoriteListProvider = { focusedIptvGroup == LeanbackClassicPanelScreenFavoriteIptvGroup },
        )

        LeanbackVisible({ epgListVisible }) {
            LeanbackClassicPanelEpgList(
                epgProvider = { epgListProvider().firstOrNull { it.channel == focusedIptv.channelName } },
                exitFocusRequesterProvider = { focusedIptvFocusRequester },
                onUserAction = onUserAction,
            )
        }
        LeanbackVisible({ !epgListVisible }) {
            LeanbackClassicPanelVerticalTip(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background.copy(0.7f))
                    .padding(horizontal = 4.dp),
                text = "向右查看节目单",
                onTap = { epgListVisible = true },
            )
        }
    }
}

@Composable
private fun LeanbackClassicPanelVerticalTip(
    modifier: Modifier = Modifier,
    text: String,
    onTap: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onTap() })
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        text.map {
            Text(text = it.toString(), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun LeanbackClassicPanelScreenPreview() {
    LeanbackTheme {
        LeanbackClassicPanelScreen(
            iptvGroupListProvider = { IptvGroupList.EXAMPLE },
            epgListProvider = {
                EpgList(IptvGroupList.EXAMPLE.iptvList.map {
                    Epg(
                        channel = it.channelName,
                        programmes = EpgProgrammeList(List(5) { idx ->
                            EpgProgramme(
                                startAt = System.currentTimeMillis() + idx * 60 * 60 * 1000L,
                                endAt = System.currentTimeMillis() + (idx + 1) * 60 * 60 * 1000L,
                                title = "${it.channelName}节目${idx + 1}",
                            )
                        })
                    )
                })
            },
        )
    }
}

val LeanbackClassicPanelScreenFavoriteIptvGroup = IptvGroup(name = "我的收藏")