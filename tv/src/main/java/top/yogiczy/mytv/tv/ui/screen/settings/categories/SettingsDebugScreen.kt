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
fun SettingsDebugScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
    onBackPressed: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()

    AppScreen(
        modifier = modifier.padding(top = 10.dp),
        header = { Text("设置 / 调试") },
        canBack = true,
        onBackPressed = onBackPressed,
    ) {
        LazyColumn(
            contentPadding = childPadding.copy(top = 10.dp).paddingValues,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                SettingsListItem(
                    headlineContent = "显示FPS",
                    supportingContent = "在屏幕左上角显示fps和柱状图",
                    trailingContent = {
                        Switch(settingsViewModel.debugShowFps, null)
                    },
                    onSelected = {
                        settingsViewModel.debugShowFps = !settingsViewModel.debugShowFps
                    },
                )
            }

            item {
                SettingsListItem(
                    headlineContent = "显示播放器信息",
                    supportingContent = "显示播放器详细信息（编码、解码器、采样率等）",
                    trailingContent = {
                        Switch(settingsViewModel.debugShowVideoPlayerMetadata, null)
                    },
                    onSelected = {
                        settingsViewModel.debugShowVideoPlayerMetadata =
                            !settingsViewModel.debugShowVideoPlayerMetadata
                    },
                )
            }

            item {
                SettingsListItem(
                    headlineContent = "显示布局网格",
                    trailingContent = {
                        Switch(settingsViewModel.debugShowLayoutGrids, null)
                    },
                    onSelected = {
                        settingsViewModel.debugShowLayoutGrids =
                            !settingsViewModel.debugShowLayoutGrids
                    },
                )
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsDebugScreenPreview() {
    MyTvTheme {
        SettingsDebugScreen()
    }
}