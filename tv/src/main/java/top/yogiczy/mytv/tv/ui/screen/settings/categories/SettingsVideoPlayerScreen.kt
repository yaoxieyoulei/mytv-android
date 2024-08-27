package top.yogiczy.mytv.tv.ui.screen.settings.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.util.utils.humanizeMs
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.screen.settings.components.SettingsListItem
import top.yogiczy.mytv.tv.ui.screensold.settings.SettingsViewModel
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme

@Composable
fun SettingsVideoPlayerScreen(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
    toVideoPlayerDisplayModeScreen: () -> Unit = {},
    toVideoPlayerLoadTimeoutScreen: () -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()

    AppScreen(
        modifier = modifier.padding(top = 10.dp),
        header = { Text("设置 / 播放器") },
        canBack = true,
        onBackPressed = onBackPressed,
    ) {
        LazyColumn(
            contentPadding = childPadding.copy(top = 10.dp).paddingValues,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                SettingsListItem(
                    headlineContent = "全局显示模式",
                    trailingContent = settingsViewModel.videoPlayerDisplayMode.label,
                    onSelected = toVideoPlayerDisplayModeScreen,
                    link = true,
                )
            }

            item {
                SettingsListItem(
                    headlineContent = "加载超时",
                    supportingContent = "影响超时换源、断线重连",
                    trailingContent = settingsViewModel.videoPlayerLoadTimeout.humanizeMs(),
                    onSelected = toVideoPlayerLoadTimeoutScreen,
                    link = true,
                )
            }

            item {
                SettingsListItem(
                    headlineContent = "自定义ua",
                    supportingContent = settingsViewModel.videoPlayerUserAgent,
                    remoteConfig = true,
                )
            }

            item {
                fun isValidHeaderFormat(headersString: String): Boolean {
                    if (headersString.isBlank()) return true

                    return headersString.lines().all { line ->
                        val parts = line.split(":", limit = 2)
                        parts.size == 2 && parts[0].isNotBlank() && parts[1].isNotBlank()
                    }
                }

                val isValid = isValidHeaderFormat(settingsViewModel.videoPlayerHeaders)

                SettingsListItem(
                    headlineContent = "自定义headers",
                    supportingContent = settingsViewModel.videoPlayerHeaders,
                    remoteConfig = true,
                    trailingIcon = if (!isValid) Icons.Default.ErrorOutline else null,
                )
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsVideoPlayerScreenPreview() {
    MyTvTheme {
        SettingsVideoPlayerScreen(
            settingsViewModel = SettingsViewModel().apply {
                videoPlayerUserAgent =
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
                videoPlayerHeaders = "Accept: "
            }
        )
    }
}