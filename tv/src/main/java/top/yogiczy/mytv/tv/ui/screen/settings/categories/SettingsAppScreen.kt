package top.yogiczy.mytv.tv.ui.screen.settings.categories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Switch
import androidx.tv.material3.Text
import kotlinx.coroutines.launch
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSource
import top.yogiczy.mytv.core.data.repositories.epg.EpgRepository
import top.yogiczy.mytv.core.data.repositories.iptv.IptvRepository
import top.yogiczy.mytv.core.data.utils.SP
import top.yogiczy.mytv.tv.ui.material.Snackbar
import top.yogiczy.mytv.tv.ui.screen.settings.SettingsViewModel
import top.yogiczy.mytv.tv.ui.screen.settings.components.SettingsCategoryScreen
import top.yogiczy.mytv.tv.ui.screen.settings.components.SettingsListItem
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme

@Composable
fun SettingsAppScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
    onReload: () -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    SettingsCategoryScreen(
        modifier = modifier,
        header = { Text("设置 / 应用") },
        onBackPressed = onBackPressed,
    ) { firstItemFocusRequester ->
        item {
            SettingsListItem(
                modifier = Modifier.focusRequester(firstItemFocusRequester),
                headlineContent = "开机自启",
                supportingContent = "请确保当前设备支持该功能",
                trailingContent = {
                    Switch(settingsViewModel.appBootLaunch, null)
                },
                onSelect = {
                    settingsViewModel.appBootLaunch = !settingsViewModel.appBootLaunch
                },
            )
        }

        item {
            SettingsListItem(
                headlineContent = "画中画",
                trailingContent = {
                    Switch(settingsViewModel.appPipEnable, null)
                },
                onSelect = {
                    settingsViewModel.appPipEnable = !settingsViewModel.appPipEnable
                },
            )
        }

        item {
            SettingsListItem(
                headlineContent = "清除缓存",
                onSelect = {
                    settingsViewModel.iptvPlayableHostList = emptySet()
                    coroutineScope.launch {
                        IptvRepository(settingsViewModel.iptvSourceCurrent).getEpgUrl()?.let {
                            EpgRepository(EpgSource(url = it)).clearCache()
                        }
                        IptvRepository(settingsViewModel.iptvSourceCurrent).clearCache()
                        EpgRepository(settingsViewModel.epgSourceCurrent).clearCache()
                    }
                    Snackbar.show("缓存已清除")
                    onReload()
                },
            )
        }

        item {
            SettingsListItem(
                headlineContent = "恢复初始化",
                onSelect = {
                    SP.clear()
                    Snackbar.show("已恢复初始化")
                    onReload()
                },
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsSystemScreenPreview() {
    MyTvTheme {
        SettingsAppScreen()
    }
}