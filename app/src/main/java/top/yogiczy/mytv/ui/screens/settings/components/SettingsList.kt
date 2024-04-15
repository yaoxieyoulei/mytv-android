package top.yogiczy.mytv.ui.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
) {
    val childPadding = rememberChildPadding()

    var appBootLaunch by remember { mutableStateOf(SP.appBootLaunch) }
    var debugShowFps by remember { mutableStateOf(SP.debugShowFps) }
    var iptvChannelChangeFlip by remember { mutableStateOf(SP.iptvChannelChangeFlip) }
    var iptvSourceSimplify by remember { mutableStateOf(SP.iptvSourceSimplify) }
    var iptvCustomSource by remember { mutableStateOf(SP.iptvCustomSource) }
    var iptvSourceCacheTime by remember { mutableLongStateOf(SP.iptvSourceCacheTime) }
    var epgEnable by remember { mutableStateOf(SP.epgEnable) }
    var epgXmlCacheTime by remember { mutableLongStateOf(SP.epgXmlCacheTime) }
    var epgCacheHash by remember { mutableIntStateOf(SP.epgCacheHash) }

    DisposableEffect(Unit) {
        onDispose {
            SP.appBootLaunch = appBootLaunch
            SP.debugShowFps = debugShowFps
            SP.iptvChannelChangeFlip = iptvChannelChangeFlip
            SP.iptvSourceSimplify = iptvSourceSimplify
            SP.iptvCustomSource = iptvCustomSource
            SP.iptvSourceCacheTime = iptvSourceCacheTime
            SP.epgEnable = epgEnable
            SP.epgXmlCacheTime = epgXmlCacheTime
            SP.epgCacheHash = epgCacheHash
        }
    }
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
                title = "应用更新",
                value = "无更新",
                description = "最新版本：v1.0.0",
                onClick = { },
            )
        }

        item {
            SettingsItem(
                title = "开机自启",
                value = if (appBootLaunch) "启用" else "禁用",
                description = "下次重启设备生效",
                onClick = { appBootLaunch = !appBootLaunch },
            )
        }

        item {
            SettingsItem(
                title = "换台反转",
                value = if (iptvChannelChangeFlip) "反转" else "正常",
                description = if (iptvChannelChangeFlip) "方向键上：下一个频道\n方向键下：上一个频道"
                else "方向键上：上一个频道\n方向键下：下一个频道",
                onClick = { iptvChannelChangeFlip = !iptvChannelChangeFlip },
            )
        }

        item {
            SettingsItem(
                title = "直播源精简",
                value = if (iptvSourceSimplify) "启用" else "禁用",
                description = if (iptvSourceSimplify) "显示精简直播源(仅央视、地方卫视)" else "显示完整直播源",
                onClick = { iptvSourceSimplify = !iptvSourceSimplify },
            )
        }

        // item {
        //     SettingsItem(
        //         title = "自定义直播源",
        //         value = if (iptvCustomSource.isNotBlank()) "已启用" else "未启用",
        //         description = "访问以下网址进行配置：",
        //         onClick = { },
        //     )
        // }

        item {
            SettingsItem(
                title = "直播源缓存",
                value = "24小时",
                description = if (iptvSourceCacheTime > 0) "已缓存(点击清除缓存)" else "未缓存",
                onClick = { iptvSourceCacheTime = 0 },
            )
        }

        item {
            SettingsItem(
                title = "节目单",
                value = if (epgEnable) "启用" else "禁用",
                description = "首次加载时可能会有跳帧风险",
                onClick = { epgEnable = !epgEnable },
            )
        }

        item {
            SettingsItem(
                title = "节目单缓存",
                value = "当天",
                description = if (epgXmlCacheTime > 0) "已缓存(点击清除缓存)" else "未缓存",
                onClick = {
                    epgXmlCacheTime = 0
                    epgCacheHash = 0
                },
            )
        }

        item {
            SettingsItem(
                title = "显示FPS",
                value = if (debugShowFps) "启用" else "禁用",
                description = "显示当前帧率",
                onClick = { debugShowFps = !debugShowFps },
            )
        }

        // item {
        //     SettingsItem(
        //         title = "日志",
        //         value = "禁用",
        //         description = "点击查看日志",
        //         onClick = { },
        //     )
        // }
    }
}

@Preview
@Composable
private fun SettingsListPreview() {
    SP.init(LocalContext.current)
    MyTVTheme {
        SettingsList()
    }
}