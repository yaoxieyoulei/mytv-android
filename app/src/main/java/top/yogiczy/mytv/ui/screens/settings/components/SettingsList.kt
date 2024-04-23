package top.yogiczy.mytv.ui.screens.settings.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
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
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.tvmaterial.StandardDialog
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.theme.MyTVTheme
import top.yogiczy.mytv.ui.utils.HttpServer
import top.yogiczy.mytv.ui.utils.SP
import top.yogiczy.mytv.ui.utils.handleDPadKeyEvents

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalTvMaterial3Api::class
)
@Composable
fun SettingsList(
    modifier: Modifier = Modifier,
    updateState: UpdateState = rememberUpdateState(),
) {
    val childPadding = rememberChildPadding()

    var appBootLaunch by remember { mutableStateOf(SP.appBootLaunch) }
    LaunchedEffect(appBootLaunch) { SP.appBootLaunch = appBootLaunch }

    var iptvChannelChangeFlip by remember { mutableStateOf(SP.iptvChannelChangeFlip) }
    LaunchedEffect(iptvChannelChangeFlip) { SP.iptvChannelChangeFlip = iptvChannelChangeFlip }

    var iptvSourceSimplify by remember { mutableStateOf(SP.iptvSourceSimplify) }
    LaunchedEffect(iptvSourceSimplify) { SP.iptvSourceSimplify = iptvSourceSimplify }

    var iptvSourceCachedAt by remember { mutableLongStateOf(SP.iptvSourceCachedAt) }
    LaunchedEffect(iptvSourceCachedAt) { SP.iptvSourceCachedAt = iptvSourceCachedAt }

    val iptvSourceCacheTime by remember { mutableLongStateOf(SP.iptvSourceCacheTime) }
    LaunchedEffect(iptvSourceCacheTime) { SP.iptvSourceCacheTime = iptvSourceCacheTime }

    var iptvSourceUrl by remember { mutableStateOf(SP.iptvSourceUrl) }
    LaunchedEffect(iptvSourceUrl) { SP.iptvSourceUrl = iptvSourceUrl }

    var iptvSourceUrlHistoryList by remember { mutableStateOf(SP.iptvSourceUrlHistoryList) }
    LaunchedEffect(iptvSourceUrlHistoryList) {
        SP.iptvSourceUrlHistoryList = iptvSourceUrlHistoryList
    }

    var epgEnable by remember { mutableStateOf(SP.epgEnable) }
    LaunchedEffect(epgEnable) { SP.epgEnable = epgEnable }

    var epgXmlCachedAt by remember { mutableLongStateOf(SP.epgXmlCachedAt) }
    LaunchedEffect(epgXmlCachedAt) { SP.epgXmlCachedAt = epgXmlCachedAt }

    var epgCachedHash by remember { mutableIntStateOf(SP.epgCachedHash) }
    LaunchedEffect(epgCachedHash) { SP.epgCachedHash = epgCachedHash }

    var epgXmlUrl by remember { mutableStateOf(SP.epgXmlUrl) }
    LaunchedEffect(epgXmlUrl) { SP.epgXmlUrl = epgXmlUrl }

    var epgXmlUrlHistoryList by remember { mutableStateOf(SP.epgXmlUrlHistoryList) }
    LaunchedEffect(epgXmlUrlHistoryList) { SP.epgXmlUrlHistoryList = epgXmlUrlHistoryList }

    var showServerQrcode by remember { mutableStateOf(false) }
    val serverUrl = "http://${HttpServer.getLocalIpAddress()}:${HttpServer.SERVER_PORT}"
    if (showServerQrcode) {
        SettingsQrcodeDialog(
            onDismissRequest = { showServerQrcode = false },
            data = serverUrl,
        )
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

            // PlacedFocusNode()
            SettingsItem(
                title = "应用更新",
                value = if (updateState.isUpdateAvailable) "新版本" else "无更新",
                description = "最新版本：${updateState.latestRelease.tagName}" + if (updateState.isUpdateAvailable) "（长按更新）" else "",
                onClick = {
                    showDialog = true
                },
                onLongClick = {
                    coroutineScope.launch {
                        updateState.downloadAndUpdate()
                    }
                },
            )

            SettingsUpdateInfoDialog(
                showDialog = showDialog,
                onDismissRequest = { showDialog = false },
                release = updateState.latestRelease,
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

        item {
            var showDialog by remember { mutableStateOf(false) }

            SettingsItem(
                title = "自定义直播源",
                value = if (iptvSourceUrl != Constants.IPTV_SOURCE_URL) "已启用" else "未启用",
                description = if (iptvSourceUrl != Constants.IPTV_SOURCE_URL) "长按查看历史直播源" else "点击查看网址二维码",
                onClick = { showServerQrcode = true },
                onLongClick = { showDialog = true },
            )

            StandardDialog(
                showDialog = showDialog,
                onDismissRequest = { showDialog = false },
                containerColor = MaterialTheme.colorScheme.background,
                confirmButton = { Text(text = "点按切换；长按删除历史记录") },
                title = {
                    Text(
                        text = "历史直播源",
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                text = {
                    SettingsIptvSourceHistory(
                        modifier = Modifier.padding(vertical = 6.dp),
                        iptvSourceList = iptvSourceUrlHistoryList.toList(),
                        currentIptvSource = iptvSourceUrl,
                        onSelected = {
                            if (iptvSourceUrl != it) {
                                iptvSourceCachedAt = 0
                                iptvSourceUrl = it
                            }
                            showDialog = false
                        },
                        onDeleted = {
                            if (it != Constants.IPTV_SOURCE_URL) {
                                iptvSourceUrlHistoryList -= it
                            }
                        },
                    )
                },
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
            var showDialog by remember { mutableStateOf(false) }

            SettingsItem(
                title = "自定义节目单",
                value = if (epgXmlUrl != Constants.EPG_XML_URL) "已启用" else "未启用",
                description = if (epgXmlUrl != Constants.EPG_XML_URL) "长按查看历史节目单" else "点击查看网址二维码",
                onClick = { showServerQrcode = true },
                onLongClick = { showDialog = true },
            )

            StandardDialog(
                showDialog = showDialog,
                onDismissRequest = { showDialog = false },
                containerColor = MaterialTheme.colorScheme.background,
                confirmButton = { Text(text = "点按切换；长按删除历史记录") },
                title = {
                    Text(
                        text = "历史节目单",
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                text = {
                    SettingsEpgHistory(
                        modifier = Modifier.padding(vertical = 6.dp),
                        epgList = epgXmlUrlHistoryList.toList(),
                        currentEpg = epgXmlUrl,
                        onSelected = {
                            if (epgXmlUrl != it) {
                                epgXmlCachedAt = 0
                                epgCachedHash = 0
                                epgXmlUrl = it
                            }
                            showDialog = false
                        },
                        onDeleted = {
                            if (it != Constants.EPG_XML_URL) {
                                epgXmlUrlHistoryList -= it
                            }
                        },
                    )
                },
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
            SettingsItem(
                title = "更多设置",
                value = "",
                description = "访问以下网址进行配置：$serverUrl",
                onClick = { showServerQrcode = true },
            )
        }
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
                    .width(200.dp)
                    .height(200.dp)
                    .background(
                        MaterialTheme.colorScheme.onBackground,
                        MaterialTheme.shapes.medium,
                    )
                    .padding(16.dp),
            ) {
                Image(
                    modifier = modifier.fillMaxSize(),
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

@OptIn(
    ExperimentalTvMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
private fun SettingsUpdateInfoDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean = false,
    release: GithubRelease = GithubRelease(),
    onDismissRequest: () -> Unit = {},
) {
    StandardDialog(
        modifier = modifier,
        showDialog = showDialog,
        onDismissRequest = onDismissRequest,
        confirmButton = { },
        containerColor = MaterialTheme.colorScheme.background,
        title = {
            Text(
                text = release.tagName,
                color = MaterialTheme.colorScheme.onBackground,
            )
        },
        text = {
            TvLazyColumn {
                item {
                    Text(
                        text = release.description,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        },
    )
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsUpdateInfoDialogPreview() {
    MyTVTheme {
        SettingsUpdateInfoDialog(
            showDialog = true
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsIptvSourceHistory(
    modifier: Modifier = Modifier,
    iptvSourceList: List<String> = emptyList(),
    currentIptvSource: String = "",
    onSelected: (String) -> Unit = {},
    onDeleted: (String) -> Unit = {},
) {
    TvLazyColumn(modifier = modifier) {
        items(iptvSourceList) { source ->
            var isFocused by remember { mutableStateOf(false) }

            ListItem(
                modifier = modifier
                    .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
                    .handleDPadKeyEvents(
                        onSelect = { onSelected(source) },
                        onLongSelect = { onDeleted(source) },
                    ),
                selected = source == currentIptvSource,
                onClick = { },
                headlineContent = {
                    Text(
                        text = if (source == Constants.IPTV_SOURCE_URL) "默认直播源（网络需要支持ipv6）" else source,
                        modifier = Modifier.fillMaxWidth(),
                        color = if (isFocused) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                        maxLines = 2,
                    )
                },
                colors = ListItemDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.onBackground,
                    focusedContentColor = MaterialTheme.colorScheme.background,
                    selectedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                    selectedContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        }
    }
}

@Preview
@Composable
private fun SettingsIptvSourceHistoryPreview() {
    MyTVTheme {
        SettingsIptvSourceHistory(
            iptvSourceList = listOf("默认直播源", "自定义直播源"),
            currentIptvSource = "自定义直播源",
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SettingsEpgHistory(
    modifier: Modifier = Modifier,
    epgList: List<String> = emptyList(),
    currentEpg: String = "",
    onSelected: (String) -> Unit = {},
    onDeleted: (String) -> Unit = {},
) {
    TvLazyColumn(modifier = modifier) {
        items(epgList) { source ->
            var isFocused by remember { mutableStateOf(false) }

            ListItem(
                modifier = modifier
                    .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
                    .handleDPadKeyEvents(
                        onSelect = { onSelected(source) },
                        onLongSelect = { onDeleted(source) },
                    ),
                selected = source == currentEpg,
                onClick = { },
                headlineContent = {
                    Text(
                        text = if (source == Constants.IPTV_SOURCE_URL) "默认节目单" else source,
                        modifier = Modifier.fillMaxWidth(),
                        color = if (isFocused) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
                        maxLines = 2,
                    )
                },
                colors = ListItemDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.onBackground,
                    focusedContentColor = MaterialTheme.colorScheme.background,
                    selectedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                    selectedContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )
        }
    }
}

@Preview
@Composable
private fun SettingsEpgHistoryPreview() {
    MyTVTheme {
        SettingsEpgHistory(
            epgList = listOf("默认节目单", "自定义节目单"),
            currentEpg = "自定义节目单",
        )
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
