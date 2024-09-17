package top.yogiczy.mytv.tv.ui.screensold.channelline.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yogiczy.mytv.core.data.entities.channel.ChannelLine
import top.yogiczy.mytv.core.data.network.request
import top.yogiczy.mytv.core.data.utils.ChannelUtil
import top.yogiczy.mytv.core.util.utils.isIPv6
import top.yogiczy.mytv.tv.ui.material.Tag
import top.yogiczy.mytv.tv.ui.theme.MyTvTheme
import top.yogiczy.mytv.tv.ui.utils.focusOnLaunchedSaveable
import top.yogiczy.mytv.tv.ui.utils.handleKeyEvents
import top.yogiczy.mytv.tv.ui.utils.ifElse
import java.io.IOException
import kotlin.system.measureTimeMillis

@Composable
fun ChannelLineItem(
    modifier: Modifier = Modifier,
    lineProvider: () -> ChannelLine = { ChannelLine() },
    lineIdxProvider: () -> Int = { 0 },
    isSelectedProvider: () -> Boolean = { false },
    onSelected: () -> Unit = {},
) {
    val line = lineProvider()
    val lineIdx = lineIdxProvider()
    val isSelected = isSelectedProvider()

    val lineDelay = rememberLineDelay(line)

    ListItem(
        modifier = modifier
            .ifElse(isSelected, Modifier.focusOnLaunchedSaveable())
            .handleKeyEvents(onSelect = onSelected),
        selected = false,
        onClick = {},
        headlineContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("线路${lineIdx + 1}")

                if (line.hybridType == ChannelLine.HybridType.WebView) {
                    Tag("混合")
                    Tag(ChannelUtil.getHybridWebViewUrlProvider(line.url))
                } else {
                    if (ChannelUtil.urlSupportPlayback(line.url)) Tag("回放")
                    Tag(if (line.url.isIPv6()) "IPV6" else "IPV4")
                    if (lineDelay != 0L) Tag("$lineDelay ms")
                }
            }
        },
        supportingContent = { Text(line.url, maxLines = 1) },
        trailingContent = {
            RadioButton(selected = isSelected, onClick = {})
        },
    )
}

@Composable
private fun rememberLineDelay(line: ChannelLine): Long {
    var elapsedTime by remember { mutableLongStateOf(0) }
    var hasError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                elapsedTime = measureTimeMillis {
                    try {
                        line.url.request({ builder ->
                            builder.apply {
                                line.httpUserAgent?.let { header("User-Agent", it) }
                            }
                        }) { body -> body.string() }
                    } catch (_: IOException) {
                        hasError = true
                    }
                }
            }
        } catch (_: Exception) {
        }
    }

    return if (hasError) 0 else elapsedTime
}

@Preview
@Composable
private fun ChannelLineItemPreview() {
    MyTvTheme {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            ChannelLineItem(
                lineProvider = { ChannelLine("http://dbiptv.sn.chinamobile.com/PLTV/88888890/224/3221226231/index.m3u8") },
                lineIdxProvider = { 0 },
                isSelectedProvider = { true },
            )

            ChannelLineItem(
                lineProvider = { ChannelLine("http://[2409:8087:5e01:34::20]:6610/ZTE_CMS/00000001000000060000000000000131/index.m3u8?IAS") },
                lineIdxProvider = { 0 },
            )

            ChannelLineItem(
                lineProvider = { ChannelUtil.getHybridWebViewLines("cctv1").first() },
                lineIdxProvider = { 0 },
                isSelectedProvider = { true },
            )
        }
    }
}