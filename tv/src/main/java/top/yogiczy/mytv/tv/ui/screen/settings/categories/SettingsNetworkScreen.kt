package top.yogiczy.mytv.tv.ui.screen.settings.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.core.util.utils.humanizeMs
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.screen.settings.components.SettingsListItem
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme

@Composable
fun SettingsNetworkScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()

    AppScreen(
        modifier = modifier.padding(top = 10.dp),
        header = { Text("设置 / 网络") },
        canBack = true,
        onBackPressed = onBackPressed,
    ) {
        LazyColumn(
            contentPadding = childPadding.copy(top = 10.dp).paddingValues,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                SettingsListItem(
                    headlineContent = "HTTP请求重试次数",
                    supportingContent = "影响直播源、节目单数据获取",
                    trailingContent = Constants.NETWORK_RETRY_COUNT.toString(),
                    locK = true,
                )
            }

            item {
                SettingsListItem(
                    headlineContent = "HTTP请求重试间隔时间",
                    supportingContent = "影响直播源、节目单数据获取",
                    trailingContent = Constants.NETWORK_RETRY_INTERVAL.humanizeMs(),
                    locK = true,
                )
            }
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsNetworkScreenPreview() {
    MyTvTheme {
        SettingsNetworkScreen()
    }
}