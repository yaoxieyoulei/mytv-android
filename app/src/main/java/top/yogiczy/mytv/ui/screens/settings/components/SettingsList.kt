package top.yogiczy.mytv.ui.screens.settings.components

import android.content.pm.PackageInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyRow
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.SP

@Composable
fun SettingsList(
    modifier: Modifier = Modifier,
    settingsState: SettingsState = rememberSettingsState(),
    updateState: UpdateState = rememberUpdateState(),
) {
    val childPadding = rememberChildPadding()

    TvLazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = childPadding.start,
            end = childPadding.end,
            bottom = childPadding.bottom,
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { SettingsUpdateItem(updateState = updateState) }

        item {
            SettingsItem(
                title = "开机自启",
                value = if (settingsState.appBootLaunch) "启用" else "禁用",
                description = "下次重启设备生效",
                onClick = { settingsState.appBootLaunch = !settingsState.appBootLaunch },
            )
        }

        item {
            SettingsItem(
                title = "换台反转",
                value = if (settingsState.iptvChannelChangeFlip) "反转" else "正常",
                description = if (settingsState.iptvChannelChangeFlip) "方向键上：下一个频道\n方向键下：上一个频道"
                else "方向键上：上一个频道\n方向键下：下一个频道",
                onClick = {
                    settingsState.iptvChannelChangeFlip = !settingsState.iptvChannelChangeFlip
                },
            )
        }

        item {
            SettingsItem(
                title = "直播源精简",
                value = if (settingsState.iptvSourceSimplify) "启用" else "禁用",
                description = if (settingsState.iptvSourceSimplify) "显示精简直播源(仅央视、地方卫视)" else "显示完整直播源",
                onClick = { settingsState.iptvSourceSimplify = !settingsState.iptvSourceSimplify },
            )
        }

        item { SettingsCustomIptvItem(settingsState = settingsState) }

        item {
            fun formatDuration(ms: Long): String {
                return when (ms) {
                    in 0..<60_000 -> "${ms / 1000}秒"
                    in 60_000..<3_600_000 -> "${ms / 60_000}分钟"
                    else -> "${ms / 3_600_000}小时"
                }
            }

            SettingsItem(
                title = "直播源缓存",
                value = formatDuration(settingsState.iptvSourceCacheTime),
                description = if (settingsState.iptvSourceCachedAt > 0) "已缓存(点击清除缓存)" else "未缓存",
                onClick = { settingsState.iptvSourceCachedAt = 0 },
            )
        }

        item {
            SettingsItem(
                title = "节目单",
                value = if (settingsState.epgEnable) "启用" else "禁用",
                description = "首次加载时可能会有跳帧风险",
                onClick = { settingsState.epgEnable = !settingsState.epgEnable },
            )
        }

        item { SettingsCustomEpgItem(settingsState = settingsState) }

        item {
            SettingsItem(
                title = "节目单缓存",
                value = "当天",
                description = if (settingsState.epgXmlCachedAt > 0) "已缓存(点击清除缓存)" else "未缓存",
                onClick = {
                    settingsState.epgXmlCachedAt = 0
                    settingsState.epgCachedHash = 0
                },
            )
        }

        item { SettingsUIItem(settingsState = settingsState) }
        item { SettingsDebugItem(settingsState = settingsState) }
        item { SettingsMoreItem() }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsListPreview() {
    MyTVTheme {
        SettingsList(
            modifier = Modifier.padding(20.dp),
            settingsState = SettingsState(),
            updateState = UpdateState(
                context = LocalContext.current,
                packageInfo = PackageInfo(),
                coroutineScope = rememberCoroutineScope(),
            ),
        )
    }
}

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

    return state
}