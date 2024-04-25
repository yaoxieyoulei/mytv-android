package top.yogiczy.mytv.ui.screens.video

import android.view.SurfaceView
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Format
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DecoderReuseEvaluation
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.video.components.VideoDetailInfo
import top.yogiczy.mytv.ui.utils.SP

@Composable
fun VideoScreen(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    state: ExoPlayerState = rememberExoPlayerState(exoPlayer),
) {
    val context = LocalContext.current
    val childPadding = rememberChildPadding()

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .align(Alignment.Center)
                .aspectRatio(state.aspectRatio),
            factory = {
                // PlayerView 切换视频时黑屏闪烁，使用 SurfaceView 代替
                SurfaceView(context)
            },
            update = { surfaceView ->
                exoPlayer.setVideoSurfaceView(surfaceView)
            },
        )

        if (SP.debugShowPlayerInfo) {
            VideoDetailInfo(
                modifier = Modifier.padding(start = childPadding.start, top = childPadding.top),
                playerState = state,
            )
        }
    }
}

class ExoPlayerState {
    var resolution by mutableStateOf(Pair(0, 0))
    var aspectRatio by mutableFloatStateOf(16f / 9f)
    var error by mutableStateOf(false)
    var metadata by mutableStateOf(Metadata())

    data class Metadata(
        val videoMimeType: String = "",
        val videoWidth: Int = 0,
        val videoHeight: Int = 0,
        val videoColor: String = "",
        val videoFrameRate: Float = 0f,
        val videoBitrate: Int = 0,
        val videoDecoder: String = "",

        val audioMimeType: String = "",
        val audioChannels: Int = 0,
        val audioSampleRate: Int = 0,
        val audioDecoder: String = "",
    )
}

@OptIn(UnstableApi::class)
@Composable
fun rememberExoPlayerState(
    exoPlayer: ExoPlayer = ExoPlayer.Builder(LocalContext.current).build(),
): ExoPlayerState {
    val state = remember { ExoPlayerState() }

    val listener = object : Player.Listener {
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            state.resolution = Pair(videoSize.width, videoSize.height)

            if (videoSize.width != 0 && videoSize.height != 0) {
                state.aspectRatio = videoSize.width.toFloat() / videoSize.height
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            state.error = true

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