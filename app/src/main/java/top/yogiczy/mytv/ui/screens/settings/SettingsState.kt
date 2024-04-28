package top.yogiczy.mytv.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import top.yogiczy.mytv.ui.utils.SP

class SettingsState(
    appBootLaunch: Boolean = false,
    appLastLatestVersion: String = "",

    debugShowFps: Boolean = false,
    debugShowPlayerInfo: Boolean = false,

    iptvLastIptvIdx: Int = 0,
    iptvChannelChangeFlip: Boolean = false,
    iptvSourceSimplify: Boolean = false,
    iptvSourceCachedAt: Long = 0,
    iptvSourceCacheTime: Long = 0,
    iptvSourceUrl: String = "",
    iptvPlayableHostList: Set<String> = emptySet(),
    iptvSourceUrlHistoryList: Set<String> = emptySet(),
    iptvChannelFavoriteList: Set<String> = emptySet(),

    epgEnable: Boolean = false,
    epgXmlCachedAt: Long = 0,
    epgCachedHash: Int = 0,
    epgXmlUrl: String = "",
    epgRefreshTimeThreshold: Int = 0,
    epgXmlUrlHistoryList: Set<String> = emptySet(),

    uiShowEpgProgrammeProgress: Boolean = false,

    updateForceRemind: Boolean = false
) {
    var appBootLaunch by mutableStateOf(appBootLaunch)
    var appLastLatestVersion by mutableStateOf(appLastLatestVersion)

    var debugShowFps by mutableStateOf(debugShowFps)
    var debugShowPlayerInfo by mutableStateOf(debugShowPlayerInfo)

    var iptvLastIptvIdx by mutableIntStateOf(iptvLastIptvIdx)
    var iptvChannelChangeFlip by mutableStateOf(iptvChannelChangeFlip)
    var iptvSourceSimplify by mutableStateOf(iptvSourceSimplify)
    var iptvSourceCachedAt by mutableLongStateOf(iptvSourceCachedAt)
    var iptvSourceCacheTime by mutableLongStateOf(iptvSourceCacheTime)
    var iptvSourceUrl by mutableStateOf(iptvSourceUrl)
    var iptvPlayableHostList by mutableStateOf(iptvPlayableHostList)
    var iptvSourceUrlHistoryList by mutableStateOf(iptvSourceUrlHistoryList)
    var iptvChannelFavoriteList by mutableStateOf(iptvChannelFavoriteList)

    var epgEnable by mutableStateOf(epgEnable)
    var epgXmlCachedAt by mutableLongStateOf(epgXmlCachedAt)
    var epgCachedHash by mutableIntStateOf(epgCachedHash)
    var epgXmlUrl by mutableStateOf(epgXmlUrl)
    var epgRefreshTimeThreshold by mutableIntStateOf(epgRefreshTimeThreshold)
    var epgXmlUrlHistoryList by mutableStateOf(epgXmlUrlHistoryList)

    var uiShowEpgProgrammeProgress by mutableStateOf(uiShowEpgProgrammeProgress)

    var updateForceRemind by mutableStateOf(updateForceRemind)
}

@Composable
fun rememberSettingsState(): SettingsState {
    val state = remember {
        SettingsState(
            appBootLaunch = SP.appBootLaunch,
            appLastLatestVersion = SP.appLastLatestVersion,

            debugShowFps = SP.debugShowFps,
            debugShowPlayerInfo = SP.debugShowPlayerInfo,

            iptvLastIptvIdx = SP.iptvLastIptvIdx,
            iptvChannelChangeFlip = SP.iptvChannelChangeFlip,
            iptvSourceSimplify = SP.iptvSourceSimplify,
            iptvSourceCachedAt = SP.iptvSourceCachedAt,
            iptvSourceCacheTime = SP.iptvSourceCacheTime,
            iptvSourceUrl = SP.iptvSourceUrl,
            iptvPlayableHostList = SP.iptvPlayableHostList,
            iptvSourceUrlHistoryList = SP.iptvSourceUrlHistoryList,
            iptvChannelFavoriteList = SP.iptvChannelFavoriteList,

            epgEnable = SP.epgEnable,
            epgXmlCachedAt = SP.epgXmlCachedAt,
            epgCachedHash = SP.epgCachedHash,
            epgXmlUrl = SP.epgXmlUrl,
            epgRefreshTimeThreshold = SP.epgRefreshTimeThreshold,
            epgXmlUrlHistoryList = SP.epgXmlUrlHistoryList,

            uiShowEpgProgrammeProgress = SP.uiShowEpgProgrammeProgress,

            updateForceRemind = SP.updateForceRemind
        )
    }

    LaunchedEffect(state.appBootLaunch) { SP.appBootLaunch = state.appBootLaunch }
    LaunchedEffect(state.appLastLatestVersion) {
        SP.appLastLatestVersion = state.appLastLatestVersion
    }

    LaunchedEffect(state.debugShowFps) { SP.debugShowFps = state.debugShowFps }
    LaunchedEffect(state.debugShowPlayerInfo) { SP.debugShowPlayerInfo = state.debugShowPlayerInfo }

    LaunchedEffect(state.iptvLastIptvIdx) { SP.iptvLastIptvIdx = state.iptvLastIptvIdx }
    LaunchedEffect(state.iptvChannelChangeFlip) {
        SP.iptvChannelChangeFlip = state.iptvChannelChangeFlip
    }
    LaunchedEffect(state.iptvSourceSimplify) { SP.iptvSourceSimplify = state.iptvSourceSimplify }
    LaunchedEffect(state.iptvSourceCachedAt) { SP.iptvSourceCachedAt = state.iptvSourceCachedAt }
    LaunchedEffect(state.iptvSourceCacheTime) { SP.iptvSourceCacheTime = state.iptvSourceCacheTime }
    LaunchedEffect(state.iptvSourceUrl) { SP.iptvSourceUrl = state.iptvSourceUrl }
    LaunchedEffect(state.iptvPlayableHostList) {
        SP.iptvPlayableHostList = state.iptvPlayableHostList
    }
    LaunchedEffect(state.iptvSourceUrlHistoryList) {
        SP.iptvSourceUrlHistoryList = state.iptvSourceUrlHistoryList
    }
    LaunchedEffect(state.iptvChannelFavoriteList) {
        SP.iptvChannelFavoriteList = state.iptvChannelFavoriteList
    }

    LaunchedEffect(state.epgEnable) { SP.epgEnable = state.epgEnable }
    LaunchedEffect(state.epgXmlCachedAt) { SP.epgXmlCachedAt = state.epgXmlCachedAt }
    LaunchedEffect(state.epgCachedHash) { SP.epgCachedHash = state.epgCachedHash }
    LaunchedEffect(state.epgXmlUrl) { SP.epgXmlUrl = state.epgXmlUrl }
    LaunchedEffect(state.epgRefreshTimeThreshold) {
        SP.epgRefreshTimeThreshold = state.epgRefreshTimeThreshold
    }
    LaunchedEffect(state.epgXmlUrlHistoryList) {
        SP.epgXmlUrlHistoryList = state.epgXmlUrlHistoryList
    }

    LaunchedEffect(state.uiShowEpgProgrammeProgress) {
        SP.uiShowEpgProgrammeProgress = state.uiShowEpgProgrammeProgress
    }

    LaunchedEffect(state.updateForceRemind) { SP.updateForceRemind = state.updateForceRemind }

    return state
}