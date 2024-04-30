package top.yogiczy.mytv.ui.screens.classicpanel

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvGroup
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.data.entities.IptvGroupList.Companion.iptvGroupIdx
import top.yogiczy.mytv.data.entities.IptvList
import top.yogiczy.mytv.tvmaterial.end
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.classicpanel.components.ClassicPanelEpgList
import top.yogiczy.mytv.ui.screens.classicpanel.components.ClassicPanelIptvGroupList
import top.yogiczy.mytv.ui.screens.classicpanel.components.ClassicPanelIptvList
import top.yogiczy.mytv.ui.screens.panel.PanelAutoCloseState
import top.yogiczy.mytv.ui.screens.panel.rememberPanelAutoCloseState
import top.yogiczy.mytv.ui.screens.settings.SettingsState
import top.yogiczy.mytv.ui.screens.settings.rememberSettingsState
import top.yogiczy.mytv.ui.screens.toast.ToastState
import top.yogiczy.mytv.ui.screens.video.PlayerState
import top.yogiczy.mytv.ui.screens.video.rememberPlayerState
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleUserAction
import kotlin.math.max

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ClassicPanelScreen(
    modifier: Modifier = Modifier,
    currentIptv: Iptv = Iptv.EMPTY,
    currentIptvUrlIdx: Int = 0,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    epgList: EpgList = EpgList(),
    onIptvSelected: (Iptv) -> Unit = {},
    settingsState: SettingsState = rememberSettingsState(),
    playerState: PlayerState = rememberPlayerState(),
    panelAutoCloseState: PanelAutoCloseState = rememberPanelAutoCloseState(),
    onClose: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    var favoriteKey by remember { mutableIntStateOf(0) }
    val iptvGroupListWithFavorite = remember(favoriteKey) {
        val favoriteList = IptvList(iptvGroupList.flatMap { it.iptvs }
            .filter { settingsState.iptvChannelFavoriteList.contains(it.channelName) })

        if (favoriteList.isNotEmpty()) IptvGroupList(
            listOf(IptvGroup(name = "收藏", iptvs = favoriteList)) + iptvGroupList
        )
        else iptvGroupList
    }

    var focusedIptvGroup by remember {
        mutableStateOf(
            iptvGroupListWithFavorite[max(0, iptvGroupListWithFavorite.iptvGroupIdx(currentIptv))]
        )
    }
    val focusedIptvGroupIdx = max(0, iptvGroupListWithFavorite.indexOf(focusedIptvGroup))

    var focusedIptv by remember { mutableStateOf(currentIptv) }
    var focusedIptvFocusRequester by remember { mutableStateOf(FocusRequester.Default) }
    val focusedIptvIdx = max(0, focusedIptvGroup.iptvs.indexOf(focusedIptv))

    val iptvListState = rememberTvLazyListState(focusedIptvIdx)

    var isFirst by remember { mutableStateOf(true) }
    LaunchedEffect(focusedIptvGroupIdx) {
        if (isFirst) {
            isFirst = false
            return@LaunchedEffect
        }
        focusedIptv = focusedIptvGroup.iptvs[0]
        iptvListState.scrollToItem(0)
    }

    var inIptvGroupTab by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .handleUserAction { panelAutoCloseState.active() }
            .pointerInput(Unit) { detectTapGestures(onTap = { onClose() }) },
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxHeight()
                .background(
                    MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                    shape = MaterialTheme.shapes.small.end(),
                )
                .padding(
                    top = childPadding.top,
                    start = childPadding.start,
                    end = childPadding.end,
                ),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ClassicPanelIptvGroupList(
                    iptvGroupList = iptvGroupListWithFavorite,
                    currentIptvGroup = focusedIptvGroup,
                    onChangeFocused = { focusedIptvGroup = it },
                    exitFocusRequester = focusedIptvFocusRequester,
                    onEnter = { inIptvGroupTab = true },
                    onExit = { inIptvGroupTab = false },
                    panelAutoCloseState = panelAutoCloseState,
                )

                ClassicPanelIptvList(
                    iptvList = iptvGroupListWithFavorite[focusedIptvGroupIdx].iptvs,
                    currentIptv = focusedIptv,
                    onChangeFocused = { iptv, focusRequester ->
                        focusedIptv = iptv
                        focusedIptvFocusRequester = focusRequester
                    },
                    onIptvSelected = onIptvSelected,
                    epgList = epgList,
                    state = iptvListState,
                    onIptvFavoriteToggle = {
                        if (settingsState.iptvChannelFavoriteList.contains(it.channelName)) {
                            settingsState.iptvChannelFavoriteList -= it.channelName
                            ToastState.I.showToast("取消收藏: ${it.channelName}")
                        } else {
                            settingsState.iptvChannelFavoriteList += it.channelName
                            ToastState.I.showToast("已收藏: ${it.channelName}")
                        }
                        favoriteKey++
                    },
                    panelAutoCloseState = panelAutoCloseState,
                )

                val epg = epgList.firstOrNull { it.channel == focusedIptv.channelName }
                if (!inIptvGroupTab && epg != null) {
                    ClassicPanelEpgList(
                        epg = epg,
                        exitFocusRequester = focusedIptvFocusRequester,
                        panelAutoCloseState = panelAutoCloseState,
                    )
                }
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun ClassicPanelScreenPreview() {
    MyTVTheme {
        ClassicPanelScreen(
            currentIptv = Iptv.EXAMPLE,
            iptvGroupList = IptvGroupList.EXAMPLE,
            settingsState = SettingsState(),
            playerState = PlayerState(),
        )
    }
}