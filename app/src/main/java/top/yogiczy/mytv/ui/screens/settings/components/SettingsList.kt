package top.yogiczy.mytv.ui.screens.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.QrShapes
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import kotlinx.coroutines.launch
import top.yogiczy.mytv.data.entities.GithubRelease
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.HttpServer
import top.yogiczy.mytv.ui.utils.SP

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsList(
    modifier: Modifier = Modifier,
    updateState: UpdateState = rememberUpdateState(),
) {
    val childPadding = rememberChildPadding()

    var appBootLaunch by remember { mutableStateOf(SP.appBootLaunch) }
    var iptvChannelChangeFlip by remember { mutableStateOf(SP.iptvChannelChangeFlip) }
    var iptvSourceSimplify by remember { mutableStateOf(SP.iptvSourceSimplify) }
    var iptvSourceCachedAt by remember { mutableLongStateOf(SP.iptvSourceCachedAt) }
    val iptvSourceCacheTime by remember { mutableLongStateOf(SP.iptvSourceCacheTime) }
    var epgEnable by remember { mutableStateOf(SP.epgEnable) }
    var epgXmlCachedAt by remember { mutableLongStateOf(SP.epgXmlCachedAt) }
    var epgCachedHash by remember { mutableIntStateOf(SP.epgCachedHash) }

    DisposableEffect(Unit) {
        onDispose {
            SP.appBootLaunch = appBootLaunch
            SP.iptvChannelChangeFlip = iptvChannelChangeFlip
            SP.iptvSourceSimplify = iptvSourceSimplify
            SP.iptvSourceCachedAt = iptvSourceCachedAt
            SP.epgEnable = epgEnable
            SP.epgXmlCachedAt = epgXmlCachedAt
            SP.epgCachedHash = epgCachedHash
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
            val coroutineScope = rememberCoroutineScope()
            var showDialog by remember { mutableStateOf(false) }

            SettingsItem(
                title = "应用更新",
                value = if (updateState.isUpdateAvailable) "新版本" else "无更新",
                description = "最新版本：${updateState.latestRelease.tagName}" + if (updateState.isUpdateAvailable) "（长按更新）" else "",
                onClick = {
                    if (updateState.isUpdateAvailable) {
                        showDialog = true
                    }
                },
                onLongClick = {
                    coroutineScope.launch {
                        updateState.downloadAndUpdate()
                    }
                },
            )

            if (showDialog) {
                SettingsUpdateInfoDialog(
                    onDismissRequest = { showDialog = false },
                    release = updateState.latestRelease,
                )
            }
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
                value = formatDuration(iptvSourceCacheTime),
                description = if (iptvSourceCachedAt > 0) "已缓存(点击清除缓存)" else "未缓存",
                onClick = { iptvSourceCachedAt = 0 },
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
                description = if (epgXmlCachedAt > 0) "已缓存(点击清除缓存)" else "未缓存",
                onClick = {
                    epgXmlCachedAt = 0
                    epgCachedHash = 0
                },
            )
        }

        item {
            val serverUrl = "http://${HttpServer.getLocalIpAddress()}:${HttpServer.SERVER_PORT}"
            var showQrcode by remember { mutableStateOf(false) }

            SettingsItem(
                title = "更多设置",
                value = "",
                description = "访问以下网址进行配置：$serverUrl",
                onClick = {
                    showQrcode = true
                },
            )

            if (showQrcode) {
                SettingsQrcodeDialog(
                    onDismissRequest = { showQrcode = false },
                    data = serverUrl,
                )
            }
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

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsQrcodeDialog(
    modifier: Modifier = Modifier,
    data: String = "",
    onDismissRequest: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        content = {
            Box(
                modifier = modifier
                    .background(Color.White, MaterialTheme.shapes.medium)
                    .padding(16.dp),
            ) {
                Image(
                    painter = rememberQrCodePainter(
                        data = data,
                        shapes = QrShapes(
                            ball = QrBallShape.circle(),
                            darkPixel = QrPixelShape.roundCorners(),
                            frame = QrFrameShape.roundCorners(.25f),
                        ),
                    ),
                    contentDescription = data,
                )
            }
        },
    )
}

@Preview
@Composable
private fun SettingsQrcodeDialogPreview() {
    MyTVTheme {
        SettingsQrcodeDialog(
            data = "data",
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsUpdateInfoDialog(
    modifier: Modifier = Modifier,
    release: GithubRelease = GithubRelease(),
    onDismissRequest: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        content = {
            Column(
                modifier = modifier
                    .background(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.shapes.medium,
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = release.tagName,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(modifier = Modifier.height(10.dp))

                TvLazyColumn(modifier = Modifier.sizeIn(maxHeight = 400.dp)) {
                    item {
                        Text(
                            text = release.description,
                        )
                    }
                }
            }
        },
    )
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsUpdateInfoDialogPreview() {
    MyTVTheme {
        SettingsUpdateInfoDialog()
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
