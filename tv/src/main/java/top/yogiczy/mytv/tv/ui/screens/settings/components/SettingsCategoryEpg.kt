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
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSourceList
import top.yogiczy.mytv.core.data.repositories.epg.EpgRepository
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.tv.ui.material.LocalPopupManager
import top.yogiczy.mytv.tv.ui.material.SimplePopup
import top.yogiczy.mytv.tv.ui.material.Snackbar
import top.yogiczy.mytv.tv.ui.screens.epgsource.EpgSourceRefreshTimeScreen
import top.yogiczy.mytv.tv.ui.screens.epgsource.EpgSourceScreen
import top.yogiczy.mytv.tv.ui.screens.settings.SettingsViewModel
import top.yogiczy.mytv.tv.ui.utils.Configs

@Composable
fun SettingsCategoryEpg(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    val coroutineScope = rememberCoroutineScope()

    SettingsContentList(modifier) {
        item {
            SettingsListItem(
                headlineContent = "节目单启用",
                supportingContent = "首次加载时可能会较为缓慢",
                trailingContent = {
                    Switch(checked = settingsViewModel.epgEnable, onCheckedChange = null)
                },
                onSelected = {
                    settingsViewModel.epgEnable = !settingsViewModel.epgEnable
                },
            )
        }

        item {
            val popupManager = LocalPopupManager.current
            val focusRequester = remember { FocusRequester() }
            var visible by remember { mutableStateOf(false) }

            SettingsListItem(
                modifier = Modifier.focusRequester(focusRequester),
                headlineContent = "节目单刷新时间阈值",
                supportingContent = "时间不到${settingsViewModel.epgRefreshTimeThreshold}:00节目单将不会刷新",
                trailingContent = "${settingsViewModel.epgRefreshTimeThreshold}:00",
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
                EpgSourceRefreshTimeScreen(
                    currentRefreshHourProvider = { settingsViewModel.epgRefreshTimeThreshold },
                    onRefreshHourSelected = {
                        settingsViewModel.epgRefreshTimeThreshold = it
                        visible = false
                    },
                    onClose = { visible = false },
                )
            }
        }

        item {
            val popupManager = LocalPopupManager.current
            val focusRequester = remember { FocusRequester() }
            val currentEpgSource =
                settingsViewModel.epgSourceList.let { Constants.EPG_SOURCE_LIST + it }
                    .firstOrNull { it.url == settingsViewModel.epgXmlUrl }
            var isEpgSourceScreenVisible by remember { mutableStateOf(false) }

            SettingsListItem(
                modifier = Modifier.focusRequester(focusRequester),
                headlineContent = "自定义节目单",
                trailingContent = currentEpgSource?.name ?: "未知",
                onSelected = {
                    popupManager.push(focusRequester, true)
                    isEpgSourceScreenVisible = true
                },
                remoteConfig = true,
            )

            SimplePopup(
                visibleProvider = { isEpgSourceScreenVisible },
                onDismissRequest = { isEpgSourceScreenVisible = false },
            ) {
                EpgSourceScreen(
                    epgSourceListProvider = {
                        settingsViewModel.epgSourceList = Configs.epgSourceList
                        settingsViewModel.epgSourceList
                    },
                    currentEpgXmlUrlProvider = { settingsViewModel.epgXmlUrl },
                    onEpgSourceSelected = {
                        isEpgSourceScreenVisible = false
                        if (settingsViewModel.epgXmlUrl != it.url) {
                            settingsViewModel.epgXmlUrl = it.url
                            coroutineScope.launch {
                                EpgRepository(settingsViewModel.epgXmlUrl).clearCache()
                            }
                        }
                    },
                    onEpgSourceDeleted = {
                        settingsViewModel.epgSourceList =
                            EpgSourceList(settingsViewModel.epgSourceList - it)
                    },
                )
            }
        }

        item {
            SettingsListItem(
                headlineContent = "清除缓存",
                supportingContent = "短按清除节目单缓存文件",
                onSelected = {
                    coroutineScope.launch {
                        EpgRepository(settingsViewModel.epgXmlUrl).clearCache()
                        Snackbar.show("缓存已清除，请重启应用")
                    }
                },
            )
        }
    }
}
