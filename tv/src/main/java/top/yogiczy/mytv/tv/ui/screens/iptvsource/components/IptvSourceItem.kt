package top.yogiczy.mytv.tv.ui.screens.iptvsource.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Text
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSource
import top.yogiczy.mytv.tv.ui.material.Tag
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunchedSaveable
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse

@Composable
fun IptvSourceItem(
    modifier: Modifier = Modifier,
    iptvSourceProvider: () -> IptvSource,
    isSelectedProvider: () -> Boolean = { false },
    onSelected: () -> Unit = {},
    onDeleted: () -> Unit = {},
) {
    val iptvSource = iptvSourceProvider()
    val isSelected = isSelectedProvider()

    var isFocused by remember { mutableStateOf(false) }

    ListItem(
        modifier = modifier
            .ifElse(isSelected, Modifier.focusOnLaunchedSaveable())
            .onFocusChanged { isFocused = it.isFocused || it.hasFocus }
            .handleKeyEvents(
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
        },
        trailingContent = {
            RadioButton(selected = isSelected, onClick = onSelected)
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
                isSelectedProvider = { true },
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