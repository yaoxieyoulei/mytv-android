package top.yogiczy.mytv.tv.ui.screen.settings.subcategories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.launch
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList.Companion.channelList
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSource
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSourceList
import top.yogiczy.mytv.core.data.repositories.iptv.IptvRepository
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.tv.ui.material.CircularProgressIndicator
import top.yogiczy.mytv.tv.ui.material.SimplePopup
import top.yogiczy.mytv.tv.ui.material.Tag
import top.yogiczy.mytv.tv.ui.material.TagDefaults
import top.yogiczy.mytv.tv.ui.rememberChildPadding
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.screen.push.PushContent
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunched
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents

@Composable
fun SettingsIptvSourceScreen(
    modifier: Modifier = Modifier,
    currentIptvSourceProvider: () -> IptvSource = { IptvSource() },
    iptvSourceListProvider: () -> IptvSourceList = { IptvSourceList() },
    onIptvSourceSelected: (IptvSource) -> Unit = {},
    onIptvSourceDelete: (IptvSource) -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    val iptvSourceList = IptvSourceList(Constants.IPTV_SOURCE_LIST + iptvSourceListProvider())

    val coroutineScope = rememberCoroutineScope()
    val detailList = remember { mutableStateListOf<IptvSourceDetail>() }

    suspend fun refreshAll() {
        if (detailList.any { it == IptvSourceDetail.Loading }) return

        detailList.clear()
        detailList.addAll(List(iptvSourceList.size) { IptvSourceDetail.Loading })

        iptvSourceList.forEachIndexed { index, iptvSource ->
            try {
                val channelGroupList = IptvRepository(iptvSource).getChannelGroupList(0)
                detailList[index] = IptvSourceDetail.Ready(
                    channelGroupCount = channelGroupList.size,
                    channelCount = channelGroupList.channelList.size,
                    lineCount = channelGroupList.channelList.sumOf { it.lineList.size },
                )
            } catch (_: Exception) {
                detailList[index] = IptvSourceDetail.Error
            }
        }
    }

    AppScreen(
        modifier = modifier,
        header = { Text("设置 / 直播源 / 自定义直播源") },
        headerExtra = {
            Button(
                modifier = modifier
                    .handleKeyEvents(onSelect = {
                        coroutineScope.launch {
                            refreshAll()
                        }
                    }),
                colors = ButtonDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                ),
                onClick = {},
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Text("刷新全部")
                }
            }
        },
        canBack = true,
        onBackPressed = onBackPressed,
    ) {
        SettingsIptvSourceContent(
            currentIptvSourceProvider = currentIptvSourceProvider,
            iptvSourceListProvider = { iptvSourceList },
            iptvSourceDetailListProvider = { detailList },
            onIptvSourceSelected = onIptvSourceSelected,
            onIptvSourceDelete = onIptvSourceDelete,
        )
    }
}

@Composable
private fun SettingsIptvSourceContent(
    modifier: Modifier = Modifier,
    currentIptvSourceProvider: () -> IptvSource = { IptvSource() },
    iptvSourceListProvider: () -> IptvSourceList = { IptvSourceList() },
    iptvSourceDetailListProvider: () -> List<IptvSourceDetail> = { listOf() },
    onIptvSourceSelected: (IptvSource) -> Unit = {},
    onIptvSourceDelete: (IptvSource) -> Unit = {},
) {
    val iptvSourceList = iptvSourceListProvider()

    val childPadding = rememberChildPadding()

    LazyColumn(
        modifier = modifier.padding(top = 10.dp),
        contentPadding = childPadding.copy(top = 10.dp).paddingValues,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        itemsIndexed(
            iptvSourceList,
            key = { _, source -> source.hashCode() },
        ) { index, iptvSource ->
            IptvSourceItem(
                iptvSourceProvider = { iptvSource },
                iptvSourceDetailProvider = {
                    iptvSourceDetailListProvider().getOrNull(index) ?: IptvSourceDetail.None
                },
                isSelectedProvider = { currentIptvSourceProvider() == iptvSource },
                onIptvSourceSelected = { onIptvSourceSelected(iptvSource) },
                onIptvSourceDelete = { onIptvSourceDelete(iptvSource) },
            )
        }

        item {
            var visible by remember { mutableStateOf(false) }

            ListItem(
                modifier = Modifier.handleKeyEvents(onSelect = { visible = true }),
                headlineContent = { Text("添加其他直播源") },
                selected = false,
                onClick = {},
            )

            SimplePopup(
                visibleProvider = { visible },
                onDismissRequest = { visible = false },
            ) {
                PushContent()
            }
        }
    }
}

@Composable
private fun IptvSourceItem(
    modifier: Modifier = Modifier,
    iptvSourceProvider: () -> IptvSource = { IptvSource() },
    iptvSourceDetailProvider: () -> IptvSourceDetail = { IptvSourceDetail.Loading },
    isSelectedProvider: () -> Boolean = { false },
    onIptvSourceSelected: () -> Unit = {},
    onIptvSourceDelete: () -> Unit = {},
) {
    val iptvSource = iptvSourceProvider()
    val iptvSourceDetail = iptvSourceDetailProvider()
    val isSelected = isSelectedProvider()

    ListItem(
        modifier = modifier.handleKeyEvents(
            onSelect = onIptvSourceSelected,
            onLongSelect = onIptvSourceDelete,
        ),
        headlineContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(iptvSource.name)

                Tag(
                    if (iptvSource.isLocal) "本地" else "远程",
                    colors = TagDefaults.colors(
                        containerColor = LocalContentColor.current.copy(0.1f)
                    ),
                )
            }
        },
        supportingContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(iptvSource.url)

                if (iptvSourceDetail is IptvSourceDetail.Ready) {
                    Text(
                        listOf(
                            "共${iptvSourceDetail.channelGroupCount}个分组",
                            "${iptvSourceDetail.channelCount}个频道",
                            "${iptvSourceDetail.lineCount}条线路"
                        ).joinToString("，")
                    )
                }
            }
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (isSelected) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                }

                when (iptvSourceDetail) {
                    is IptvSourceDetail.Loading -> CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 3.dp,
                        color = LocalContentColor.current,
                        trackColor = MaterialTheme.colorScheme.surface.copy(0.1f),
                    )

                    is IptvSourceDetail.Error -> Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )

                    else -> Unit
                }
            }
        },
        selected = false,
        onClick = {},
    )
}

private sealed interface IptvSourceDetail {
    data object None : IptvSourceDetail
    data object Loading : IptvSourceDetail
    data object Error : IptvSourceDetail
    data class Ready(
        val channelGroupCount: Int,
        val channelCount: Int,
        val lineCount: Int,
    ) : IptvSourceDetail
}

@Preview
@Composable
private fun SettingsIptvSourceItemPreview() {
    MyTvTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            IptvSourceItem(
                iptvSourceProvider = { IptvSource.EXAMPLE },
            )

            IptvSourceItem(
                iptvSourceProvider = { IptvSource.EXAMPLE },
                iptvSourceDetailProvider = { IptvSourceDetail.Ready(10, 100, lineCount = 1000) },
            )

            IptvSourceItem(
                iptvSourceProvider = { IptvSource.EXAMPLE },
                iptvSourceDetailProvider = { IptvSourceDetail.Error },
            )

            IptvSourceItem(
                iptvSourceProvider = { IptvSource.EXAMPLE },
                isSelectedProvider = { true },
            )

            IptvSourceItem(
                modifier = Modifier.focusOnLaunched(),
                iptvSourceProvider = { IptvSource.EXAMPLE },
                iptvSourceDetailProvider = { IptvSourceDetail.Error },
                isSelectedProvider = { true },
            )
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun SettingsIptvSourceScreenPreview() {
    MyTvTheme {
        SettingsIptvSourceScreen(
            currentIptvSourceProvider = { IptvSourceList.EXAMPLE.first() },
            iptvSourceListProvider = { IptvSourceList.EXAMPLE },
            onIptvSourceSelected = {},
        )
    }
}