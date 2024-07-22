package top.yogiczy.mytv.ui.screens.leanback.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.foundation.lazy.list.TvLazyColumn
import top.yogiczy.mytv.ui.screens.leanback.settings.LeanbackSettingsViewModel
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.SP
import top.yogiczy.mytv.utils.humanizeMs
import kotlin.math.max

@Composable
fun LeanbackSettingsCategoryVideoPlayer(
    modifier: Modifier = Modifier,
    settingsViewModel: LeanbackSettingsViewModel = viewModel(),
) {
    TvLazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
    ) {
        item {
            LeanbackSettingsCategoryListItem(
                headlineContent = "全局画面比例",
                trailingContent = when (settingsViewModel.videoPlayerAspectRatio) {
                    SP.VideoPlayerAspectRatio.ORIGINAL -> "原始"
                    SP.VideoPlayerAspectRatio.SIXTEEN_NINE -> "16:9"
                    SP.VideoPlayerAspectRatio.FOUR_THREE -> "4:3"
                    SP.VideoPlayerAspectRatio.AUTO -> "自动拉伸"
                },
                onSelected = {
                    settingsViewModel.videoPlayerAspectRatio =
                        SP.VideoPlayerAspectRatio.entries.let {
                            it[(it.indexOf(settingsViewModel.videoPlayerAspectRatio) + 1) % it.size]
                        }
                },
            )
        }


        item {
            val min = 1000 * 5L
            val max = 1000 * 30L
            val step = 1000 * 5L

            LeanbackSettingsCategoryListItem(
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
            LeanbackSettingsCategoryListItem(
                headlineContent = "播放器自定义UA",
                supportingContent = settingsViewModel.videoPlayerUserAgent,
                remoteConfig = true,
            )
        }
    }
}

@Preview
@Composable
private fun LeanbackSettingsCategoryHttpPreview() {
    SP.init(LocalContext.current)
    LeanbackTheme {
        LeanbackSettingsCategoryVideoPlayer(
            modifier = Modifier.padding(20.dp),
        )
    }
}
