package top.yogiczy.mytv.tv.ui.screensold.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeReserveList
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSource
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSourceList
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSource
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSourceList
import top.yogiczy.mytv.tv.ui.screensold.videoplayer.VideoPlayerDisplayMode
import top.yogiczy.mytv.tv.ui.utils.Configs

class SettingsViewModel : ViewModel() {
    private var _appBootLaunch by mutableStateOf(false)
    var appBootLaunch: Boolean
        get() = _appBootLaunch
        set(value) {
            _appBootLaunch = value
            Configs.appBootLaunch = value
        }

    private var _appLastLatestVersion by mutableStateOf("")
    var appLastLatestVersion: String
        get() = _appLastLatestVersion
        set(value) {
            _appLastLatestVersion = value
            Configs.appLastLatestVersion = value
        }

    private var _appAgreementAgreed by mutableStateOf(false)
    var appAgreementAgreed: Boolean
        get() = _appAgreementAgreed
        set(value) {
            _appAgreementAgreed = value
            Configs.appAgreementAgreed = value
        }

    private var _debugShowFps by mutableStateOf(false)
    var debugShowFps: Boolean
        get() = _debugShowFps
        set(value) {
            _debugShowFps = value
            Configs.debugShowFps = value
        }

    private var _debugShowVideoPlayerMetadata by mutableStateOf(false)
    var debugShowVideoPlayerMetadata: Boolean
        get() = _debugShowVideoPlayerMetadata
        set(value) {
            _debugShowVideoPlayerMetadata = value
            Configs.debugShowVideoPlayerMetadata = value
        }

    private var _debugShowLayoutGrids by mutableStateOf(false)
    var debugShowLayoutGrids: Boolean
        get() = _debugShowLayoutGrids
        set(value) {
            _debugShowLayoutGrids = value
            Configs.debugShowLayoutGrids = value
        }

    private var _iptvLastChannelIdx by mutableIntStateOf(0)
    var iptvLastChannelIdx: Int
        get() = _iptvLastChannelIdx
        set(value) {
            _iptvLastChannelIdx = value
            Configs.iptvLastChannelIdx = value
        }

    private var _iptvChannelChangeFlip by mutableStateOf(false)
    var iptvChannelChangeFlip: Boolean
        get() = _iptvChannelChangeFlip
        set(value) {
            _iptvChannelChangeFlip = value
            Configs.iptvChannelChangeFlip = value
        }

    private var _iptvSourceCacheTime by mutableLongStateOf(0)
    var iptvSourceCacheTime: Long
        get() = _iptvSourceCacheTime
        set(value) {
            _iptvSourceCacheTime = value
            Configs.iptvSourceCacheTime = value
        }

    private var _iptvSourceCurrent by mutableStateOf(IptvSource())
    var iptvSourceCurrent: IptvSource
        get() = _iptvSourceCurrent
        set(value) {
            _iptvSourceCurrent = value
            Configs.iptvSourceCurrent = value
        }

    private var _iptvSourceList by mutableStateOf(IptvSourceList())
    var iptvSourceList: IptvSourceList
        get() = _iptvSourceList
        set(value) {
            _iptvSourceList = value
            Configs.iptvSourceList = value
        }

    private var _iptvPlayableHostList by mutableStateOf(emptySet<String>())
    var iptvPlayableHostList: Set<String>
        get() = _iptvPlayableHostList
        set(value) {
            _iptvPlayableHostList = value
            Configs.iptvPlayableHostList = value
        }

    private var _iptvChannelNoSelectEnable by mutableStateOf(false)
    var iptvChannelNoSelectEnable: Boolean
        get() = _iptvChannelNoSelectEnable
        set(value) {
            _iptvChannelNoSelectEnable = value
            Configs.iptvChannelNoSelectEnable = value
        }

    private var _iptvChannelFavoriteEnable by mutableStateOf(false)
    var iptvChannelFavoriteEnable: Boolean
        get() = _iptvChannelFavoriteEnable
        set(value) {
            _iptvChannelFavoriteEnable = value
            Configs.iptvChannelFavoriteEnable = value
        }

    private var _iptvChannelFavoriteListVisible by mutableStateOf(false)
    var iptvChannelFavoriteListVisible: Boolean
        get() = _iptvChannelFavoriteListVisible
        set(value) {
            _iptvChannelFavoriteListVisible = value
            Configs.iptvChannelFavoriteListVisible = value
        }

    private var _iptvChannelFavoriteList by mutableStateOf(emptySet<String>())
    var iptvChannelFavoriteList: Set<String>
        get() = _iptvChannelFavoriteList
        set(value) {
            _iptvChannelFavoriteList = value
            Configs.iptvChannelFavoriteList = value
        }

    private var _iptvChannelFavoriteChangeBoundaryJumpOut by mutableStateOf(false)
    var iptvChannelFavoriteChangeBoundaryJumpOut: Boolean
        get() = _iptvChannelFavoriteChangeBoundaryJumpOut
        set(value) {
            _iptvChannelFavoriteChangeBoundaryJumpOut = value
            Configs.iptvChannelFavoriteChangeBoundaryJumpOut = value
        }

    private var _iptvChannelGroupHiddenList by mutableStateOf(emptySet<String>())
    var iptvChannelGroupHiddenList: Set<String>
        get() = _iptvChannelGroupHiddenList
        set(value) {
            _iptvChannelGroupHiddenList = value
            Configs.iptvChannelGroupHiddenList = value
        }

    private var _iptvHybridMode by mutableStateOf(Configs.IptvHybridMode.DISABLE)
    var iptvHybridMode: Configs.IptvHybridMode
        get() = _iptvHybridMode
        set(value) {
            _iptvHybridMode = value
            Configs.iptvHybridMode = value
        }

    private var _iptvSimilarChannelMerge by mutableStateOf(false)
    var iptvSimilarChannelMerge: Boolean
        get() = _iptvSimilarChannelMerge
        set(value) {
            _iptvSimilarChannelMerge = value
            Configs.iptvSimilarChannelMerge = value
        }

    private var _epgEnable by mutableStateOf(false)
    var epgEnable: Boolean
        get() = _epgEnable
        set(value) {
            _epgEnable = value
            Configs.epgEnable = value
        }

    private var _epgSourceCurrent by mutableStateOf(EpgSource())
    var epgSourceCurrent: EpgSource
        get() = _epgSourceCurrent
        set(value) {
            _epgSourceCurrent = value
            Configs.epgSourceCurrent = value
        }

    private var _epgSourceList by mutableStateOf(EpgSourceList())
    var epgSourceList: EpgSourceList
        get() = _epgSourceList
        set(value) {
            _epgSourceList = value
            Configs.epgSourceList = value
        }

    private var _epgRefreshTimeThreshold by mutableIntStateOf(0)
    var epgRefreshTimeThreshold: Int
        get() = _epgRefreshTimeThreshold
        set(value) {
            _epgRefreshTimeThreshold = value
            Configs.epgRefreshTimeThreshold = value
        }

    private var _epgChannelReserveList by mutableStateOf(EpgProgrammeReserveList())
    var epgChannelReserveList: EpgProgrammeReserveList
        get() = _epgChannelReserveList
        set(value) {
            _epgChannelReserveList = value
            Configs.epgChannelReserveList = value
        }

    private var _uiShowEpgProgrammeProgress by mutableStateOf(false)
    var uiShowEpgProgrammeProgress: Boolean
        get() = _uiShowEpgProgrammeProgress
        set(value) {
            _uiShowEpgProgrammeProgress = value
            Configs.uiShowEpgProgrammeProgress = value
        }

    private var _uiShowEpgProgrammePermanentProgress by mutableStateOf(false)
    var uiShowEpgProgrammePermanentProgress: Boolean
        get() = _uiShowEpgProgrammePermanentProgress
        set(value) {
            _uiShowEpgProgrammePermanentProgress = value
            Configs.uiShowEpgProgrammePermanentProgress = value
        }

    private var _uiShowChannelLogo by mutableStateOf(false)
    var uiShowChannelLogo: Boolean
        get() = _uiShowChannelLogo
        set(value) {
            _uiShowChannelLogo = value
            Configs.uiShowChannelLogo = value
        }

    private var _uiShowChannelPreview by mutableStateOf(false)
    var uiShowChannelPreview: Boolean
        get() = _uiShowChannelPreview
        set(value) {
            _uiShowChannelPreview = value
            Configs.uiShowChannelPreview = value
        }

    private var _uiUseClassicPanelScreen by mutableStateOf(false)
    var uiUseClassicPanelScreen: Boolean
        get() = _uiUseClassicPanelScreen
        set(value) {
            _uiUseClassicPanelScreen = value
            Configs.uiUseClassicPanelScreen = value
        }

    private var _uiDensityScaleRatio by mutableFloatStateOf(0f)
    var uiDensityScaleRatio: Float
        get() = _uiDensityScaleRatio
        set(value) {
            _uiDensityScaleRatio = value
            Configs.uiDensityScaleRatio = value
        }

    private var _uiFontScaleRatio by mutableFloatStateOf(1f)
    var uiFontScaleRatio: Float
        get() = _uiFontScaleRatio
        set(value) {
            _uiFontScaleRatio = value
            Configs.uiFontScaleRatio = value
        }

    private var _uiTimeShowMode by mutableStateOf(Configs.UiTimeShowMode.HIDDEN)
    var uiTimeShowMode: Configs.UiTimeShowMode
        get() = _uiTimeShowMode
        set(value) {
            _uiTimeShowMode = value
            Configs.uiTimeShowMode = value
        }

    private var _uiFocusOptimize by mutableStateOf(false)
    var uiFocusOptimize: Boolean
        get() = _uiFocusOptimize
        set(value) {
            _uiFocusOptimize = value
            Configs.uiFocusOptimize = value
        }

    private var _uiScreenAutoCloseDelay by mutableLongStateOf(0)
    var uiScreenAutoCloseDelay: Long
        get() = _uiScreenAutoCloseDelay
        set(value) {
            _uiScreenAutoCloseDelay = value
            Configs.uiScreenAutoCloseDelay = value
        }

    private var _updateForceRemind by mutableStateOf(false)
    var updateForceRemind: Boolean
        get() = _updateForceRemind
        set(value) {
            _updateForceRemind = value
            Configs.updateForceRemind = value
        }

    private var _updateChannel by mutableStateOf("")
    var updateChannel: String
        get() = _updateChannel
        set(value) {
            _updateChannel = value
            Configs.updateChannel = value
        }

    private var _videoPlayerUserAgent by mutableStateOf("")
    var videoPlayerUserAgent: String
        get() = _videoPlayerUserAgent
        set(value) {
            _videoPlayerUserAgent = value
            Configs.videoPlayerUserAgent = value
        }

    private var _videoPlayerHeaders by mutableStateOf("")
    var videoPlayerHeaders: String
        get() = _videoPlayerHeaders
        set(value) {
            _videoPlayerHeaders = value
        }

    private var _videoPlayerLoadTimeout by mutableLongStateOf(0)
    var videoPlayerLoadTimeout: Long
        get() = _videoPlayerLoadTimeout
        set(value) {
            _videoPlayerLoadTimeout = value
            Configs.videoPlayerLoadTimeout = value
        }

    private var _videoPlayerAspectRatio by mutableStateOf(VideoPlayerDisplayMode.ORIGINAL)
    var videoPlayerDisplayMode: VideoPlayerDisplayMode
        get() = _videoPlayerAspectRatio
        set(value) {
            _videoPlayerAspectRatio = value
            Configs.videoPlayerDisplayMode = value
        }

    init {
        try {
            refresh()
        } catch (_: Exception) {
        }

        // 删除过期的预约
        _epgChannelReserveList = EpgProgrammeReserveList(
            _epgChannelReserveList.filter {
                System.currentTimeMillis() < it.startAt + 60 * 1000
            }
        )
    }

    fun refresh() {
        _appBootLaunch = Configs.appBootLaunch
        _appLastLatestVersion = Configs.appLastLatestVersion
        _appAgreementAgreed = Configs.appAgreementAgreed
        _debugShowFps = Configs.debugShowFps
        _debugShowVideoPlayerMetadata = Configs.debugShowVideoPlayerMetadata
        _debugShowLayoutGrids = Configs.debugShowLayoutGrids
        _iptvLastChannelIdx = Configs.iptvLastChannelIdx
        _iptvChannelChangeFlip = Configs.iptvChannelChangeFlip
        _iptvSourceCacheTime = Configs.iptvSourceCacheTime
        _iptvSourceCurrent = Configs.iptvSourceCurrent
        _iptvSourceList = Configs.iptvSourceList
        _iptvPlayableHostList = Configs.iptvPlayableHostList
        _iptvChannelNoSelectEnable = Configs.iptvChannelNoSelectEnable
        _iptvChannelFavoriteEnable = Configs.iptvChannelFavoriteEnable
        _iptvChannelFavoriteListVisible = Configs.iptvChannelFavoriteListVisible
        _iptvChannelFavoriteList = Configs.iptvChannelFavoriteList
        _iptvChannelFavoriteChangeBoundaryJumpOut = Configs.iptvChannelFavoriteChangeBoundaryJumpOut
        _iptvChannelGroupHiddenList = Configs.iptvChannelGroupHiddenList
        _iptvHybridMode = Configs.iptvHybridMode
        _iptvSimilarChannelMerge = Configs.iptvSimilarChannelMerge
        _epgEnable = Configs.epgEnable
        _epgSourceCurrent = Configs.epgSourceCurrent
        _epgSourceList = Configs.epgSourceList
        _epgRefreshTimeThreshold = Configs.epgRefreshTimeThreshold
        _epgChannelReserveList = Configs.epgChannelReserveList
        _uiShowEpgProgrammeProgress = Configs.uiShowEpgProgrammeProgress
        _uiShowEpgProgrammePermanentProgress = Configs.uiShowEpgProgrammePermanentProgress
        _uiShowChannelLogo = Configs.uiShowChannelLogo
        _uiShowChannelPreview = Configs.uiShowChannelPreview
        _uiUseClassicPanelScreen = Configs.uiUseClassicPanelScreen
        _uiDensityScaleRatio = Configs.uiDensityScaleRatio
        _uiFontScaleRatio = Configs.uiFontScaleRatio
        _uiTimeShowMode = Configs.uiTimeShowMode
        _uiFocusOptimize = Configs.uiFocusOptimize
        _uiScreenAutoCloseDelay = Configs.uiScreenAutoCloseDelay
        _updateForceRemind = Configs.updateForceRemind
        _updateChannel = Configs.updateChannel
        _videoPlayerUserAgent = Configs.videoPlayerUserAgent
        _videoPlayerHeaders = Configs.videoPlayerHeaders
        _videoPlayerLoadTimeout = Configs.videoPlayerLoadTimeout
        _videoPlayerAspectRatio = Configs.videoPlayerDisplayMode
    }
}