package top.yogiczy.mytv.ui.screens.home.components

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
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
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yogiczy.mytv.data.entities.EpgList
import top.yogiczy.mytv.data.entities.EpgList.Companion.currentProgrammes
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.data.entities.IptvGroupList
import top.yogiczy.mytv.data.entities.IptvGroupList.Companion.iptvIdx
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.screens.classicpanel.ClassicPanelScreen
import top.yogiczy.mytv.ui.screens.monitor.MonitorScreen
import top.yogiczy.mytv.ui.screens.panel.DigitChannelSelectState
import top.yogiczy.mytv.ui.screens.panel.PanelDigitChannelSelectScreen
import top.yogiczy.mytv.ui.screens.panel.PanelScreen
import top.yogiczy.mytv.ui.screens.panel.PanelTempScreen
import top.yogiczy.mytv.ui.screens.panel.PanelTimeScreen
import top.yogiczy.mytv.ui.screens.panel.rememberDigitChannelSelectState
import top.yogiczy.mytv.ui.screens.settings.SettingsScreen
import top.yogiczy.mytv.ui.screens.settings.SettingsState
import top.yogiczy.mytv.ui.screens.settings.components.SettingsUpdaterDialog
import top.yogiczy.mytv.ui.screens.settings.components.UpdateState
import top.yogiczy.mytv.ui.screens.settings.components.rememberUpdateState
import top.yogiczy.mytv.ui.screens.settings.rememberSettingsState
import top.yogiczy.mytv.ui.screens.video.PlayerState
import top.yogiczy.mytv.ui.screens.video.VideoScreen
import top.yogiczy.mytv.ui.screens.video.rememberPlayerState
import top.yogiczy.mytv.ui.utils.Loggable
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents
import top.yogiczy.mytv.ui.utils.handleDragGestures
import kotlin.math.max

@OptIn(UnstableApi::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    epgList: EpgList = EpgList(),
    onBackPressed: () -> Unit = {},
    settingsState: SettingsState = rememberSettingsState(),
    updateState: UpdateState = rememberUpdateState(forceRemind = settingsState.updateForceRemind),
    homeState: HomeContentState = rememberHomeContentState(
        iptvGroupList = iptvGroupList,
        settingsState = settingsState,
    ),
    playerState: PlayerState = rememberPlayerState(homeState.exoPlayer),
    digitChannelSelectState: DigitChannelSelectState = rememberDigitChannelSelectState { channelNo ->
        if (channelNo.toInt() - 1 in 0..<iptvGroupList.flatMap { it.iptvs }.size) {
            homeState.changeCurrentIptv(iptvGroupList.flatMap { it.iptvs }[channelNo.toInt() - 1])
        }
    },
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(3000)
        updateState.checkUpdate()
    }

    LaunchedEffect(homeState.isPanelVisible, homeState.isSettingsVisible) {
        if (!homeState.isPanelVisible && !homeState.isSettingsVisible) {
            focusRequester.requestFocus()
            delay(1)
            focusRequester.requestFocus()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                homeState.exoPlayer.play()
            } else if (event == Lifecycle.Event.ON_STOP) {
                homeState.exoPlayer.pause()
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
            if (homeState.isPanelVisible) homeState.changePanelVisible(false)
            else if (homeState.isSettingsVisible) homeState.changeSettingsVisible(false)
            else onBackPressed()
        },
    ) {
        VideoScreen(
            modifier = Modifier
                .handleDPadKeyEvents(
                    onUp = {
                        if (settingsState.iptvChannelChangeFlip) homeState.changeCurrentIptvToNext()
                        else homeState.changeCurrentIptvToPrev()
                    },
                    onDown = {
                        if (settingsState.iptvChannelChangeFlip) homeState.changeCurrentIptvToPrev()
                        else homeState.changeCurrentIptvToNext()
                    },
                    onLeft = {
                        if (homeState.currentIptv.urlList.size > 1) {
                            homeState.changeCurrentIptv(
                                homeState.currentIptv, homeState.currentIptvUrlIdx - 1
                            )
                        }
                    },
                    onRight = {
                        if (homeState.currentIptv.urlList.size > 1) {
                            homeState.changeCurrentIptv(
                                homeState.currentIptv, homeState.currentIptvUrlIdx + 1
                            )
                        }
                    },
                    onSelect = { homeState.changePanelVisible(true) },
                    onLongSelect = { homeState.changeSettingsVisible(true) },
                    onSettings = { homeState.changeSettingsVisible(true) },
                    onNumber = {
                        homeState.changeTempPanelVisible(false)
                        digitChannelSelectState.input(it)
                    },
                )
                .handleDragGestures(
                    onSwipeDown = {
                        if (settingsState.iptvChannelChangeFlip) homeState.changeCurrentIptvToNext()
                        else homeState.changeCurrentIptvToPrev()
                    },
                    onSwipeUp = {
                        if (settingsState.iptvChannelChangeFlip) homeState.changeCurrentIptvToPrev()
                        else homeState.changeCurrentIptvToNext()
                    },
                    onSwipeRight = {
                        if (homeState.currentIptv.urlList.size > 1) {
                            homeState.changeCurrentIptv(
                                homeState.currentIptv, homeState.currentIptvUrlIdx - 1
                            )
                        }
                    },
                    onSwipeLeft = {
                        if (homeState.currentIptv.urlList.size > 1) {
                            homeState.changeCurrentIptv(
                                homeState.currentIptv, homeState.currentIptvUrlIdx + 1
                            )
                        }
                    },
                )
                .focusRequester(focusRequester)
                .focusable(),
            exoPlayer = homeState.exoPlayer,
            state = playerState,
            showPlayerInfo = settingsState.debugShowPlayerInfo,
        )

        PanelTimeScreen(showMode = settingsState.uiTimeShowMode)

        if (homeState.isTempPanelVisible) {
            PanelTempScreen(
                channelNo = iptvGroupList.iptvIdx(homeState.currentIptv) + 1,
                currentIptv = homeState.currentIptv,
                currentIptvUrlIdx = homeState.currentIptvUrlIdx,
                playerError = playerState.error,
                currentProgrammes = epgList.currentProgrammes(homeState.currentIptv),
                showProgrammeProgress = settingsState.uiShowEpgProgrammeProgress,
            )
        }

        PanelDigitChannelSelectScreen(state = digitChannelSelectState)

        var showFavoriteList by remember { mutableStateOf(false) }
        AnimatedVisibility(homeState.isPanelVisible, enter = fadeIn(), exit = fadeOut()) {
            if (settingsState.uiUseClassicPanelScreen) {
                ClassicPanelScreen(
                    currentIptv = homeState.currentIptv,
                    currentIptvUrlIdx = homeState.currentIptvUrlIdx,
                    iptvGroupList = iptvGroupList,
                    epgList = epgList,
                    onIptvSelected = { homeState.changeCurrentIptv(it) },
                    settingsState = settingsState,
                    playerState = playerState,
                    onClose = { homeState.changePanelVisible(false) },
                )
            } else {
                PanelScreen(
                    currentIptv = homeState.currentIptv,
                    currentIptvUrlIdx = homeState.currentIptvUrlIdx,
                    iptvGroupList = iptvGroupList,
                    epgList = epgList,
                    onIptvSelected = { homeState.changeCurrentIptv(it) },
                    showFavoriteList = showFavoriteList,
                    onChangeShowFavoriteList = { showFavoriteList = it },
                    settingsState = settingsState,
                    playerState = playerState,
                    onClose = { homeState.changePanelVisible(false) },
                )
            }
        }

        AnimatedVisibility(homeState.isSettingsVisible, enter = fadeIn(), exit = fadeOut()) {
            SettingsScreen(
                settingsState = settingsState,
                updateState = updateState,
                onClose = { homeState.changeSettingsVisible(false) },
            )
        }

        if (settingsState.debugShowFps) {
            MonitorScreen()
        }
    }

    val coroutineScope = rememberCoroutineScope()
    SettingsUpdaterDialog(
        showDialog = updateState.showDialog,
        onDismissRequest = { updateState.showDialog = false },
        release = updateState.latestRelease,
        onUpdateAndInstall = {
            updateState.showDialog = false
            coroutineScope.launch(Dispatchers.IO) {
                updateState.downloadAndUpdate()
            }
        },
    )
}

@Composable
private fun BackPressHandledArea(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) = Box(
    modifier = Modifier
        .onPreviewKeyEvent {
            if (it.key == Key.Back && it.type == KeyEventType.KeyUp) {
                onBackPressed()
                true
            } else {
                false
            }
        }
        .then(modifier),
    content = content,
)

@UnstableApi
class HomeContentState(
    context: Context,
    coroutineScope: CoroutineScope,
    private val iptvGroupList: IptvGroupList,
    private val settingsState: SettingsState,
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
        if (settingsState.iptvLastIptvIdx in 0..<iptvGroupList.flatMap { it.iptvs }.size) {
            changeCurrentIptv(iptvGroupList.flatMap { it.iptvs }[settingsState.iptvLastIptvIdx])
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
                    settingsState.iptvPlayableHostList += Uri.parse(_currentIptv.urlList[_currentIptvUrlIdx]).host
                        ?: ""
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                if (_currentIptvUrlIdx < _currentIptv.urlList.size - 1) {
                    changeCurrentIptv(_currentIptv, _currentIptvUrlIdx + 1)
                }

                // 从记忆中删除不可播放的域名
                settingsState.iptvPlayableHostList -= Uri.parse(_currentIptv.urlList[_currentIptvUrlIdx]).host
                    ?: ""

                // 当解析容器不支持时，尝试使用 HLS 解析
                if (error.errorCode == PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED) {
                    val uri = _exoPlayer.currentMediaItem?.localConfiguration?.uri
                    if (uri != null) {
                        log.w("尝试使用 HLS 解析：$uri")
                        val contentType = Util.inferContentType(uri)
                        if (contentType == C.CONTENT_TYPE_OTHER) {
                            exoPlayer.setMediaSource(
                                getExoPlayerMediaSource(
                                    uri,
                                    dataSourceFactory,
                                    C.CONTENT_TYPE_HLS,
                                )
                            )
                            exoPlayer.prepare()
                        }
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

    fun changeCurrentIptv(iptv: Iptv, urlIdx: Int? = null) {
        _isPanelVisible = false

        if (iptv == _currentIptv && urlIdx == null) return

        _currentIptv = iptv
        settingsState.iptvLastIptvIdx = iptvGroupList.iptvIdx(iptv)
        _isTempPanelVisible = true

        _currentIptvUrlIdx = if (urlIdx == null) {
            // 优先从记忆中选择可播放的域名
            max(0, _currentIptv.urlList.indexOfLast {
                settingsState.iptvPlayableHostList.contains(Uri.parse(it).host ?: "")
            })
        } else {
            (urlIdx + _currentIptv.urlList.size) % _currentIptv.urlList.size
        }

        val url = iptv.urlList[_currentIptvUrlIdx]
        log.d("播放（${_currentIptvUrlIdx + 1}/${_currentIptv.urlList.size}）: $url")

        exoPlayer.setMediaSource(getExoPlayerMediaSource(Uri.parse(url), dataSourceFactory))
        exoPlayer.prepare()
    }

    fun changeCurrentIptvToPrev() {
        changeCurrentIptv(getPrevIptv())
    }

    fun changeCurrentIptvToNext() {
        changeCurrentIptv(getNextIptv())
    }
}

@OptIn(UnstableApi::class)
@Composable
fun rememberHomeContentState(
    context: Context = LocalContext.current,
    iptvGroupList: IptvGroupList = IptvGroupList(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    settingsState: SettingsState = rememberSettingsState(),
): HomeContentState {
    val state = remember {
        HomeContentState(
            context = context,
            coroutineScope = coroutineScope,
            iptvGroupList = iptvGroupList,
            settingsState = settingsState,
        )
    }

    return state
}

@OptIn(UnstableApi::class)
fun getExoPlayerMediaSource(
    uri: Uri,
    dataSourceFactory: DataSource.Factory,
    contentType: Int? = null,
): MediaSource {
    val mediaItem = MediaItem.fromUri(uri)

    return when (val type = contentType ?: Util.inferContentType(uri)) {
        C.CONTENT_TYPE_HLS -> {
            HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        }

        C.CONTENT_TYPE_OTHER -> {
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        }

        else -> {
            throw IllegalStateException("Unsupported type: $type")
        }
    }
}