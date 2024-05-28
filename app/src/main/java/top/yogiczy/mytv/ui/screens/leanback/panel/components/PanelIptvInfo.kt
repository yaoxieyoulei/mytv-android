package top.yogiczy.mytv.ui.screens.leanback.panel.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yogiczy.mytv.data.entities.EpgProgrammeCurrent
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.ui.theme.LeanbackTheme
import java.net.Inet6Address
import java.net.InetAddress

@Composable
fun LeanbackPanelIptvInfo(
    modifier: Modifier = Modifier,
    iptvProvider: () -> Iptv = { Iptv() },
    iptvUrlIdxProvider: () -> Int = { 0 },
    currentProgrammesProvider: () -> EpgProgrammeCurrent? = { null },
) {
    val iptv = iptvProvider()
    val iptvUrlIdx = iptvUrlIdxProvider()
    val currentProgrammes = currentProgrammesProvider()

    val urlType = rememberLeanbackIptvUrlType(iptv.urlList[iptvUrlIdx])

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = iptv.name,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.alignByBaseline(),
                maxLines = 1,
            )

            Spacer(modifier = Modifier.width(6.dp))

            Row(
                // FIXME 没对齐，临时解决
                modifier = Modifier.padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.labelMedium,
                    LocalContentColor provides LocalContentColor.current.copy(alpha = 0.8f),
                ) {
                    val textModifier = Modifier
                        .background(
                            LocalContentColor.current.copy(alpha = 0.3f),
                            MaterialTheme.shapes.extraSmall,
                        )
                        .padding(vertical = 2.dp, horizontal = 4.dp)

                    // 多线路标识
                    if (iptv.urlList.size > 1) {
                        Text(
                            text = "${iptvUrlIdx + 1}/${iptv.urlList.size}",
                            modifier = textModifier,
                        )
                    }

                    // ipv4、iptv6标识
                    if (urlType != LeanbackUrlType.UNKNOWN) {
                        Text(
                            text = if (urlType == LeanbackUrlType.IPV6) "IPV6" else "IPV4",
                            modifier = textModifier,
                        )
                    }
                }
            }
        }

        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodyLarge,
            LocalContentColor provides LocalContentColor.current.copy(alpha = 0.8f),
        ) {
            Text(
                text = "正在播放：${currentProgrammes?.now?.title ?: "无节目"}",
                maxLines = 1,
            )
            Text(
                text = "稍后播放：${currentProgrammes?.next?.title ?: "无节目"}",
                maxLines = 1,
            )
        }
    }
}

@Composable
fun rememberLeanbackIptvUrlType(url: String): LeanbackUrlType {
    var urlType by remember { mutableStateOf(LeanbackUrlType.UNKNOWN) }

    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            urlType = LeanbackUrlType.UNKNOWN
            try {
                val uri = Uri.parse(url)
                urlType =
                    if (InetAddress.getByName(uri.host) is Inet6Address) LeanbackUrlType.IPV6
                    else LeanbackUrlType.IPV4
            } catch (_: Exception) {
            }
        }
    }

    return urlType
}

enum class LeanbackUrlType {
    UNKNOWN,
    IPV4,
    IPV6;
}

@Preview
@Composable
private fun LeanbackPanelIptvInfoPreview() {
    LeanbackTheme {
        LeanbackPanelIptvInfo(
            iptvProvider = { Iptv.EXAMPLE },
            currentProgrammesProvider = { EpgProgrammeCurrent.EXAMPLE },
        )
    }
}