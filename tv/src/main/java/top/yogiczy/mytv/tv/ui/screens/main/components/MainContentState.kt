package top.yogiczy.mytv.tv.ui.screens.main.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import top.yogiczy.mytv.tv.ui.screens.videoplayer.VideoPlayerState
import top.yogiczy.mytv.tv.ui.screens.videoplayer.rememberVideoPlayerState
import top.yogiczy.mytv.tv.ui.utils.Configs
import kotlin.math.max

@Stable
class MainContentState(
    private val coroutineScope: CoroutineScope,
    private val videoPlayerState: VideoPlayerState,
    private val channelGroupList: ChannelGroupList,
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

    private var _isGuideScreenVisible by mutableStateOf(false)
    var isGuideScreenVisible
        get() = _isGuideScreenVisible
        set(value) {
            _isGuideScreenVisible = value
        }

    private var _isIptvSourceScreenVisible by mutableStateOf(false)
    var isIptvSourceScreenVisible
        get() = _isIptvSourceScreenVisible
        set(value) {
            _isIptvSourceScreenVisible = value
        }

    private var _isEpgSourceScreenVisible by mutableStateOf(false)
    var isEpgSourceScreenVisible
        get() = _isEpgSourceScreenVisible
        set(value) {
            _isEpgSourceScreenVisible = value
        }

    init {
        changeCurrentChannel(channelGroupList.channelList.getOrElse(Configs.iptvLastChannelIdx) {
            channelGroupList.channelList.firstOrNull() ?: Channel()
        })

        videoPlayerState.onReady {
            Configs.iptvPlayableHostList += getUrlHost(_currentChannel.urlList[_currentChannelUrlIdx])
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
            Configs.iptvPlayableHostList -= getUrlHost(_currentChannel.urlList[_currentChannelUrlIdx])

            if (_currentChannelUrlIdx < _currentChannel.urlList.size - 1) {
                changeCurrentChannel(_currentChannel, _currentChannelUrlIdx + 1)
            }
        }

        videoPlayerState.onInterrupt {
            changeCurrentChannel(_currentChannel, _currentChannelUrlIdx)
        }
    }

    private fun getPrevChannel(): Channel {
        val currentIdx = channelGroupList.channelIdx(_currentChannel)
        return channelGroupList.channelList.getOrElse(currentIdx - 1) {
            channelGroupList.channelList.lastOrNull() ?: Channel()
        }
    }

    private fun getNextChannel(): Channel {
        val currentIdx = channelGroupList.channelIdx(_currentChannel)
        return channelGroupList.channelList.getOrElse(currentIdx + 1) {
            channelGroupList.channelList.firstOrNull() ?: Channel()
        }
    }

    fun changeCurrentChannel(channel: Channel, urlIdx: Int? = null) {
        if (channel == _currentChannel && urlIdx == null) return

        if (channel == _currentChannel && urlIdx != _currentChannelUrlIdx) {
            Configs.iptvPlayableHostList -= getUrlHost(_currentChannel.urlList[_currentChannelUrlIdx])
        }

        _isTempChannelScreenVisible = true

        _currentChannel = channel
        Configs.iptvLastChannelIdx = channelGroupList.channelIdx(_currentChannel)

        _currentChannelUrlIdx = if (urlIdx == null) {
            max(0, _currentChannel.urlList.indexOfFirst {
                Configs.iptvPlayableHostList.contains(getUrlHost(it))
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
) = remember {
    MainContentState(
        coroutineScope = coroutineScope,
        videoPlayerState = videoPlayerState,
        channelGroupList = channelGroupList,
    )
}

private fun getUrlHost(url: String): String {
    return url.split("://").getOrElse(1) { "" }.split("/").firstOrNull() ?: url
}