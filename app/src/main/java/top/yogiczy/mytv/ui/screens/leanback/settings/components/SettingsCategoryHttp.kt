package top.yogiczy.mytv.ui.screens.leanback.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import top.yogiczy.mytv.ui.utils.SP
import top.yogiczy.mytv.utils.humanizeMs

@Composable
fun LeanbackSettingsCategoryHttp(
    modifier: Modifier = Modifier,
) {
    TvLazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
    ) {
        item {
            LeanbackSettingsCategoryListItem(
                headlineContent = "HTTP请求重试次数",
                supportingContent = "影响直播源、节目单数据获取",
                trailingContent = Constants.HTTP_RETRY_COUNT.toString(),
                locK = true,
            )
        }

        item {
            LeanbackSettingsCategoryListItem(
                headlineContent = "HTTP请求重试间隔时间",
                supportingContent = "影响直播源、节目单数据获取",
                trailingContent = Constants.HTTP_RETRY_INTERVAL.humanizeMs(),
                locK = true,
            )
        }
    }
}

@Preview
@Composable
private fun LeanbackSettingsCategoryHttpPreview() {
    SP.init(LocalContext.current)
    LeanbackTheme {
        LeanbackSettingsCategoryHttp(
            modifier = Modifier.padding(20.dp),
        )
    }
}
