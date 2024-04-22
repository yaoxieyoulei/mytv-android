package top.yogiczy.mytv.ui.screens.home.components

import android.content.Context
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.EpgList.Companion.currentProgrammes
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.data.entities.IptvGroupList.Companion.iptvIdx
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.screens.monitor.MonitorScreen
import top.yogiczy.mytv.ui.screens.panel.PanelScreen
import top.yogiczy.mytv.ui.screens.settings.SettingsScreen
import top.yogiczy.mytv.ui.screens.settings.components.rememberUpdateState
import top.yogiczy.mytv.ui.screens.video.VideoScreen
import top.yogiczy.mytv.ui.screens.video.rememberExoPlayerState
import top.yogiczy.mytv.ui.utils.Loggable
import top.yogiczy.mytv.ui.utils.SP
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents
import top.yogiczy.mytv.ui.utils.handleVerticalDragGestures
import kotlin.math.max

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    epgList: EpgList = EpgList(),
    onBackPressed: () -> Unit = {},
    state: HomeContentState = rememberHomeContentState(
        iptvGroupList = iptvGroupList,
    ),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusRequester = remember { FocusRequester() }

    val updateState = rememberUpdateState()
    val playerState = rememberExoPlayerState(state.exoPlayer)
    val channelNoInputState = rememberChannelNoInputState { channelNo ->
        if (channelNo.toInt() - 1 in 0..<iptvGroupList.flatMap { it.iptvs }.size) {
            state.changeCurrentIptv(iptvGroupList.flatMap { it.iptvs }[channelNo.toInt() - 1])
        }
    }

    LaunchedEffect(Unit) {
        delay(3000)
        updateState.checkUpdate()
    }

    LaunchedEffect(state.isPanelVisible, state.isSettingsVisible) {
        if (!state.isPanelVisible && !state.isSettingsVisible) {
            focusRequester.requestFocus()
        }
    }

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                state.exoPlayer.play()
            } else if (event == Lifecycle.Event.ON_STOP) {
                state.exoPlayer.pause()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BackPressHandledArea(
        modifier = modifier,
        onBackPressed = {
            if (state.isPanelVisible) state.changePanelVisible(false)
            else if (state.isSettingsVisible) state.changeSettingsVisible(false)
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
                    onLeft = {
                        if (state.currentIptv.urlList.size > 1) {
                            state.changeCurrentIptv(state.currentIptv, state.currentIptvUrlIdx - 1)
                        }
                    },
                    onRight = {
                        if (state.currentIptv.urlList.size > 1) {
                            state.changeCurrentIptv(state.currentIptv, state.currentIptvUrlIdx + 1)
                        }
                    },
                    onEnter = { state.changePanelVisible(true) },
                    onLongEnter = { state.changeSettingsVisible(true) },
                    onSettings = { state.changeSettingsVisible(true) },
                    onNumber = {
                        state.changeTempPanelVisible(false)
                        channelNoInputState.input(it)
                    },
                )
                .handleVerticalDragGestures(
                    onSwipeUp = { state.changeCurrentIptvToNext() },
                    onSwipeDown = { state.changeCurrentIptvToPrev() },
                    onSwipeLeft = {
                        if (state.currentIptv.urlList.size > 1) {
                            state.changeCurrentIptv(state.currentIptv, state.currentIptvUrlIdx + 1)
                        }
                    },
                    onSwipeRight = {
                        if (state.currentIptv.urlList.size > 1) {
                            state.changeCurrentIptv(state.currentIptv, state.currentIptvUrlIdx - 1)
                        }
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
                currentIptvUrlIdx = state.currentIptvUrlIdx,
                playerError = playerState.error,
                currentProgrammes = epgList.currentProgrammes(state.currentIptv),
            )
        }

        ChannelNoInput(state = channelNoInputState)

        AnimatedVisibility(state.isPanelVisible, enter = fadeIn(), exit = fadeOut()) {
            PanelScreen(
                currentIptv = state.currentIptv,
                currentIptvUrlIdx = state.currentIptvUrlIdx,
                playerState = playerState,
                iptvGroupList = iptvGroupList,
                epgList = epgList,
                onClose = { state.changePanelVisible(false) },
                onIptvSelected = {
                    state.changeCurrentIptv(it)
                },
                onActiveAction = { state.onActiveAction() },
            )
        }

        AnimatedVisibility(state.isSettingsVisible, enter = fadeIn(), exit = fadeOut()) {
            SettingsScreen(
                updateState = updateState,
                onClose = { state.changeSettingsVisible(false) },
            )
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
) : Loggable() {
    private var _currentIptv by mutableStateOf(Iptv.EMPTY)
    val currentIptv get() = _currentIptv

    private var _currentIptvUrlIdx by mutableIntStateOf(0)
    val currentIptvUrlIdx get() = _currentIptvUrlIdx

    private var _isPanelVisible by mutableStateOf(false)
    val isPanelVisible get() = _isPanelVisible

    private var _isSettingsVisible by mutableStateOf(false)
    val isSettingsVisible get() = _isSettingsVisible

    private var _isTempPanelVisible by mutableStateOf(false)
    val isTempPanelVisible get() = _isTempPanelVisible

    val timeoutClosePanel = Channel<Long>(Channel.CONFLATED)

    private var _exoPlayer = ExoPlayer.Builder(context).build().apply {
        playWhenReady = true
    }
    val exoPlayer get() = _exoPlayer

    private val dataSourceFactory: DataSource.Factory =
        DefaultDataSource.Factory(context, DefaultHttpDataSource.Factory().apply {
            setUserAgent(Constants.VIDEO_PLAYER_HTTP_USER_AGENT)
            setConnectTimeoutMs(5_000)
            setReadTimeoutMs(5_000)
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
                        val name = currentIptv.name
                        delay(1000)
                        if (name == currentIptv.name) {
                            _isTempPanelVisible = false
                        }
                    }

                    // 记忆可播放的域名
                    SP.iptvPlayableHostList = SP.iptvPlayableHostList.plus(
                        Uri.parse(_currentIptv.urlList[_currentIptvUrlIdx]).host ?: ""
                    )
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                if (_currentIptvUrlIdx < _currentIptv.urlList.size - 1) {
                    changeCurrentIptv(_currentIptv, _currentIptvUrlIdx + 1)
                }

                // 从记忆中删除不可播放的域名
                SP.iptvPlayableHostList = SP.iptvPlayableHostList.minus(
                    Uri.parse(_currentIptv.urlList[_currentIptvUrlIdx]).host ?: ""
                )
            }
        })
    }

    fun changePanelVisible(visible: Boolean) {
        _isPanelVisible = visible
        onActiveAction()
    }

    fun changeSettingsVisible(visible: Boolean) {
        _isSettingsVisible = visible
    }

    fun changeTempPanelVisible(visible: Boolean) {
        _isTempPanelVisible = visible
    }

    fun onActiveAction() {
        timeoutClosePanel.trySend(Constants.UI_PANEL_SCREEN_AUTO_CLOSE_DELAY)
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

    fun changeCurrentIptv(iptv: Iptv, urlIdx: Int? = null) {
        _isPanelVisible = false

        if (iptv == _currentIptv && urlIdx == null) return

        _currentIptv = iptv
        SP.iptvLastIptvIdx = iptvGroupList.iptvIdx(iptv)
        _isTempPanelVisible = true

        _currentIptvUrlIdx = if (urlIdx == null) {
            // 优先从记忆中选择可播放的域名
            max(0, _currentIptv.urlList.indexOfLast {
                SP.iptvPlayableHostList.contains(Uri.parse(it).host ?: "")
            })
        } else {
            (urlIdx + _currentIptv.urlList.size) % _currentIptv.urlList.size
        }

        val url = iptv.urlList[_currentIptvUrlIdx]
        log.d("播放（${_currentIptvUrlIdx + 1}/${_currentIptv.urlList.size}）: $url")

        val uri = Uri.parse(url)
        val contentType = if (uri.path?.endsWith(".php") == true) C.CONTENT_TYPE_HLS else null

        exoPlayer.setMediaSource(getExoPlayerMediaSource(uri, dataSourceFactory, contentType))
        exoPlayer.prepare()
    }

    fun changeCurrentIptvToPrev() {
        changeCurrentIptv(getPrevIptv())
    }

    fun changeCurrentIptvToNext() {
        changeCurrentIptv(getNextIptv())
    }
}

@OptIn(FlowPreview::class)
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun rememberHomeContentState(
    context: Context = LocalContext.current,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): HomeContentState {
    val state = remember {
        HomeContentState(
            context = context,
            coroutineScope = coroutineScope,
            iptvGroupList = iptvGroupList,
        )
    }

    LaunchedEffect(Unit) {
        state.timeoutClosePanel.consumeAsFlow().debounce { it }
            .collect { state.changePanelVisible(false) }
    }

    return state
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