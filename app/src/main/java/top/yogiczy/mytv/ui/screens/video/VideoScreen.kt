package top.yogiczy.mytv.ui.screens.video

import android.view.SurfaceView
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun VideoScreen(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    state: ExoPlayerState = rememberExoPlayerState(exoPlayer),
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier
            .aspectRatio(state.aspectRatio),
        factory = {
            // PlayerView 切换视频时黑屏闪烁，使用 SurfaceView 代替
            SurfaceView(context)
        },
        update = { surfaceView ->
            exoPlayer.setVideoSurfaceView(surfaceView)
        },
    )
}

class ExoPlayerState {
    var resolution by mutableStateOf(Pair(0, 0))
    var aspectRatio by mutableFloatStateOf(16f / 9f)
    var error by mutableStateOf(false)
}

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

    DisposableEffect(Unit) {
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    return state
}
