package top.yogiczy.mytv.tv.ui.screen.multiview

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList.Companion.channelList
import top.yogiczy.mytv.core.data.entities.epg.EpgList
import top.yogiczy.mytv.tv.ui.material.Snackbar
import top.yogiczy.mytv.tv.ui.screen.multiview.components.MultiViewItem
import top.yogiczy.mytv.tv.ui.screen.multiview.components.MultiViewLayout

@Composable
fun MultiViewScreen(
    modifier: Modifier = Modifier,
    channelGroupListProvider: () -> ChannelGroupList = { ChannelGroupList() },
    epgListProvider: () -> EpgList = { EpgList() },
    onBackPressed: () -> Unit = {},
) {
    val channelList =
        remember { mutableStateListOf(channelGroupListProvider().channelList.first()) }

    BackHandler { onBackPressed() }
    Box(
        modifier = modifier.background(Color.Black),
    ) {
        MultiViewLayout(
            count = channelList.size,
            keyList = channelList.map { it.hashCode() },
        ) { index ->
            MultiViewItem(
                channelGroupListProvider = channelGroupListProvider,
                epgListProvider = epgListProvider,
                channelProvider = { channelList[index] },
                viewCountProvider = { channelList.size },
                onAddChannel = {
                    if (channelList.size >= MULTI_VIEW_MAX_COUNT) {
                        Snackbar.show("最多只能添加${MULTI_VIEW_MAX_COUNT}个频道")
                        return@MultiViewItem
                    }

                    if (channelList.contains(it)) {
                        Snackbar.show("已存在该频道")
                        return@MultiViewItem
                    }

                    channelList.add(it)
                },
                onRemoveChannel = {
                    if (channelList.size == 1) {
                        Snackbar.show("至少保留一个频道")
                        return@MultiViewItem
                    }

                    channelList.remove(it)
                },
                onChangeChannel = {
                    if (channelList.contains(it)) {
                        Snackbar.show("已存在该频道")
                        return@MultiViewItem
                    }

                    channelList[index] = it
                },
            )
        }
    }
}

const val MULTI_VIEW_MAX_COUNT = 9