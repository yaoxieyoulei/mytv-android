package top.yogiczy.mytv.tv.ui.screens.iptvsource.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.data.entities.channel.ChannelGroupList
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSource
import top.yogiczy.mytv.core.data.repositories.iptv.IptvRepository
import top.yogiczy.mytv.tv.ui.material.Tag
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.utils.Configs
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunchedSaveable
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse

@Composable
fun IptvSourceItem(
    modifier: Modifier = Modifier,
    iptvSourceProvider: () -> IptvSource,
    selectedProvider: () -> Boolean = { false },
    onSelected: () -> Unit = {},
    onDeleted: () -> Unit = {},
) {
    val iptvSource = iptvSourceProvider()
    val selected = selectedProvider()

    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    var channelGroupList by remember { mutableStateOf(ChannelGroupList()) }

    LaunchedEffect(Unit) {
        if (channelGroupList.isEmpty()) {
            channelGroupList = try {
                IptvRepository(iptvSource).getChannelGroupList(cacheTime = Configs.iptvSourceCacheTime)
            } catch (ex: Exception) {
                ChannelGroupList()
            }
        }
    }

    ListItem(
        modifier = modifier
            .ifElse(selected, Modifier.focusOnLaunchedSaveable())
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .handleKeyEvents(
                isFocused = { isFocused },
                focusRequester = focusRequester,
                onSelect = onSelected,
                onLongSelect = onDeleted,
            ),
        selected = false,
        onClick = {},
        headlineContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(iptvSource.name)

                Tag(if (iptvSource.isLocal) "本地" else "远程")
            }
        },
        supportingContent = {
            Text(
                iptvSource.url,
                maxLines = if (isFocused) Int.MAX_VALUE else 1,
            )

            if (channelGroupList.isNotEmpty()) {
                val totalChannelGroupCount = channelGroupList.size
                val totalChannelCount = channelGroupList.sumOf { it.channelList.size }
                Text("共${totalChannelGroupCount}个分组，${totalChannelCount}个频道")
            } else {
                Text("")
            }
        },
        trailingContent = {
            RadioButton(selected = selected, onClick = onSelected)
        },
    )
}

@Preview
@Composable
private fun IptvSourceItemPreview() {
    MyTVTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            IptvSourceItem(
                iptvSourceProvider = {
                    IptvSource(
                        name = "直播源1",
                        url = "https://gh.con.sh/https://raw.githubusercontent.com/yuanzl77/IPTV/main/live.m3u"
                    )
                },
                selectedProvider = { true },
            )

            IptvSourceItem(
                iptvSourceProvider = {
                    IptvSource(
                        name = "直播源1",
                        url = "https://gh.con.sh/https://raw.githubusercontent.com/yuanzl77/IPTV/main/live.m3u"
                    )
                },
            )
        }
    }
}