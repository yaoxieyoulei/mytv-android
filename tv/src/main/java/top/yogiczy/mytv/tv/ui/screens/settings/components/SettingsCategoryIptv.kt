package top.yogiczy.mytv.tv.ui.screens.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Switch
import kotlinx.coroutines.launch
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSourceList
import top.yogiczy.mytv.core.data.repositories.iptv.IptvRepository
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.core.util.utils.humanizeMs
import top.yogiczy.mytv.tv.ui.material.LocalPopupManager
import top.yogiczy.mytv.tv.ui.material.SimplePopup
import top.yogiczy.mytv.tv.ui.material.Snackbar
import top.yogiczy.mytv.tv.ui.screens.iptvsource.IptvSourceCacheTimeScreen
import top.yogiczy.mytv.tv.ui.screens.iptvsource.IptvSourceScreen
import top.yogiczy.mytv.tv.ui.screens.settings.SettingsViewModel
import top.yogiczy.mytv.tv.ui.utils.Configs

@Composable
fun SettingsCategoryIptv(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    val coroutineScope = rememberCoroutineScope()

    SettingsContentList(modifier) {
        item {
            SettingsListItem(
                headlineContent = "数字选台",
                supportingContent = "通过数字选择频道",
                trailingContent = {
                    Switch(
                        checked = settingsViewModel.iptvChannelNoSelectEnable,
                        onCheckedChange = null
                    )
                },
                onSelected = {
                    settingsViewModel.iptvChannelNoSelectEnable =
                        !settingsViewModel.iptvChannelNoSelectEnable
                },
            )
        }

        item {
            SettingsListItem(
                headlineContent = "换台反转",
                supportingContent = if (settingsViewModel.iptvChannelChangeFlip) "方向键上：下一个频道；方向键下：上一个频道"
                else "方向键上：上一个频道；方向键下：下一个频道",
                trailingContent = {
                    Switch(
                        checked = settingsViewModel.iptvChannelChangeFlip, onCheckedChange = null
                    )
                },
                onSelected = {
                    settingsViewModel.iptvChannelChangeFlip =
                        !settingsViewModel.iptvChannelChangeFlip
                },
            )
        }

        item {
            SettingsListItem(
                headlineContent = "直播源精简",
                supportingContent = if (settingsViewModel.iptvSourceSimplify) "显示精简直播源(仅央视、地方卫视)" else "显示完整直播源",
                trailingContent = {
                    Switch(checked = settingsViewModel.iptvSourceSimplify, onCheckedChange = null)
                },
                onSelected = {
                    settingsViewModel.iptvSourceSimplify = !settingsViewModel.iptvSourceSimplify
                },
            )
        }

        item {
            val popupManager = LocalPopupManager.current
            val focusRequester = remember { FocusRequester() }
            var visible by remember { mutableStateOf(false) }

            SettingsListItem(
                modifier = Modifier.focusRequester(focusRequester),
                headlineContent = "直播源缓存时间",
                trailingContent = when (settingsViewModel.iptvSourceCacheTime) {
                    0L -> "不缓存"
                    Long.MAX_VALUE -> "永久"
                    else -> settingsViewModel.iptvSourceCacheTime.humanizeMs()
                },
                onSelected = {
                    popupManager.push(focusRequester, true)
                    visible = true
                },
                remoteConfig = true,
            )

            SimplePopup(
                visibleProvider = { visible },
                onDismissRequest = { visible = false },
            ) {
                IptvSourceCacheTimeScreen(
                    currentCacheTimeProvider = { settingsViewModel.iptvSourceCacheTime },
                    onCacheTimeSelected = {
                        settingsViewModel.iptvSourceCacheTime = it
                        visible = false
                    },
                    onClose = { visible = false },
                )
            }
        }

        item {
            val popupManager = LocalPopupManager.current
            val focusRequester = remember { FocusRequester() }
            val currentIptvSource =
                settingsViewModel.iptvSourceList.let { Constants.IPTV_SOURCE_LIST + it }
                    .firstOrNull { it.url == settingsViewModel.iptvSourceUrl }
            var isIptvSourceScreenVisible by remember { mutableStateOf(false) }

            SettingsListItem(
                modifier = Modifier.focusRequester(focusRequester),
                headlineContent = "自定义直播源",
                trailingContent = currentIptvSource?.name ?: "未知",
                onSelected = {
                    popupManager.push(focusRequester, true)
                    isIptvSourceScreenVisible = true
                },
                remoteConfig = true,
            )

            SimplePopup(
                visibleProvider = { isIptvSourceScreenVisible },
                onDismissRequest = { isIptvSourceScreenVisible = false },
            ) {
                IptvSourceScreen(
                    iptvSourceListProvider = {
                        settingsViewModel.iptvSourceList = Configs.iptvSourceList
                        settingsViewModel.iptvSourceList
                    },
                    currentIptvSourceUrlProvider = { settingsViewModel.iptvSourceUrl },
                    onIptvSourceSelected = {
                        isIptvSourceScreenVisible = false
                        if (settingsViewModel.iptvSourceUrl != it.url) {
                            settingsViewModel.iptvSourceUrl = it.url
                            settingsViewModel.iptvLastChannelIdx = 0
                            coroutineScope.launch {
                                IptvRepository(settingsViewModel.iptvSourceUrl).clearCache()
                            }
                        }
                    },
                    onIptvSourceDeleted = {
                        settingsViewModel.iptvSourceList =
                            IptvSourceList(settingsViewModel.iptvSourceList - it)
                    },
                )
            }
        }

        item {
            SettingsListItem(
                headlineContent = "清除缓存",
                supportingContent = "短按清除直播源缓存文件、可播放域名列表",
                onSelected = {
                    settingsViewModel.iptvPlayableHostList = emptySet()
                    coroutineScope.launch {
                        IptvRepository(settingsViewModel.iptvSourceUrl).clearCache()
                        Snackbar.show("缓存已清除，请重启应用")
                    }
                },
            )
        }

        item {
            SettingsListItem(
                headlineContent = "混合模式",
                supportingContent = when (settingsViewModel.iptvHybridMode) {
                    Configs.IptvHybridMode.DISABLE -> ""
                    Configs.IptvHybridMode.IPTV_FIRST -> "优先尝试播放直播源中线路，若所有直播源线路不可用，则进入混合模式"
                    Configs.IptvHybridMode.HYBRID_FIRST -> "优先进入混合模式，若混合模式不可用，则播放直播源中线路"
                },
                trailingContent = when (settingsViewModel.iptvHybridMode) {
                    Configs.IptvHybridMode.DISABLE -> "禁用"
                    Configs.IptvHybridMode.IPTV_FIRST -> "直播源优先"
                    Configs.IptvHybridMode.HYBRID_FIRST -> "混合优先"
                },
                onSelected = {
                    settingsViewModel.iptvHybridMode =
                        Configs.IptvHybridMode.entries.let { it[(it.indexOf(settingsViewModel.iptvHybridMode) + 1) % it.size] }
                },
            )
        }
    }
}