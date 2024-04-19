package top.yogiczy.mytv.ui.screens.panel.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import top.yogiczy.mytv.data.entities.EpgProgrammeCurrent
import top.yogiczy.mytv.data.entities.Iptv
import top.yogiczy.mytv.ui.theme.MyTVTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelIptvInfo(
    modifier: Modifier = Modifier,
    iptv: Iptv = Iptv.EMPTY,
    currentProgrammes: EpgProgrammeCurrent? = null,
    playerError: Boolean = false,
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = iptv.name,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.width(16.dp))

            if (playerError) {
                Text(
                    text = "播放失败",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error,
                    softWrap = false,
                )
            }
        }
        Text(
            text = "正在播放：${currentProgrammes?.now?.title ?: "无节目"}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            maxLines = 1,
        )
        Text(
            text = "正在播放：${currentProgrammes?.next?.title ?: "无节目"}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            maxLines = 1,
        )
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