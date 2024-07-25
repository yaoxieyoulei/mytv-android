package top.yogiczy.mytv.tv.ui.screens.iptvsource.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import top.yogiczy.mytv.core.data.repositories.iptv.IptvRepository
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.utils.Configs
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunchedSaveable
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse

@Composable
fun IptvSourceItem(
    modifier: Modifier = Modifier,
    iptvSourceUrlProvider: () -> String = { "" },
    selectedProvider: () -> Boolean = { false },
    onSelected: () -> Unit = {},
    onDeleted: () -> Unit = {},
) {
    val iptvSourceUrl = iptvSourceUrlProvider()
    val selected = selectedProvider()

    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    var channelGroupList by remember { mutableStateOf(ChannelGroupList()) }

    LaunchedEffect(Unit) {
        channelGroupList = try {
            IptvRepository(iptvSourceUrl).getChannelGroupList(cacheTime = Configs.iptvSourceCacheTime)
        } catch (ex: Exception) {
            ChannelGroupList()
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
            Text(
                if (iptvSourceUrl == Constants.IPTV_SOURCE_URL) "默认直播源" else iptvSourceUrl,
                maxLines = if (isFocused) Int.MAX_VALUE else 1,
            )
        },
        supportingContent = {
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
                iptvSourceUrlProvider = { "https://iptv-org.github.io/epg.xml" },
                selectedProvider = { true },
            )

            IptvSourceItem(
                iptvSourceUrlProvider = { "https://iptv-org.github.io/epg.xml" },
            )
        }
    }
}