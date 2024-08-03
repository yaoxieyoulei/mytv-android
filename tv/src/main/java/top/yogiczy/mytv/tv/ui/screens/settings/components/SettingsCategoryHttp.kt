package top.yogiczy.mytv.tv.ui.screens.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.core.util.utils.humanizeMs

@Composable
fun SettingsCategoryHttp(
    modifier: Modifier = Modifier,
) {
    SettingsContentList(modifier) {
        item {
            SettingsListItem(
                modifier = Modifier.focusRequester(it),
                headlineContent = "HTTP请求重试次数",
                supportingContent = "影响直播源、节目单数据获取",
                trailingContent = Constants.HTTP_RETRY_COUNT.toString(),
                locK = true,
            )
        }

        item {
            SettingsListItem(
                headlineContent = "HTTP请求重试间隔时间",
                supportingContent = "影响直播源、节目单数据获取",
                trailingContent = Constants.HTTP_RETRY_INTERVAL.humanizeMs(),
                locK = true,
            )
        }
    }
}