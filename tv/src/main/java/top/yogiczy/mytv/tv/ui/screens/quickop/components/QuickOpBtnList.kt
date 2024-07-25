package top.yogiczy.mytv.tv.ui.screens.quickop.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import kotlinx.coroutines.flow.distinctUntilChanged
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunched

@Composable
fun QuickOpBtnList(
    modifier: Modifier = Modifier,
    onShowEpg: () -> Unit = {},
    onShowChannelUrl: () -> Unit = {},
    onClearCache: () -> Unit = {},
    videoPlayerAspectRatioProvider: () -> Float = { 16f / 9f },
    onChangeVideoPlayerAspectRatio: (Float) -> Unit = {},
    onShowMoreSettings: () -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    val childPadding = rememberChildPadding()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { _ -> onUserAction() }
    }

    LazyRow(
        modifier = modifier,
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = childPadding.start, end = childPadding.end),
    ) {
        item { QuickOpBtnEpg(modifier = Modifier.focusOnLaunched(), onShowEpg = onShowEpg) }
        item { QuickOpBtnChannelUrl(onShowChannelUrl = onShowChannelUrl) }
        item { QuickOpBtnClearCache(onClearCache = onClearCache) }
        item {
            QuickOpBtnVideoPlayerAspectRatio(
                videoPlayerAspectRatioProvider = videoPlayerAspectRatioProvider,
                onChangeVideoPlayerAspectRatio = onChangeVideoPlayerAspectRatio,
            )
        }
        item { QuickOpBtnMoreSettings(onShowMoreSettings = onShowMoreSettings) }
    }
}

@Composable
private fun QuickOpBtnEpg(
    modifier: Modifier = Modifier,
    onShowEpg: () -> Unit = {},
) {
    QuickOpBtn(
        modifier = modifier,
        title = { Text("节目单") },
        onSelect = onShowEpg,
    )
}

@Composable
private fun QuickOpBtnChannelUrl(
    modifier: Modifier = Modifier,
    onShowChannelUrl: () -> Unit = {},
) {
    QuickOpBtn(
        modifier = modifier,
        title = { Text("多线路") },
        onSelect = onShowChannelUrl,
    )
}

@Composable
private fun QuickOpBtnClearCache(
    modifier: Modifier = Modifier,
    onClearCache: () -> Unit = {},
) {
    QuickOpBtn(
        modifier = modifier,
        title = { Text("清除缓存") },
        onSelect = onClearCache,
    )
}

@Composable
private fun QuickOpBtnVideoPlayerAspectRatio(
    modifier: Modifier = Modifier,
    videoPlayerAspectRatioProvider: () -> Float = { 16f / 9f },
    onChangeVideoPlayerAspectRatio: (Float) -> Unit = {},
) {
    val configuration = LocalConfiguration.current
    val screenAspectRatio =
        configuration.screenWidthDp.toFloat() / configuration.screenHeightDp.toFloat()

    QuickOpBtn(
        modifier = modifier,
        title = {
            Text(
                "画面比例 " + when (videoPlayerAspectRatioProvider()) {
                    16f / 9f -> "16:9"
                    4f / 3f -> "4:3"
                    screenAspectRatio -> "自动拉伸"
                    else -> "原始"
                }
            )
        },
        onSelect = {
            onChangeVideoPlayerAspectRatio(
                when (videoPlayerAspectRatioProvider()) {
                    16f / 9f -> 4f / 3f
                    4f / 3f -> screenAspectRatio
                    screenAspectRatio -> 16f / 9f
                    else -> 16f / 9f
                }
            )
        },
    )
}

@Composable
private fun QuickOpBtnMoreSettings(
    modifier: Modifier = Modifier,
    onShowMoreSettings: () -> Unit = {},
) {
    QuickOpBtn(
        modifier = modifier,
        title = { Text("更多设置") },
        onSelect = onShowMoreSettings,
    )
}

@Preview
@Composable
private fun QuickOpBtnListPreview() {
    MyTVTheme {
        QuickOpBtnList()
    }
}