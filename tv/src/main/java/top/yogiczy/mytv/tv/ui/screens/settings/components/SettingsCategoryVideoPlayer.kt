package top.yogiczy.mytv.tv.ui.screens.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.lifecycle.viewmodel.compose.viewModel
import top.yogiczy.mytv.core.util.utils.humanizeMs
import top.yogiczy.mytv.tv.ui.screens.settings.SettingsViewModel
import top.yogiczy.mytv.tv.ui.utils.Configs
import kotlin.math.max

@Composable
fun SettingsCategoryVideoPlayer(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    SettingsContentList(modifier) {
        item {
            SettingsListItem(
                modifier = Modifier.focusRequester(it),
                headlineContent = "全局画面比例",
                trailingContent = when (settingsViewModel.videoPlayerAspectRatio) {
                    Configs.VideoPlayerAspectRatio.ORIGINAL -> "原始"
                    Configs.VideoPlayerAspectRatio.SIXTEEN_NINE -> "16:9"
                    Configs.VideoPlayerAspectRatio.FOUR_THREE -> "4:3"
                    Configs.VideoPlayerAspectRatio.AUTO -> "自动拉伸"
                },
                onSelected = {
                    settingsViewModel.videoPlayerAspectRatio =
                        Configs.VideoPlayerAspectRatio.entries.let {
                            it[(it.indexOf(settingsViewModel.videoPlayerAspectRatio) + 1) % it.size]
                        }
                },
            )
        }

        item {
            val min = 1000 * 5L
            val max = 1000 * 30L
            val step = 1000 * 5L

            SettingsListItem(
                headlineContent = "播放器加载超时",
                supportingContent = "影响超时换源、断线重连",
                trailingContent = settingsViewModel.videoPlayerLoadTimeout.humanizeMs(),
                onSelected = {
                    settingsViewModel.videoPlayerLoadTimeout =
                        max(min, (settingsViewModel.videoPlayerLoadTimeout + step) % (max + step))
                },
            )
        }

        item {
            SettingsListItem(
                headlineContent = "播放器自定义UA",
                supportingContent = settingsViewModel.videoPlayerUserAgent,
                remoteConfig = true,
            )
        }
    }
}