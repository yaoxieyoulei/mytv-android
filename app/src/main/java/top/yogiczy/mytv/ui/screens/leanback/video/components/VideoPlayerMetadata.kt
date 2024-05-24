package top.yogiczy.mytv.ui.screens.leanback.video.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import top.yogiczy.mytv.ui.screens.leanback.video.player.LeanbackVideoPlayer
import top.yogiczy.mytv.ui.theme.LeanbackTheme

@Composable
fun LeanbackVideoPlayerMetadata(
    modifier: Modifier = Modifier,
    metadata: LeanbackVideoPlayer.Metadata,
) {
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.labelMedium,
        LocalContentColor provides MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = modifier
                .background(
                    MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                    MaterialTheme.shapes.extraSmall,
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Column {
                Text("视频", style = MaterialTheme.typography.bodyMedium)
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text("编码: ${metadata.videoMimeType}")
                    Text("解码器: ${metadata.videoDecoder}")
                    Text("分辨率: ${metadata.videoWidth}x${metadata.videoHeight}")
                    Text("色彩: ${metadata.videoColor}")
                    Text("帧率: ${metadata.videoFrameRate}")
                    Text("比特率: ${metadata.videoBitrate / 1024} kbps")
                }
            }

            Column {
                Text("音频", style = MaterialTheme.typography.bodyMedium)
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text("编码: ${metadata.audioMimeType}")
                    Text("解码器: ${metadata.audioDecoder}")
                    Text("声道数: ${metadata.audioChannels}")
                    Text("采样率: ${metadata.audioSampleRate} Hz")
                }
            }
        }
    }
}

@Preview
@Composable
private fun LeanbackVideoMetadataPreview() {
    LeanbackTheme {
        LeanbackVideoPlayerMetadata(
            metadata = LeanbackVideoPlayer.Metadata(
                videoWidth = 1920,
                videoHeight = 1080,
                videoMimeType = "video/hevc",
                videoColor = "BT2020/Limited range/HLG/8/8",
                videoFrameRate = 25.0f,
                videoBitrate = 10605096,
                videoDecoder = "c2.goldfish.h264.decoder",

                audioMimeType = "audio/mp4a-latm",
                audioChannels = 2,
                audioSampleRate = 32000,
                audioDecoder = "c2.android.aac.decoder",
            )
        )
    }
}