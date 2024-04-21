package top.yogiczy.mytv.ui.screens.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.remember
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
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.panel.components.PanelChannelNo
import top.yogiczy.mytv.ui.screens.panel.components.PanelDateTime
import top.yogiczy.mytv.ui.screens.panel.components.PanelIptvGroupList
import top.yogiczy.mytv.ui.screens.panel.components.PanelIptvInfo
import top.yogiczy.mytv.ui.screens.panel.components.PanelPlayerInfo
import top.yogiczy.mytv.ui.screens.video.ExoPlayerState
import top.yogiczy.mytv.ui.screens.video.rememberExoPlayerState
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.handleAnyActiveAction


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelScreen(
    modifier: Modifier = Modifier,
    currentIptv: Iptv = Iptv.EMPTY,
    playerState: ExoPlayerState = rememberExoPlayerState(),
    iptvGroupList: IptvGroupList = IptvGroupList(),
    epgList: EpgList = EpgList(),
    onClose: () -> Unit = {},
    onIptvSelected: (Iptv) -> Unit = {},
    onActiveAction: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .focusRequester(focusRequester)
            .handleAnyActiveAction { onActiveAction() }
            .pointerInput(Unit) { detectTapGestures(onTap = { onClose() }) },
    ) {
        PanelTopRight(
            channelNo = (iptvGroupList.iptvIdx(currentIptv) + 1).toString().padStart(2, '0'),
        )

        PanelBottom(
            currentIptv = currentIptv,
            playerState = playerState,
            iptvGroupList = iptvGroupList,
            epgList = epgList,
            onIptvSelected = onIptvSelected,
            onActiveAction = onActiveAction
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
        PanelTopRight(
            channelNo = "01",
        )
    }
}

@Composable
fun PanelBottom(
    modifier: Modifier = Modifier,
    currentIptv: Iptv = Iptv.EMPTY,
    playerState: ExoPlayerState = rememberExoPlayerState(),
    iptvGroupList: IptvGroupList = IptvGroupList(),
    epgList: EpgList = EpgList(),
    onIptvSelected: (Iptv) -> Unit = {},
    onActiveAction: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            PanelIptvInfo(
                iptv = currentIptv,
                currentProgrammes = epgList.currentProgrammes(currentIptv),
                playerError = playerState.error,
                modifier = Modifier.padding(start = childPadding.start),
            )

            Spacer(modifier = Modifier.height(20.dp))

            PanelPlayerInfo(
                resolution = playerState.resolution,
                modifier = Modifier.padding(start = childPadding.start),
            )

            Spacer(modifier = Modifier.height(20.dp))

            PanelIptvGroupList(
                iptvGroupList = iptvGroupList,
                currentIptv = currentIptv,
                onIptvSelected = onIptvSelected,
                epgList = epgList,
                modifier = Modifier.height(150.dp),
                onListStateChanged = onActiveAction,
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun PanelBottomPreview() {
    MyTVTheme {
        PanelBottom(
            currentIptv = Iptv.EXAMPLE,
            playerState = ExoPlayerState(),
            iptvGroupList = IptvGroupList.EXAMPLE,
        )
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun PanelScreenPreview() {
    MyTVTheme {
        PanelScreen(
            currentIptv = Iptv.EXAMPLE,
            playerState = ExoPlayerState(),
            iptvGroupList = IptvGroupList.EXAMPLE,
        )
    }
}
