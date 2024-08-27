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
fun SettingsUpdateScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
    toUpdateChannelScreen: () -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()

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
                val channel = settingsViewModel.updateChannel

                SettingsListItem(
                    headlineContent = "更新通道",
                    trailingContent = when (channel) {
                        "stable" -> "稳定版"
                        "beta" -> "测试版"
                        "dev" -> "开发版"
                        else -> channel
                    },
                    onSelected = toUpdateChannelScreen,
                    link = true,
                )
            }

            item {
                val forceRemind = settingsViewModel.updateForceRemind

                SettingsListItem(
                    headlineContent = "更新强提醒",
                    supportingContent = if (forceRemind) "检测到新版本时会全屏提醒"
                    else "检测到新版本时仅消息提示",
                    trailingContent = {
                        Switch(forceRemind, null)
                    },
                    onSelected = {
                        settingsViewModel.updateForceRemind = !forceRemind
                    },
                )
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsUpdateScreenPreview() {
    MyTvTheme {
        SettingsUpdateScreen()
    }
}