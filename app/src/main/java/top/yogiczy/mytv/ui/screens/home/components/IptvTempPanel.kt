package top.yogiczy.mytv.ui.screens.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NonInteractiveSurfaceDefaults
import androidx.tv.material3.Surface
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.panel.components.PanelChannelNo
import top.yogiczy.mytv.ui.screens.panel.components.PanelIptvInfo


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun IptvTempPanel(
    modifier: Modifier = Modifier,
    channelNo: Int = 0,
    currentIptv: Iptv = Iptv.EMPTY,
    playerError: Boolean = false,
) {
    val childPadding = rememberChildPadding()

    Box(modifier = modifier.fillMaxSize()) {
        PanelChannelNo(
            channelNo = channelNo.toString().padStart(2, '0'),
            modifier = Modifier
                .padding(top = childPadding.top, end = childPadding.end)
                .align(Alignment.TopEnd),
        )

        Surface(
            colors = NonInteractiveSurfaceDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            ),
            modifier = Modifier
                .padding(start = childPadding.start, bottom = childPadding.bottom)
                .align(Alignment.BottomStart),
        ) {
            PanelIptvInfo(
                iptv = currentIptv,
                playerError = playerError,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}