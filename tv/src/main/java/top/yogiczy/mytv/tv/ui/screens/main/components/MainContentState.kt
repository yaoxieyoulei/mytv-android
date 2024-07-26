package top.yogiczy.mytv.tv.ui.screens.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList.Companion.channelIdx
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList.Companion.channelList
import top.yogiczy.mytv.core.data.utils.ChannelUtil
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.core.data.utils.Loggable
import top.yogiczy.mytv.tv.ui.screens.settings.SettingsViewModel
import top.yogiczy.mytv.tv.ui.screens.videoplayer.VideoPlayerState
import top.yogiczy.mytv.tv.ui.screens.videoplayer.rememberVideoPlayerState
import kotlin.math.max

@Stable
class MainContentState(
    private val coroutineScope: CoroutineScope,
    private val videoPlayerState: VideoPlayerState,
    private val channelGroupList: ChannelGroupList,
    private val settingsViewModel: SettingsViewModel,
) : Loggable() {
    private var _currentChannel by mutableStateOf(Channel())
    val currentChannel get() = _currentChannel

    private var _currentChannelUrlIdx by mutableIntStateOf(0)
    val currentChannelUrlIdx get() = _currentChannelUrlIdx

    private var _isTempChannelScreenVisible by mutableStateOf(false)
    var isTempChannelScreenVisible
        get() = _isTempChannelScreenVisible
        set(value) {
            _isTempChannelScreenVisible = value
        }

    private var _isChannelScreenVisible by mutableStateOf(false)
    var isChannelScreenVisible
        get() = _isChannelScreenVisible
        set(value) {
            _isChannelScreenVisible = value
        }

    private var _isSettingsScreenVisible by mutableStateOf(false)
    var isSettingsScreenVisible
        get() = _isSettingsScreenVisible
        set(value) {
            _isSettingsScreenVisible = value
        }

    private var _isQuickOpScreenVisible by mutableStateOf(false)
    var isQuickOpScreenVisible
        get() = _isQuickOpScreenVisible
        set(value) {
            _isQuickOpScreenVisible = value
        }

    private var _isEpgScreenVisible by mutableStateOf(false)
    var isEpgScreenVisible
        get() = _isEpgScreenVisible
        set(value) {
            _isEpgScreenVisible = value
        }

    private var _isChannelUrlScreenVisible by mutableStateOf(false)
    var isChannelUrlScreenVisible
        get() = _isChannelUrlScreenVisible
        set(value) {
            _isChannelUrlScreenVisible = value
        }

    init {
        changeCurrentChannel(channelGroupList.channelList.getOrElse(settingsViewModel.iptvLastChannelIdx) {
            channelGroupList.channelList.firstOrNull() ?: Channel()
        })

        videoPlayerState.onReady {
            settingsViewModel.iptvPlayableHostList += getUrlHost(_currentChannel.urlList[_currentChannelUrlIdx])
            coroutineScope.launch {
                val name = _currentChannel.name
                val urlIdx = _currentChannelUrlIdx
                delay(Constants.UI_TEMP_CHANNEL_SCREEN_SHOW_DURATION)
                if (name == _currentChannel.name && urlIdx == _currentChannelUrlIdx) {
                    _isTempChannelScreenVisible = false
                }
            }
        }

        videoPlayerState.onError {
            settingsViewModel.iptvPlayableHostList -= getUrlHost(_currentChannel.urlList[_currentChannelUrlIdx])

            if (_currentChannelUrlIdx < _currentChannel.urlList.size - 1) {
                changeCurrentChannel(_currentChannel, _currentChannelUrlIdx + 1)
            }
        }

        videoPlayerState.onInterrupt {
            changeCurrentChannel(_currentChannel, _currentChannelUrlIdx)
        }
    }

    private fun getPrevFavoriteChannel(): Channel? {
        if (!settingsViewModel.iptvChannelFavoriteListVisible) return null

        val favoriteChannelNameList = settingsViewModel.iptvChannelFavoriteList
        val favoriteChannelList =
            channelGroupList.channelList.filter { it.name in favoriteChannelNameList }

        return if (_currentChannel in favoriteChannelList && _currentChannel != favoriteChannelList.first()) {
            val currentIdx = favoriteChannelList.indexOf(_currentChannel)
            favoriteChannelList[currentIdx - 1]
        } else if (settingsViewModel.iptvChannelFavoriteChangeBoundaryJumpOut) {
            settingsViewModel.iptvChannelFavoriteListVisible = false
            channelGroupList.channelList.lastOrNull()
        } else {
            favoriteChannelList.lastOrNull()
        }

    }

    private fun getNextFavoriteChannel(): Channel? {
        if (!settingsViewModel.iptvChannelFavoriteListVisible) return null

        val favoriteChannelNameList = settingsViewModel.iptvChannelFavoriteList
        val favoriteChannelList =
            channelGroupList.channelList.filter { it.name in favoriteChannelNameList }

        return if (_currentChannel in favoriteChannelList && _currentChannel != favoriteChannelList.last()) {
            val currentIdx = favoriteChannelList.indexOf(_currentChannel)
            favoriteChannelList[currentIdx + 1]
        } else if (settingsViewModel.iptvChannelFavoriteChangeBoundaryJumpOut) {
            settingsViewModel.iptvChannelFavoriteListVisible = false
            channelGroupList.channelList.firstOrNull()
        } else {
            favoriteChannelList.firstOrNull()
        }
    }

    private fun getPrevChannel(): Channel {
        return getPrevFavoriteChannel() ?: run {
            val currentIdx = channelGroupList.channelIdx(_currentChannel)
            return channelGroupList.channelList.getOrElse(currentIdx - 1) {
                channelGroupList.channelList.lastOrNull() ?: Channel()
            }
        }
    }

    private fun getNextChannel(): Channel {
        return getNextFavoriteChannel() ?: run {
            val currentIdx = channelGroupList.channelIdx(_currentChannel)
            return channelGroupList.channelList.getOrElse(currentIdx + 1) {
                channelGroupList.channelList.firstOrNull() ?: Channel()
            }
        }
    }

    fun changeCurrentChannel(channel: Channel, urlIdx: Int? = null) {
        if (channel == _currentChannel && urlIdx == null) return

        if (channel == _currentChannel && urlIdx != _currentChannelUrlIdx) {
            settingsViewModel.iptvPlayableHostList -= getUrlHost(_currentChannel.urlList[_currentChannelUrlIdx])
        }

        _isTempChannelScreenVisible = true

        _currentChannel = channel
        settingsViewModel.iptvLastChannelIdx = channelGroupList.channelIdx(_currentChannel)

        _currentChannelUrlIdx = if (urlIdx == null) {
            max(0, _currentChannel.urlList.indexOfFirst {
                settingsViewModel.iptvPlayableHostList.contains(getUrlHost(it))
            })
        } else {
            (urlIdx + _currentChannel.urlList.size) % _currentChannel.urlList.size
        }

        val url = _currentChannel.urlList[_currentChannelUrlIdx]
        log.d("播放${_currentChannel.name}（${_currentChannelUrlIdx + 1}/${_currentChannel.urlList.size}）: $url")

        if (ChannelUtil.isHybridWebViewUrl(url)) {
            videoPlayerState.stop()
        } else {
            videoPlayerState.prepare(url)
        }
    }

    fun changeCurrentChannelToPrev() {
        changeCurrentChannel(getPrevChannel())
    }

    fun changeCurrentChannelToNext() {
        changeCurrentChannel(getNextChannel())
    }
}

@Composable
fun rememberMainContentState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    videoPlayerState: VideoPlayerState = rememberVideoPlayerState(),
    channelGroupList: ChannelGroupList = ChannelGroupList(),
    settingsViewModel: SettingsViewModel = viewModel(),
) = remember {
    MainContentState(
        coroutineScope = coroutineScope,
        videoPlayerState = videoPlayerState,
        channelGroupList = channelGroupList,
        settingsViewModel = settingsViewModel,
    )
}

private fun getUrlHost(url: String): String {
    return url.split("://").getOrElse(1) { "" }.split("/").firstOrNull() ?: url
}