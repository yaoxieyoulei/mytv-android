package top.yogiczy.mytv.ui.screens.video

import android.view.SurfaceView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import top.yogiczy.mytv.ui.rememberChildPadding
import top.yogiczy.mytv.ui.screens.video.components.VideoDetailInfo

@Composable
fun VideoScreen(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    state: PlayerState = rememberPlayerState(exoPlayer),
    showPlayerInfo: Boolean = false,
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

        if (showPlayerInfo) {
            VideoDetailInfo(
                modifier = Modifier.padding(start = childPadding.start, top = childPadding.top),
                playerState = state,
            )
        }
    }
}