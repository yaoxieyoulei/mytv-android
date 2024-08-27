package top.yogiczy.mytv.tv.ui.screen.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoDisturb
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.data.entities.channel.Channel
import top.yogiczy.mytv.core.data.entities.channel.ChannelList
import top.yogiczy.mytv.core.data.entities.epg.EpgList
import top.yogiczy.mytv.tv.ui.screen.channels.components.ChannelsChannelGrid
import top.yogiczy.mytv.tv.ui.screen.components.AppScreen
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    channelListProvider: () -> ChannelList = { ChannelList() },
    onChannelSelected: (Channel) -> Unit = {},
    onChannelFavoriteToggle: (Channel) -> Unit = {},
    onChannelFavoriteClear: () -> Unit = {},
    epgListProvider: () -> EpgList = { EpgList() },
    onBackPressed: () -> Unit = {},
) {
    AppScreen(
        modifier = modifier.padding(top = 10.dp),
        header = { Text("我的收藏") },
        headerExtra = { FavoritesScreenClearBtn(onClear = onChannelFavoriteClear) },
        canBack = true,
        onBackPressed = onBackPressed,
    ) {
        ChannelsChannelGrid(
            channelListProvider = channelListProvider,
            onChannelSelected = onChannelSelected,
            onChannelFavoriteToggle = onChannelFavoriteToggle,
            epgListProvider = epgListProvider,
            inFavoriteMode = true,
        )

        if (channelListProvider().isEmpty()) {
            FavoritesScreenEmpty()
        }
    }
}

@Composable
private fun FavoritesScreenClearBtn(
    modifier: Modifier = Modifier,
    onClear: () -> Unit = {},
) {
    Button(
        modifier = modifier.handleKeyEvents(onSelect = onClear),
        colors = ButtonDefaults.colors(
            containerColor = MaterialTheme.colorScheme.onSurface.copy(0.1f),
        ),
        onClick = {},
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Outlined.DeleteOutline,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Text("清空")
        }
    }
}

@Composable
private fun FavoritesScreenEmpty(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                Icons.Default.DoDisturb,
                contentDescription = null,
                modifier = Modifier.size(52.dp),
            )
            Text("暂无数据", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun FavoritesScreenPreview() {
    MyTvTheme {
        FavoritesScreen(
            channelListProvider = { ChannelList.EXAMPLE },
            epgListProvider = { EpgList.example(ChannelList.EXAMPLE) },
        )
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun FavoritesScreenEmptyPreview() {
    MyTvTheme {
        FavoritesScreen()
    }
}