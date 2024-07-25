package top.yogiczy.mytv.tv.ui.screens.channel.components

import android.net.TrafficStats
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.LocalTextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.delay
import top.yogiczy.mytv.tv.ui.theme.MyTVTheme
import java.text.DecimalFormat

@Composable
fun ChannelPlayerInfo(
    modifier: Modifier = Modifier,
    resolutionProvider: () -> Pair<Int, Int> = { 0 to 0 },
) {
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.bodyLarge
    ) {
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ChannelPlayerInfoResolution(resolutionProvider = resolutionProvider)
            ChannelPlayerInfoNetSpeed()
        }
    }
}

@Composable
private fun ChannelPlayerInfoResolution(
    modifier: Modifier = Modifier,
    resolutionProvider: () -> Pair<Int, Int> = { 0 to 0 },
) {
    val resolution = resolutionProvider()

    Text(
        text = "分辨率：${resolution.first}×${resolution.second}",
        modifier = modifier,
    )
}

@Composable
private fun ChannelPlayerInfoNetSpeed(
    modifier: Modifier = Modifier,
    netSpeed: Long = rememberNetSpeed(),
) {
    Text(
        text = if (netSpeed < 1024 * 999) "网速：${netSpeed / 1024}KB/s"
        else "网速：${DecimalFormat("#.#").format(netSpeed / 1024 / 1024f)}MB/s",
        modifier = modifier,
    )
}

@Composable
private fun rememberNetSpeed(): Long {
    var netSpeed by remember { mutableLongStateOf(0) }

    LaunchedEffect(Unit) {
        var lastTotalRxBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid())
        var lastTimeStamp = System.currentTimeMillis()

        while (true) {
            delay(1000)
            val nowTotalRxBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid())
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
private fun ChannelPlayerInfoNetSpeedPreview() {
    MyTVTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ChannelPlayerInfoNetSpeed()
            ChannelPlayerInfoNetSpeed(netSpeed = 54321)
            ChannelPlayerInfoNetSpeed(netSpeed = 1222 * 1222)
        }
    }
}

@Preview
@Composable
private fun ChannelPlayerInfoPreview() {
    MyTVTheme {
        ChannelPlayerInfo(
            resolutionProvider = { 1920 to 1080 }
        )
    }
}