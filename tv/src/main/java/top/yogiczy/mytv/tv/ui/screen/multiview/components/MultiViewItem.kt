package top.yogiczy.mytv.tv.ui.screen.multiview.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material.icons.outlined.ZoomInMap
import androidx.compose.material.icons.outlined.ZoomOutMap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.core.data.entities.epg.EpgList
import top.yogiczy.mytv.tv.ui.material.SimplePopup
import top.yogiczy.mytv.tv.ui.screen.channels.ChannelsScreen
import top.yogiczy.mytv.tv.ui.screen.multiview.MULTI_VIEW_MAX_COUNT
import top.yogiczy.mytv.tv.ui.screen.search.SearchScreen
import top.yogiczy.mytv.tv.ui.screensold.videoplayer.VideoPlayerScreen
import top.yogiczy.mytv.tv.ui.screensold.videoplayer.rememberVideoPlayerState
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.utils.backHandler
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunched
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse

@Composable
fun MultiViewItem(
    modifier: Modifier = Modifier,
    channelGroupListProvider: () -> ChannelGroupList = { ChannelGroupList() },
    epgListProvider: () -> EpgList = { EpgList() },
    channelProvider: () -> Channel = { Channel() },
    viewCountProvider: () -> Int = { 0 },
    isZoomInProvider: () -> Boolean = { false },
    onAddChannel: (Channel) -> Unit = {},
    onRemoveChannel: (Channel) -> Unit = {},
    onChangeChannel: (Channel) -> Unit = {},
    onZoomIn: () -> Unit = {},
    onZoomOut: () -> Unit = {},
) {
    val channel = channelProvider()

    var actionVisible by remember { mutableStateOf(false) }
    var addChannelVisible by remember { mutableStateOf(false) }
    var searchAndAddChannelVisible by remember { mutableStateOf(false) }
    var changeChannelVisible by remember { mutableStateOf(false) }

    val state = rememberVideoPlayerState()
    LaunchedEffect(channel) {
        // TODO 获取最优线路
        state.prepare(channel.lineList.first())
    }

    Surface(
        modifier = modifier
            .onFocusChanged {
                if (!it.hasFocus && !it.isFocused) {
                    actionVisible = false
                }
            }
            .handleKeyEvents(onSelect = { actionVisible = true })
            .backHandler({ actionVisible }) { actionVisible = false },
        onClick = {},
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(
                BorderStroke(4.dp, MaterialTheme.colorScheme.onSurface),
            )
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
    ) {
        VideoPlayerScreen(
            state = state
        )

        MultiViewItemActions(
            visibleProvider = { actionVisible },
            onDismissRequest = { actionVisible = false },
            viewCountProvider = viewCountProvider,
            isZoomInProvider = isZoomInProvider,
            onAddChannel = { addChannelVisible = true },
            onSearchAndAddChannel = { searchAndAddChannelVisible = true },
            onChangeChannel = { changeChannelVisible = true },
            onRemoveChannel = { onRemoveChannel(channel) },
            onZoomIn = {
                onZoomIn()
                actionVisible = false
            },
            onZoomOut = {
                onZoomOut()
                actionVisible = false
            },
        )
    }

    SimplePopup(
        visibleProvider = { addChannelVisible },
        onDismissRequest = { addChannelVisible = false },
    ) {
        ChannelsScreen(
            channelGroupListProvider = channelGroupListProvider,
            onChannelSelected = {
                onAddChannel(it)
                addChannelVisible = false
            },
            epgListProvider = epgListProvider,
            onBackPressed = { addChannelVisible = false },
        )
    }

    SimplePopup(
        visibleProvider = { searchAndAddChannelVisible },
        onDismissRequest = { searchAndAddChannelVisible = false },
    ) {
        SearchScreen(
            channelGroupListProvider = channelGroupListProvider,
            onChannelSelected = {
                onAddChannel(it)
                searchAndAddChannelVisible = false
            },
            epgListProvider = epgListProvider,
            onBackPressed = { searchAndAddChannelVisible = false },
        )
    }

    SimplePopup(
        visibleProvider = { changeChannelVisible },
        onDismissRequest = { changeChannelVisible = false },
    ) {
        ChannelsScreen(
            channelGroupListProvider = channelGroupListProvider,
            onChannelSelected = {
                onChangeChannel(it)
                changeChannelVisible = false
            },
            epgListProvider = epgListProvider,
            onBackPressed = { changeChannelVisible = false },
        )
    }
}


@Composable
private fun MultiViewItemActions(
    modifier: Modifier = Modifier,
    visibleProvider: () -> Boolean = { false },
    onDismissRequest: () -> Unit = {},
    viewCountProvider: () -> Int = { 0 },
    isZoomInProvider: () -> Boolean = { false },
    onAddChannel: () -> Unit = {},
    onSearchAndAddChannel: () -> Unit = {},
    onChangeChannel: () -> Unit = {},
    onRemoveChannel: () -> Unit = {},
    onZoomIn: () -> Unit = {},
    onZoomOut: () -> Unit = {},
) {
    if (!visibleProvider()) return

    val viewCount = viewCountProvider()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(0.8f)),
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(0.6f)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MultiViewItemActionItem(
                    title = "添加",
                    imageVector = Icons.Outlined.Add,
                    onSelected = onAddChannel,
                    disabled = viewCount >= MULTI_VIEW_MAX_COUNT,
                    modifier = Modifier
                        .focusOnLaunched()
                        .weight(1f),
                )

                MultiViewItemActionItem(
                    title = "搜索",
                    imageVector = Icons.Outlined.Search,
                    onSelected = onSearchAndAddChannel,
                    disabled = viewCount >= MULTI_VIEW_MAX_COUNT,
                    modifier = Modifier.weight(1f),
                )

                MultiViewItemActionItem(
                    title = "切换",
                    imageVector = Icons.Outlined.SyncAlt,
                    onSelected = onChangeChannel,
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MultiViewItemActionItem(
                    title = "删除",
                    imageVector = Icons.Outlined.DeleteOutline,
                    onSelected = onRemoveChannel,
                    disabled = viewCount <= 1,
                    modifier = Modifier.weight(1f),
                )

                if (isZoomInProvider()) {
                    MultiViewItemActionItem(
                        title = "缩小",
                        imageVector = Icons.Outlined.ZoomInMap,
                        onSelected = onZoomOut,
                        disabled = viewCount <= 1,
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    MultiViewItemActionItem(
                        title = "放大",
                        imageVector = Icons.Outlined.ZoomOutMap,
                        onSelected = onZoomIn,
                        disabled = viewCount <= 1,
                        modifier = Modifier.weight(1f),
                    )
                }

                MultiViewItemActionItem(
                    title = "返回",
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    onSelected = onDismissRequest,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun MultiViewItemActionItem(
    modifier: Modifier = Modifier,
    title: String,
    imageVector: ImageVector,
    onSelected: () -> Unit = {},
    disabled: Boolean = false,
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .ifElse(
                disabled,
                Modifier.alpha(0.5f),
                Modifier.handleKeyEvents(onSelect = onSelected),
            ),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.onSurface.copy(0.1f),
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
        onClick = {},
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(imageVector, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.headlineLarge)
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun MultiViewItemActionsPreview() {
    MyTvTheme {
        MultiViewItemActions(
            visibleProvider = { true },
        )
    }
}