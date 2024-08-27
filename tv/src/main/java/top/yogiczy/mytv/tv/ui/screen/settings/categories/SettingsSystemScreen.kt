package top.yogiczy.mytv.tv.ui.screen.settings.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Switch
import androidx.tv.material3.Text
import kotlinx.coroutines.launch
import top.yogiczy.mytv.core.data.repositories.epg.EpgRepository
import top.yogiczy.mytv.core.data.repositories.iptv.IptvRepository
import top.yogiczy.mytv.core.data.utils.SP
import top.yogiczy.mytv.tv.ui.material.Snackbar
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.screen.settings.components.SettingsListItem
import top.yogiczy.mytv.tv.ui.screensold.settings.SettingsViewModel
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme

@Composable
fun SettingsSystemScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
    onReload: () -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()
    val coroutineScope = rememberCoroutineScope()

    AppScreen(
        modifier = modifier.padding(top = 10.dp),
        header = { Text("设置 / 系统") },
        canBack = true,
        onBackPressed = onBackPressed,
    ) {
        LazyColumn(
            contentPadding = childPadding.copy(top = 10.dp).paddingValues,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                SettingsListItem(
                    headlineContent = "开机自启",
                    supportingContent = "请确保当前设备支持该功能",
                    trailingContent = {
                        Switch(settingsViewModel.appBootLaunch, null)
                    },
                    onSelected = {
                        settingsViewModel.appBootLaunch = !settingsViewModel.appBootLaunch
                    },
                )
            }

            item {
                SettingsListItem(
                    headlineContent = "清除缓存",
                    onSelected = {
                        settingsViewModel.iptvPlayableHostList = emptySet()
                        coroutineScope.launch {
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
                    onSelected = {
                        SP.clear()
                        Snackbar.show("已恢复初始化")
                        onReload()
                    },
                )
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsSystemScreenPreview() {
    MyTvTheme {
        SettingsSystemScreen()
    }
}