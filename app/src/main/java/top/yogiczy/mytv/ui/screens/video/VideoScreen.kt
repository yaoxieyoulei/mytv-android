package top.yogiczy.mytv.ui.screens.video

import android.view.SurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun VideoScreen(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = {
            // PlayerView 切换视频时黑屏闪烁，使用 SurfaceView 代替
            SurfaceView(context)
        },
        update = { surfaceView ->
            exoPlayer.setVideoSurface(surfaceView.holder.surface)
        },
        onRelease = {
            exoPlayer.release()
        }
    )
}
