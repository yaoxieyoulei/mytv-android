package top.yogiczy.mytv.ui.screens.video

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.Format
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DecoderReuseEvaluation
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener

/**
 * 播放器状态
 */
class PlayerState {
    /** 视频分辨率 */
    var resolution by mutableStateOf(Pair(0, 0))

    /** 视频宽高比 */
    var aspectRatio by mutableFloatStateOf(16f / 9f)

    /** 是否出现错误 */
    var error by mutableStateOf(false)

    /** 元数据 */
    var metadata by mutableStateOf(Metadata())

    /** 元数据 */
    data class Metadata(
        /** 视频编码 */
        val videoMimeType: String = "",
        /** 视频宽度 */
        val videoWidth: Int = 0,
        /** 视频高度 */
        val videoHeight: Int = 0,
        /** 视频颜色 */
        val videoColor: String = "",
        /** 视频帧率 */
        val videoFrameRate: Float = 0f,
        /** 视频比特率 */
        val videoBitrate: Int = 0,
        /** 视频解码器 */
        val videoDecoder: String = "",

        /** 音频编码 */
        val audioMimeType: String = "",
        /** 音频通道 */
        val audioChannels: Int = 0,
        /** 音频采样率 */
        val audioSampleRate: Int = 0,
        /** 音频解码器 */
        val audioDecoder: String = "",
    )
}

@OptIn(UnstableApi::class)
@Composable
fun rememberPlayerState(
    exoPlayer: ExoPlayer = ExoPlayer.Builder(LocalContext.current).build(),
): PlayerState {
    val state = remember { PlayerState() }

    val listener = object : Player.Listener {
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            state.resolution = Pair(videoSize.width, videoSize.height)

            if (videoSize.width != 0 && videoSize.height != 0) {
                state.aspectRatio = videoSize.width.toFloat() / videoSize.height
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            state.error = true

            // 如果是直播加载位置错误，尝试重新播放
            if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                exoPlayer.seekToDefaultPosition()
                exoPlayer.prepare()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_BUFFERING) {
                state.error = false
            }
        }
    }

    val metadataListener = object : AnalyticsListener {
        override fun onVideoInputFormatChanged(
            eventTime: AnalyticsListener.EventTime,
            format: Format,
            decoderReuseEvaluation: DecoderReuseEvaluation?,
        ) {
            state.metadata = state.metadata.copy(
                videoMimeType = format.sampleMimeType ?: "",
                videoWidth = format.width,
                videoHeight = format.height,
                videoColor = format.colorInfo?.toLogString() ?: "",
                // TODO 帧率、比特率目前是从tag中获取，有的返回空，后续需要实时计算
                videoFrameRate = format.frameRate,
                videoBitrate = format.bitrate,
            )
        }

        override fun onVideoDecoderInitialized(
            eventTime: AnalyticsListener.EventTime,
            decoderName: String,
            initializedTimestampMs: Long,
            initializationDurationMs: Long,
        ) {
            state.metadata = state.metadata.copy(videoDecoder = decoderName)
        }

        override fun onAudioInputFormatChanged(
            eventTime: AnalyticsListener.EventTime,
            format: Format,
            decoderReuseEvaluation: DecoderReuseEvaluation?,
        ) {
            state.metadata = state.metadata.copy(
                audioMimeType = format.sampleMimeType ?: "",
                audioChannels = format.channelCount,
                audioSampleRate = format.sampleRate,
            )
        }

        override fun onAudioDecoderInitialized(
            eventTime: AnalyticsListener.EventTime,
            decoderName: String,
            initializedTimestampMs: Long,
            initializationDurationMs: Long,
        ) {
            state.metadata = state.metadata.copy(audioDecoder = decoderName)
        }
    }

    DisposableEffect(Unit) {
        exoPlayer.addListener(listener)
        exoPlayer.addAnalyticsListener(metadataListener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.removeAnalyticsListener(metadataListener)
        }
    }

    return state
}