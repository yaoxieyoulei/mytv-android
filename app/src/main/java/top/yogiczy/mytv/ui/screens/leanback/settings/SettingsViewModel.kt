package top.yogiczy.mytv.ui.screens.leanback.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import top.yogiczy.mytv.ui.utils.SP

class LeanbackSettingsViewModel : ViewModel() {
    private var _appBootLaunch by mutableStateOf(SP.appBootLaunch)
    var appBootLaunch: Boolean
        get() = _appBootLaunch
        set(value) {
            _appBootLaunch = value
            SP.appBootLaunch = value
        }

    private var _appLastLatestVersion by mutableStateOf(SP.appLastLatestVersion)
    var appLastLatestVersion: String
        get() = _appLastLatestVersion
        set(value) {
            _appLastLatestVersion = value
            SP.appLastLatestVersion = value
        }

    private var _appDeviceDisplayType by mutableStateOf(SP.appDeviceDisplayType)
    var appDeviceDisplayType: SP.AppDeviceDisplayType
        get() = _appDeviceDisplayType
        set(value) {
            _appDeviceDisplayType = value
            SP.appDeviceDisplayType = value
        }

    private var _debugShowFps by mutableStateOf(SP.debugShowFps)
    var debugShowFps: Boolean
        get() = _debugShowFps
        set(value) {
            _debugShowFps = value
            SP.debugShowFps = value
        }

    private var _debugShowVideoPlayerMetadata by mutableStateOf(SP.debugShowVideoPlayerMetadata)
    var debugShowVideoPlayerMetadata: Boolean
        get() = _debugShowVideoPlayerMetadata
        set(value) {
            _debugShowVideoPlayerMetadata = value
            SP.debugShowVideoPlayerMetadata = value
        }

    private var _iptvLastIptvIdx by mutableIntStateOf(SP.iptvLastIptvIdx)
    var iptvLastIptvIdx: Int
        get() = _iptvLastIptvIdx
        set(value) {
            _iptvLastIptvIdx = value
            SP.iptvLastIptvIdx = value
        }

    private var _iptvChannelChangeFlip by mutableStateOf(SP.iptvChannelChangeFlip)
    var iptvChannelChangeFlip: Boolean
        get() = _iptvChannelChangeFlip
        set(value) {
            _iptvChannelChangeFlip = value
            SP.iptvChannelChangeFlip = value
        }

    private var _iptvSourceSimplify by mutableStateOf(SP.iptvSourceSimplify)
    var iptvSourceSimplify: Boolean
        get() = _iptvSourceSimplify
        set(value) {
            _iptvSourceSimplify = value
            SP.iptvSourceSimplify = value
        }

    private var _iptvSourceCacheTime by mutableLongStateOf(SP.iptvSourceCacheTime)
    var iptvSourceCacheTime: Long
        get() = _iptvSourceCacheTime
        set(value) {
            _iptvSourceCacheTime = value
            SP.iptvSourceCacheTime = value
        }

    private var _iptvSourceUrl by mutableStateOf(SP.iptvSourceUrl)
    var iptvSourceUrl: String
        get() = _iptvSourceUrl
        set(value) {
            _iptvSourceUrl = value
            SP.iptvSourceUrl = value
        }

    private var _iptvPlayableHostList by mutableStateOf(SP.iptvPlayableHostList)
    var iptvPlayableHostList: Set<String>
        get() = _iptvPlayableHostList
        set(value) {
            _iptvPlayableHostList = value
            SP.iptvPlayableHostList = value
        }

    private var _iptvChannelNoSelectEnable by mutableStateOf(SP.iptvChannelNoSelectEnable)
    var iptvChannelNoSelectEnable: Boolean
        get() = _iptvChannelNoSelectEnable
        set(value) {
            _iptvChannelNoSelectEnable = value
            SP.iptvChannelNoSelectEnable = value
        }

    private var _iptvSourceUrlHistoryList by mutableStateOf(SP.iptvSourceUrlHistoryList)
    var iptvSourceUrlHistoryList: Set<String>
        get() = _iptvSourceUrlHistoryList
        set(value) {
            _iptvSourceUrlHistoryList = value
            SP.iptvSourceUrlHistoryList = value
        }

    private var _iptvChannelFavoriteEnable by mutableStateOf(SP.iptvChannelFavoriteEnable)
    var iptvChannelFavoriteEnable: Boolean
        get() = _iptvChannelFavoriteEnable
        set(value) {
            _iptvChannelFavoriteEnable = value
            SP.iptvChannelFavoriteEnable = value
        }

    private var _iptvChannelFavoriteListVisible by mutableStateOf(SP.iptvChannelFavoriteListVisible)
    var iptvChannelFavoriteListVisible: Boolean
        get() = _iptvChannelFavoriteListVisible
        set(value) {
            _iptvChannelFavoriteListVisible = value
            SP.iptvChannelFavoriteListVisible = value
        }

    private var _iptvChannelFavoriteList by mutableStateOf(SP.iptvChannelFavoriteList)
    var iptvChannelFavoriteList: Set<String>
        get() = _iptvChannelFavoriteList
        set(value) {
            _iptvChannelFavoriteList = value
            SP.iptvChannelFavoriteList = value
        }

    private var _epgEnable by mutableStateOf(SP.epgEnable)
    var epgEnable: Boolean
        get() = _epgEnable
        set(value) {
            _epgEnable = value
            SP.epgEnable = value
        }

    private var _epgXmlUrl by mutableStateOf(SP.epgXmlUrl)
    var epgXmlUrl: String
        get() = _epgXmlUrl
        set(value) {
            _epgXmlUrl = value
            SP.epgXmlUrl = value
        }

    private var _epgRefreshTimeThreshold by mutableIntStateOf(SP.epgRefreshTimeThreshold)
    var epgRefreshTimeThreshold: Int
        get() = _epgRefreshTimeThreshold
        set(value) {
            _epgRefreshTimeThreshold = value
            SP.epgRefreshTimeThreshold = value
        }

    private var _epgXmlUrlHistoryList by mutableStateOf(SP.epgXmlUrlHistoryList)
    var epgXmlUrlHistoryList: Set<String>
        get() = _epgXmlUrlHistoryList
        set(value) {
            _epgXmlUrlHistoryList = value
            SP.epgXmlUrlHistoryList = value
        }

    private var _uiShowEpgProgrammeProgress by mutableStateOf(SP.uiShowEpgProgrammeProgress)
    var uiShowEpgProgrammeProgress: Boolean
        get() = _uiShowEpgProgrammeProgress
        set(value) {
            _uiShowEpgProgrammeProgress = value
            SP.uiShowEpgProgrammeProgress = value
        }

    private var _uiUseClassicPanelScreen by mutableStateOf(SP.uiUseClassicPanelScreen)
    var uiUseClassicPanelScreen: Boolean
        get() = _uiUseClassicPanelScreen
        set(value) {
            _uiUseClassicPanelScreen = value
            SP.uiUseClassicPanelScreen = value
        }

    private var _uiDensityScaleRatio by mutableFloatStateOf(SP.uiDensityScaleRatio)
    var uiDensityScaleRatio: Float
        get() = _uiDensityScaleRatio
        set(value) {
            _uiDensityScaleRatio = value
            SP.uiDensityScaleRatio = value
        }

    private var _uiFontScaleRatio by mutableFloatStateOf(SP.uiFontScaleRatio)
    var uiFontScaleRatio: Float
        get() = _uiFontScaleRatio
        set(value) {
            _uiFontScaleRatio = value
            SP.uiFontScaleRatio = value
        }

    private var _uiTimeShowMode by mutableStateOf(SP.uiTimeShowMode)
    var uiTimeShowMode: SP.UiTimeShowMode
        get() = _uiTimeShowMode
        set(value) {
            _uiTimeShowMode = value
            SP.uiTimeShowMode = value
        }

    private var _uiPipMode by mutableStateOf(SP.uiPipMode)
    var uiPipMode: Boolean
        get() = _uiPipMode
        set(value) {
            _uiPipMode = value
            SP.uiPipMode = value
        }

    private var _updateForceRemind by mutableStateOf(SP.updateForceRemind)
    var updateForceRemind: Boolean
        get() = _updateForceRemind
        set(value) {
            _updateForceRemind = value
            SP.updateForceRemind = value
        }

    private var _videoPlayerUserAgent by mutableStateOf(SP.videoPlayerUserAgent)
    var videoPlayerUserAgent: String
        get() = _videoPlayerUserAgent
        set(value) {
            _videoPlayerUserAgent = value
            SP.videoPlayerUserAgent = value
        }

    private var _videoPlayerLoadTimeout by mutableLongStateOf(SP.videoPlayerLoadTimeout)
    var videoPlayerLoadTimeout: Long
        get() = _videoPlayerLoadTimeout
        set(value) {
            _videoPlayerLoadTimeout = value
            SP.videoPlayerLoadTimeout = value
        }

    private var _videoPlayerAspectRatio by mutableStateOf(SP.videoPlayerAspectRatio)
    var videoPlayerAspectRatio: SP.VideoPlayerAspectRatio
        get() = _videoPlayerAspectRatio
        set(value) {
            _videoPlayerAspectRatio = value
            SP.videoPlayerAspectRatio = value
        }
}