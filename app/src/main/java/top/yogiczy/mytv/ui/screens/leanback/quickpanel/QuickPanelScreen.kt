package top.yogiczy.mytv.ui.screens.leanback.quickpanel

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ButtonDefaults
import top.yogiczy.mytv.data.entities.EpgProgrammeCurrent
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.rememberLeanbackChildPadding
import top.yogiczy.mytv.ui.screens.leanback.panel.LeanbackPanelScreenTopRight
import top.yogiczy.mytv.ui.screens.leanback.panel.PanelAutoCloseState
import top.yogiczy.mytv.ui.screens.leanback.panel.components.LeanbackPanelIptvInfo
import top.yogiczy.mytv.ui.screens.leanback.panel.components.LeanbackPanelPlayerInfo
import top.yogiczy.mytv.ui.screens.leanback.panel.rememberPanelAutoCloseState
import top.yogiczy.mytv.ui.screens.leanback.quickpanel.components.LeanbackQuickPanelIptvChannelsDialog
import top.yogiczy.mytv.ui.screens.leanback.video.player.LeanbackVideoPlayer
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.handleLeanbackKeyEvents
import top.yogiczy.mytv.ui.utils.handleLeanbackUserAction

@Composable
fun LeanbackQuickPanelScreen(
    modifier: Modifier = Modifier,
    currentIptvProvider: () -> Iptv = { Iptv() },
    currentIptvUrlIdxProvider: () -> Int = { 0 },
    currentProgrammesProvider: () -> EpgProgrammeCurrent? = { null },
    currentIptvChannelNoProvider: () -> String = { "" },
    videoPlayerMetadataProvider: () -> LeanbackVideoPlayer.Metadata = { LeanbackVideoPlayer.Metadata() },
    videoPlayerAspectRatioProvider: () -> Float = { 16f / 9f },
    onChangeVideoPlayerAspectRatio: (Float) -> Unit = {},
    onIptvUrlIdxChange: (Int) -> Unit = {},
    onClearCache: () -> Unit = {},
    onMoreSettings: () -> Unit = {},
    onClose: () -> Unit = {},
    autoCloseState: PanelAutoCloseState = rememberPanelAutoCloseState(
        timeout = Constants.UI_SCREEN_AUTO_CLOSE_DELAY,
        onTimeout = onClose,
    ),
) {
    val childPadding = rememberLeanbackChildPadding()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        autoCloseState.active()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
            .focusRequester(focusRequester)
            .handleLeanbackUserAction { autoCloseState.active() }
            .pointerInput(Unit) { detectTapGestures(onTap = { onClose() }) },
    ) {
        LeanbackPanelScreenTopRight(
            channelNoProvider = currentIptvChannelNoProvider
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(
                    start = childPadding.start,
                    bottom = childPadding.bottom,
                    top = 20.dp,
                ),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                LeanbackPanelIptvInfo(
                    iptvProvider = currentIptvProvider,
                    iptvUrlIdxProvider = currentIptvUrlIdxProvider,
                    currentProgrammesProvider = currentProgrammesProvider,
                )

                LeanbackPanelPlayerInfo(
                    metadataProvider = videoPlayerMetadataProvider,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    LeanbackQuickPanelActionMultipleChannels(
                        currentIptvProvider = currentIptvProvider,
                        currentIptvUrlIdxProvider = currentIptvUrlIdxProvider,
                        onIptvUrlIdxChange = onIptvUrlIdxChange,
                        onUserAction = { autoCloseState.active() },
                    )

                    LeanbackQuickPanelButton(
                        titleProvider = { "清除缓存" },
                        onSelect = onClearCache,
                    )

                    LeanbackQuickPanelActionVideoAspectRatio(
                        videoPlayerAspectRatioProvider = videoPlayerAspectRatioProvider,
                        onChangeVideoPlayerAspectRatio = onChangeVideoPlayerAspectRatio,
                    )

                    LeanbackQuickPanelButton(
                        titleProvider = { "更多设置" },
                        onSelect = onMoreSettings,
                    )
                }
            }
        }
    }
}

@Composable
private fun LeanbackQuickPanelButton(
    modifier: Modifier = Modifier,
    titleProvider: () -> String,
    onSelect: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    androidx.tv.material3.Button(
        onClick = { },
        shape = ButtonDefaults.shape(
            shape = MaterialTheme.shapes.small,
        ),
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused || it.hasFocus
            }
            .handleLeanbackKeyEvents(
                onSelect = {
                    if (isFocused) onSelect()
                    else focusRequester.requestFocus()
                },
            ),
    ) {
        androidx.tv.material3.Text(text = titleProvider())
    }
}

@Composable
private fun LeanbackQuickPanelActionMultipleChannels(
    currentIptvProvider: () -> Iptv = { Iptv() },
    currentIptvUrlIdxProvider: () -> Int = { 0 },
    onIptvUrlIdxChange: (Int) -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    if (currentIptvProvider().urlList.size > 1) {
        var showChannelsDialog by remember { mutableStateOf(false) }
        LeanbackQuickPanelButton(
            titleProvider = { "多线路" },
            onSelect = { showChannelsDialog = true },
        )
        LeanbackQuickPanelIptvChannelsDialog(
            showDialogProvider = { showChannelsDialog },
            onDismissRequest = { showChannelsDialog = false },
            iptvProvider = currentIptvProvider,
            iptvUrlIdxProvider = currentIptvUrlIdxProvider,
            onIptvUrlIdxChange = onIptvUrlIdxChange,
            onUserAction = onUserAction,
        )
    }
}

@Composable
private fun LeanbackQuickPanelActionVideoAspectRatio(
    videoPlayerAspectRatioProvider: () -> Float = { 16f / 9f },
    onChangeVideoPlayerAspectRatio: (Float) -> Unit = {},
) {
    val configuration = LocalConfiguration.current
    val screenAspectRatio =
        configuration.screenWidthDp.toFloat() / configuration.screenHeightDp.toFloat()
    LeanbackQuickPanelButton(
        titleProvider = {
            "画面比例 " + when (videoPlayerAspectRatioProvider()) {
                16f / 9f -> "16:9"
                4f / 3f -> "4:3"
                screenAspectRatio -> "自动拉伸"
                else -> "原始"
            }
        },
        onSelect = {
            onChangeVideoPlayerAspectRatio(
                when (videoPlayerAspectRatioProvider()) {
                    16f / 9f -> 4f / 3f
                    4f / 3f -> screenAspectRatio
                    screenAspectRatio -> 16f / 9f
                    else -> 16f / 9f
                }
            )
        },
    )
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun LeanbackQuickPanelScreenPreview() {
    LeanbackTheme {
        LeanbackQuickPanelScreen(currentIptvProvider = { Iptv.EXAMPLE },
            currentProgrammesProvider = { EpgProgrammeCurrent.EXAMPLE },
            videoPlayerMetadataProvider = {
                LeanbackVideoPlayer.Metadata(
                    videoWidth = 1920,
                    videoHeight = 1080,
                )
            })
    }
}