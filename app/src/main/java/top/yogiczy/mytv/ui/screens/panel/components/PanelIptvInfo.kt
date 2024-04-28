package top.yogiczy.mytv.ui.screens.panel.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.LocalTextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yogiczy.mytv.data.entities.EpgProgrammeCurrent
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.ui.theme.MyTVTheme
import java.net.Inet6Address
import java.net.InetAddress

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelIptvInfo(
    modifier: Modifier = Modifier,
    iptv: Iptv = Iptv.EMPTY,
    iptvUrlIdx: Int = 0,
    currentProgrammes: EpgProgrammeCurrent? = null,
    playerError: Boolean = false,
) {
    var isUrlIpv6 by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(iptv, iptvUrlIdx) {
        withContext(Dispatchers.IO) {
            isUrlIpv6 = null
            try {
                val uri = Uri.parse(iptv.urlList[iptvUrlIdx])
                isUrlIpv6 = InetAddress.getByName(uri.host) is Inet6Address
            } catch (ex: Exception) {
                Log.e("PanelIptvInfo", ex.message, ex)
            }
        }
    }

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = iptv.name,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
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
                    LocalContentColor provides MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                ) {
                    val textModifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
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
                    if (isUrlIpv6 != null) {
                        Text(
                            text = if (isUrlIpv6 == true) "IPV6" else "IPV4",
                            modifier = textModifier,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (playerError) {
                Text(
                    text = "播放失败",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.alignByBaseline(),
                    softWrap = false,
                )
            }
        }

        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodyLarge,
            LocalContentColor provides MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
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

@Preview(device = "id:Android TV (720p)")
@Composable
private fun PanelIptvInfoPreview() {
    MyTVTheme {
        PanelIptvInfo(
            iptv = Iptv.EXAMPLE,
            currentProgrammes = EpgProgrammeCurrent.EXAMPLE,
            playerError = true,
        )
    }
}