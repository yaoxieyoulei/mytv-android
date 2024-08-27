package top.yogiczy.mytv.tv.ui.screen.settings.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Switch
import androidx.tv.material3.Text
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.screen.settings.components.SettingsListItem
import top.yogiczy.mytv.tv.ui.screensold.settings.SettingsViewModel
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme

@Composable
fun SettingsEpgScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
    toEpgSourceScreen: () -> Unit = {},
    toEpgRefreshTimeThresholdScreen: () -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()

    AppScreen(
        modifier = modifier.padding(top = 10.dp),
        header = { Text("设置 / 节目单") },
        canBack = true,
        onBackPressed = onBackPressed,
    ) {
        LazyColumn(
            contentPadding = childPadding.copy(top = 10.dp).paddingValues,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                SettingsListItem(
                    headlineContent = "节目单启用",
                    supportingContent = "首次加载时可能会较为缓慢",
                    trailingContent = { Switch(settingsViewModel.epgEnable, null) },
                    onSelected = { settingsViewModel.epgEnable = !settingsViewModel.epgEnable },
                )
            }

            item {
                val currentEpgSource = settingsViewModel.epgSourceCurrent

                SettingsListItem(
                    headlineContent = "自定义节目单",
                    trailingContent = { Text(currentEpgSource.name) },
                    onSelected = toEpgSourceScreen,
                    link = true,
                )
            }

            item {
                val epgRefreshTimeThreshold = settingsViewModel.epgRefreshTimeThreshold

                SettingsListItem(
                    headlineContent = "节目单刷新时间阈值",
                    trailingContent = { Text("${epgRefreshTimeThreshold}:00") },
                    supportingContent = "时间不到${epgRefreshTimeThreshold}:00节目单将不会刷新",
                    onSelected = toEpgRefreshTimeThresholdScreen,
                    link = true,
                )
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsEpgScreenPreview() {
    MyTvTheme {
        SettingsEpgScreen()
    }
}