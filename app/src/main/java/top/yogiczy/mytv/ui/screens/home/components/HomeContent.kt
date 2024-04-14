package top.yogiczy.mytv.ui.screens.home.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.dash.DefaultDashChunkSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.smoothstreaming.DefaultSsChunkSource
import androidx.media3.exoplayer.smoothstreaming.SsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.data.entities.IptvGroupList.Companion.iptvIdx
import top.yogiczy.mytv.ui.screens.monitor.MonitorScreen
import top.yogiczy.mytv.ui.screens.panel.PanelScreen
import top.yogiczy.mytv.ui.screens.settings.SettingsScreen
import top.yogiczy.mytv.ui.screens.video.VideoScreen
import top.yogiczy.mytv.ui.screens.video.rememberExoPlayerState
import top.yogiczy.mytv.ui.utils.SP
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents
import top.yogiczy.mytv.ui.utils.handleVerticalDragGestures

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    onBackPressed: () -> Unit = {},
    state: HomeContentState = rememberHomeContentState(
        iptvGroupList = iptvGroupList,
    ),
) {
    val focusRequester = remember { FocusRequester() }

    val playerState = rememberExoPlayerState(state.exoPlayer)
    val channelNoInputState = rememberChannelNoInputState { channelNo ->
        if (channelNo.toInt() - 1 in 0..<iptvGroupList.flatMap { it.iptvs }.size) {
            state.changeCurrentIptv(iptvGroupList.flatMap { it.iptvs }[channelNo.toInt() - 1])
        }
    }

    LaunchedEffect(state.isPanelVisible, state.isSettingsVisible) {
        if (!state.isPanelVisible && !state.isSettingsVisible) {
            focusRequester.requestFocus()
        }
    }

    BackPressHandledArea(
        modifier = modifier,
        onBackPressed = {
            if (state.isPanelVisible)
                state.changePanelVisible(false)
            else if (state.isSettingsVisible)
                state.changeSettingsVisible(false)
            else onBackPressed()
        },
    ) {
        Box(
            modifier = Modifier
                .handleDPadKeyEvents(
                    onUp = {
                        if (SP.iptvChannelChangeFlip) state.changeCurrentIptvToNext()
                        else state.changeCurrentIptvToPrev()
                    },
                    onDown = {
                        if (SP.iptvChannelChangeFlip) state.changeCurrentIptvToPrev()
                        else state.changeCurrentIptvToNext()
                    },
                    onEnter = { state.changePanelVisible(true) },
                    onLongEnter = { state.changeSettingsVisible(true) },
                    onSettings = { state.changeSettingsVisible(true) },
                    onNumber = { channelNoInputState.input(it) }
                )
                .handleVerticalDragGestures(
                    onSwipeUp = {
                        state.changeCurrentIptvToNext()
                    },
                    onSwipeDown = {
                        state.changeCurrentIptvToPrev()
                    },
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { state.changePanelVisible(true) },
                        onDoubleTap = { state.changeSettingsVisible(true) },
                    )
                }
                .focusRequester(focusRequester)
                .focusable(),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                VideoScreen(
                    exoPlayer = state.exoPlayer,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        if (state.isTempPanelVisible) {
            IptvTempPanel(
                channelNo = iptvGroupList.iptvIdx(state.currentIptv) + 1,
                currentIptv = state.currentIptv,
                playerError = playerState.error,
            )
        }

        ChannelNoInput(state = channelNoInputState)

        if (state.isPanelVisible) {
            PanelScreen(
                currentIptv = state.currentIptv,
                playerError = playerState.error,
                playerResolution = playerState.resolution,
                iptvGroupList = iptvGroupList,
                onClose = { state.changePanelVisible(false) },
                onIptvSelected = {
                    state.changeCurrentIptv(it)
                },
            )
        }

        if (state.isSettingsVisible) {
            SettingsScreen(onClose = { state.changeSettingsVisible(false) })
        }

        if (SP.debugShowFps) {
            MonitorScreen()
        }
    }
}

@Composable
private fun BackPressHandledArea(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) = Box(modifier = Modifier
    .onPreviewKeyEvent {
        if (it.key == Key.Back && it.type == KeyEventType.KeyUp) {
            onBackPressed()
            true
        } else {
            false
        }
    }
    .then(modifier), content = content)

@UnstableApi
class HomeContentState(
    context: Context,
    coroutineScope: CoroutineScope,
    private val iptvGroupList: IptvGroupList,
) {
    private var _currentIptv by mutableStateOf(Iptv.EMPTY)
    val currentIptv get() = _currentIptv

    private var _isPanelVisible by mutableStateOf(false)
    val isPanelVisible get() = _isPanelVisible

    private var _isSettingsVisible by mutableStateOf(false)
    val isSettingsVisible get() = _isSettingsVisible

    private var _isTempPanelVisible by mutableStateOf(false)
    val isTempPanelVisible get() = _isTempPanelVisible

    private var _exoPlayer = ExoPlayer.Builder(context).build().apply {
        playWhenReady = true
    }
    val exoPlayer get() = _exoPlayer

    private val dataSourceFactory: DataSource.Factory =
        DefaultDataSource.Factory(context, DefaultHttpDataSource.Factory().apply {
            setUserAgent("ExoPlayer")
            setConnectTimeoutMs(5_000)
            setKeepPostFor302Redirects(true)
            setAllowCrossProtocolRedirects(true)
        })

    init {
        if (SP.iptvLastIptvIdx in 0..<iptvGroupList.flatMap { it.iptvs }.size) {
            changeCurrentIptv(iptvGroupList.flatMap { it.iptvs }[SP.iptvLastIptvIdx])
        } else {
            changeCurrentIptv(iptvGroupList.firstOrNull()?.iptvs?.firstOrNull() ?: Iptv.EMPTY)
        }

        _exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    coroutineScope.launch {
                        delay(1000)
                        changeTempPanelVisible(false)
                    }
                }
            }
        })
    }

    fun changePanelVisible(visible: Boolean) {
        _isPanelVisible = visible
    }

    fun changeSettingsVisible(visible: Boolean) {
        _isSettingsVisible = visible
    }

    fun changeTempPanelVisible(visible: Boolean) {
        _isTempPanelVisible = visible
    }

    private fun getPrevIptv(): Iptv {
        val currentIndex = iptvGroupList.iptvIdx(_currentIptv)
        return if (currentIndex > 0) {
            iptvGroupList.flatMap { it.iptvs }[currentIndex - 1]
        } else {
            iptvGroupList.lastOrNull()?.iptvs?.lastOrNull() ?: Iptv.EMPTY
        }
    }

    private fun getNextIptv(): Iptv {
        val currentIndex = iptvGroupList.iptvIdx(_currentIptv)
        return if (currentIndex < iptvGroupList.flatMap { it.iptvs }.size - 1) {
            iptvGroupList.flatMap { it.iptvs }[currentIndex + 1]
        } else {
            iptvGroupList.firstOrNull()?.iptvs?.firstOrNull() ?: Iptv.EMPTY
        }
    }

    fun changeCurrentIptv(iptv: Iptv) {
        _isPanelVisible = false

        if (iptv.name == _currentIptv.name) return

        _currentIptv = iptv
        SP.iptvLastIptvIdx = iptvGroupList.iptvIdx(iptv)
        _isTempPanelVisible = true

        if (iptv.urlList.isNotEmpty()) {
            val uri = Uri.parse(iptv.urlList.first())
            val contentType =
                if (uri.path?.endsWith(".php") == true) C.CONTENT_TYPE_HLS else null

            exoPlayer.setMediaSource(
                getExoPlayerMediaSource(uri, dataSourceFactory, contentType)
            )
            exoPlayer.prepare()
        }
    }

    fun changeCurrentIptvToPrev() {
        changeCurrentIptv(getPrevIptv())
    }

    fun changeCurrentIptvToNext() {
        changeCurrentIptv(getNextIptv())
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun rememberHomeContentState(
    context: Context = LocalContext.current,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) = remember {
    HomeContentState(
        context = context,
        coroutineScope = coroutineScope,
        iptvGroupList = iptvGroupList,
    )
}

@androidx.annotation.OptIn(UnstableApi::class)
fun getExoPlayerMediaSource(
    uri: Uri,
    dataSourceFactory: DataSource.Factory,
    contentType: Int?,
): MediaSource {
    val mediaItem = MediaItem.fromUri(uri)
    when (val type = contentType ?: Util.inferContentType(uri)) {
        C.CONTENT_TYPE_HLS -> {
            return HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        }

        C.CONTENT_TYPE_DASH -> {
            return DashMediaSource.Factory(
                DefaultDashChunkSource.Factory(dataSourceFactory), dataSourceFactory
            ).createMediaSource(mediaItem)
        }

        C.CONTENT_TYPE_SS -> {
            return SsMediaSource.Factory(
                DefaultSsChunkSource.Factory(dataSourceFactory), dataSourceFactory
            ).createMediaSource(mediaItem)
        }

        C.CONTENT_TYPE_OTHER -> {
            return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        }

        else -> {
            throw IllegalStateException("Unsupported type: $type")
        }
    }
}