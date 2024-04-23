package top.yogiczy.mytv.ui.screens.panel.components

import android.net.TrafficStats
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.delay
import top.yogiczy.mytv.ui.theme.MyTVTheme
import java.text.DecimalFormat

@Composable
fun PanelPlayerInfo(
    modifier: Modifier = Modifier,
    resolution: Pair<Int, Int> = Pair(0, 0),
    netSpeed: Long = rememberNetSpeed(),
) {
    Row(modifier = modifier) {
        PanelPlayerInfoResolution(resolution)
        Spacer(modifier = Modifier.width(16.dp))
        PanelPlayerInfoNetSpeed(netSpeed = netSpeed)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelPlayerInfoResolution(
    resolution: Pair<Int, Int>,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "分辨率：${resolution.first}×${resolution.second}",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier,
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PanelPlayerInfoNetSpeed(
    modifier: Modifier = Modifier,
    netSpeed: Long = rememberNetSpeed(),
) {
    Text(
        text = if (netSpeed < 1024 * 999) "网速：${netSpeed / 1024}KB/s"
        else "网速：${DecimalFormat("#.#").format(netSpeed / 1024 / 1024f)}MB/s",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier,
    )
}

@Composable
private fun rememberNetSpeed(): Long {
    var netSpeed by remember { mutableLongStateOf(0) }

    LaunchedEffect(Unit) {
        var lastTotalRxBytes = TrafficStats.getTotalRxBytes()
        var lastTimeStamp = System.currentTimeMillis()

        while (true) {
            delay(1000)
            val nowTotalRxBytes = TrafficStats.getTotalRxBytes()
            val nowTimeStamp = System.currentTimeMillis()
            val speed = (nowTotalRxBytes - lastTotalRxBytes) / (nowTimeStamp - lastTimeStamp) * 1000
            lastTimeStamp = nowTimeStamp
            lastTotalRxBytes = nowTotalRxBytes

            netSpeed = speed
        }
    }

    return netSpeed
}

@Preview
@Composable
private fun PanelPlayerInfoPreview() {
    MyTVTheme {
        PanelPlayerInfo(
            resolution = Pair(1920, 1080),
            netSpeed = 242313,
        )
    }
}