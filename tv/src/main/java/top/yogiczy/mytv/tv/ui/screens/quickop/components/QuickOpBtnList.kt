package top.yogiczy.mytv.tv.ui.screens.quickop.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
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
    onShowVideoPlayerController: () -> Unit = {},
    onShowVideoPlayerDisplayMode: () -> Unit = {},
    onShowMoreSettings: () -> Unit = {},
    onClearCache: () -> Unit = {},
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
        item {
            QuickOpBtn(
                modifier = Modifier.focusOnLaunched(),
                title = { Text("节目单") },
                onSelect = onShowEpg,
            )
        }

        item {
            QuickOpBtn(
                title = { Text("多线路") },
                onSelect = onShowChannelUrl,
            )
        }

        item {
            QuickOpBtn(
                title = { Text("播放控制") },
                onSelect = onShowVideoPlayerController,
            )
        }

        item {
            QuickOpBtn(
                title = { Text("显示模式") },
                onSelect = onShowVideoPlayerDisplayMode,
            )
        }

        item {
            QuickOpBtn(
                title = { Text("清除缓存") },
                onSelect = onClearCache,
            )
        }
        item {
            QuickOpBtn(
                title = { Text("更多设置") },
                onSelect = onShowMoreSettings,
            )
        }
    }
}

@Preview
@Composable
private fun QuickOpBtnListPreview() {
    MyTVTheme {
        QuickOpBtnList()
    }
}