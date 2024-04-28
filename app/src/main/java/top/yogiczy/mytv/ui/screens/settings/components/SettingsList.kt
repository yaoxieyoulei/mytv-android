package top.yogiczy.mytv.ui.screens.settings.components

import android.content.pm.PackageInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyRow
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.settings.SettingsState
import top.yogiczy.mytv.ui.screens.settings.rememberSettingsState
import top.yogiczy.mytv.ui.theme.MyTVTheme

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
        item {
            SettingsItem(
                modifier = modifier,
                title = "应用更新",
                value = if (updateState.isUpdateAvailable) "新版本" else "无更新",
                description = "最新版本：${updateState.latestRelease.tagName}",
                onClick = {
                    if (updateState.isUpdateAvailable) {
                        updateState.showDialog = true
                    }
                },
            )
        }

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
        item { SettingsUpdateItem(settingsState = settingsState) }
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
