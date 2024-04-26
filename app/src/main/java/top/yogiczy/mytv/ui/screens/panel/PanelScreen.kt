package top.yogiczy.mytv.ui.screens.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.EpgList.Companion.currentProgrammes
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.data.entities.IptvGroupList.Companion.iptvIdx
import top.yogiczy.mytv.data.entities.IptvList
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.panel.components.PanelChannelNo
import top.yogiczy.mytv.ui.screens.panel.components.PanelDateTime
import top.yogiczy.mytv.ui.screens.panel.components.PanelIptvFavoriteList
import top.yogiczy.mytv.ui.screens.panel.components.PanelIptvGroupList
import top.yogiczy.mytv.ui.screens.panel.components.PanelIptvInfo
import top.yogiczy.mytv.ui.screens.panel.components.PanelPlayerInfo
import top.yogiczy.mytv.ui.screens.settings.SettingsState
import top.yogiczy.mytv.ui.screens.settings.rememberSettingsState
import top.yogiczy.mytv.ui.screens.toast.ToastState
import top.yogiczy.mytv.ui.screens.video.PlayerState
import top.yogiczy.mytv.ui.screens.video.rememberPlayerState
import top.yogiczy.mytv.ui.theme.MyTVTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelScreen(
    modifier: Modifier = Modifier,
    currentIptv: Iptv = Iptv.EMPTY,
    currentIptvUrlIdx: Int = 0,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    epgList: EpgList = EpgList(),
    onIptvSelected: (Iptv) -> Unit = {},
    showFavoriteList: Boolean = false,
    onChangeShowFavoriteList: (Boolean) -> Unit = {},
    settingsState: SettingsState = rememberSettingsState(),
    playerState: PlayerState = rememberPlayerState(),
    onClose: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
            .focusRequester(focusRequester)
            .pointerInput(Unit) { detectTapGestures(onTap = { onClose() }) },
    ) {
        PanelTopRight(
            channelNo = (iptvGroupList.iptvIdx(currentIptv) + 1).toString().padStart(2, '0'),
        )

        PanelBottom(
            currentIptv = currentIptv,
            currentIptvUrlIdx = currentIptvUrlIdx,
            iptvGroupList = iptvGroupList,
            epgList = epgList,
            iptvChannelFavoriteList = settingsState.iptvChannelFavoriteList,
            onIptvSelected = onIptvSelected,
            showFavoriteList = showFavoriteList,
            onChangeShowFavoriteList = onChangeShowFavoriteList,
            showProgrammeProgress = settingsState.uiShowEpgProgrammeProgress,
            playerState = playerState,
            onIptvFavoriteToggle = {
                if (settingsState.iptvChannelFavoriteList.contains(it.channelName)) {
                    settingsState.iptvChannelFavoriteList -= it.channelName
                    ToastState.I.showToast("取消收藏: ${it.channelName}")
                } else {
                    settingsState.iptvChannelFavoriteList += it.channelName
                    ToastState.I.showToast("已收藏: ${it.channelName}")
                }
            }
        )
    }
}

@Composable
fun PanelTopRight(
    modifier: Modifier = Modifier,
    channelNo: String = "",
) {
    val childPadding = rememberChildPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = childPadding.top, end = childPadding.end),
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopEnd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PanelChannelNo(channelNo = channelNo)

            Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                Spacer(
                    modifier = Modifier
                        .background(Color.White)
                        .width(2.dp)
                        .height(30.dp),
                )
            }

            PanelDateTime()
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun PanelTopRightPreview() {
    MyTVTheme {
        PanelTopRight(channelNo = "01")
    }
}

@Composable
fun PanelBottom(
    modifier: Modifier = Modifier,
    currentIptv: Iptv = Iptv.EMPTY,
    currentIptvUrlIdx: Int = 0,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    epgList: EpgList = EpgList(),
    iptvChannelFavoriteList: Set<String> = emptySet(),
    onIptvSelected: (Iptv) -> Unit = {},
    showFavoriteList: Boolean = false,
    onChangeShowFavoriteList: (Boolean) -> Unit = {},
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    showProgrammeProgress: Boolean = false,
    playerState: PlayerState = rememberPlayerState(),
) {
    val childPadding = rememberChildPadding()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.BottomStart),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            PanelIptvInfo(
                iptv = currentIptv,
                iptvUrlIdx = currentIptvUrlIdx,
                currentProgrammes = epgList.currentProgrammes(currentIptv),
                playerError = playerState.error,
                modifier = Modifier
                    .padding(start = childPadding.start)
                    .pointerInput(showFavoriteList) {
                        detectTapGestures(onLongPress = {
                            onChangeShowFavoriteList(!showFavoriteList)
                        })
                    },
            )

            PanelPlayerInfo(
                modifier = Modifier.padding(start = childPadding.start),
                playerState = playerState,
            )

            Box(modifier = Modifier.height(150.dp)) {
                var key by remember { mutableIntStateOf(0) }
                val favoriteList by remember(key) {
                    mutableStateOf(iptvGroupList.flatMap { it.iptvs }
                        .filter { iptvChannelFavoriteList.contains(it.channelName) })
                }

                LaunchedEffect(favoriteList) {
                    if (favoriteList.isEmpty()) {
                        onChangeShowFavoriteList(false)
                    }
                }

                if (showFavoriteList) {
                    PanelIptvFavoriteList(
                        currentIptv = currentIptv,
                        iptvList = IptvList(favoriteList),
                        epgList = epgList,
                        onIptvSelected = onIptvSelected,
                        onIptvFavoriteToggle = {
                            onIptvFavoriteToggle(it)
                            key++
                        },
                        showProgrammeProgress = showProgrammeProgress,
                        onClose = { onChangeShowFavoriteList(false) },
                    )
                } else {
                    PanelIptvGroupList(
                        currentIptv = currentIptv,
                        iptvGroupList = iptvGroupList,
                        epgList = epgList,
                        onIptvSelected = onIptvSelected,
                        onIptvFavoriteToggle = {
                            onIptvFavoriteToggle(it)
                            key++
                        },
                        showProgrammeProgress = showProgrammeProgress,
                        onChangeToFavoriteList = {
                            if (favoriteList.isNotEmpty()) {
                                onChangeShowFavoriteList(true)
                            } else {
                                ToastState.I.showToast("没有收藏的频道")
                            }
                        },
                    )
                }
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun PanelBottomPreview() {
    MyTVTheme {
        PanelBottom(
            currentIptv = Iptv.EXAMPLE,
            iptvGroupList = IptvGroupList.EXAMPLE,
            playerState = PlayerState(),
        )
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun PanelScreenPreview() {
    MyTVTheme {
        PanelScreen(
            currentIptv = Iptv.EXAMPLE,
            iptvGroupList = IptvGroupList.EXAMPLE,
            settingsState = SettingsState(),
            playerState = PlayerState(),
        )
    }
}
