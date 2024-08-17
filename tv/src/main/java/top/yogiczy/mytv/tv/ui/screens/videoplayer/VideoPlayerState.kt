package top.yogiczy.mytv.tv.ui.screens.videoplayer

import android.view.SurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import top.yogiczy.mytv.tv.ui.screens.videoplayer.player.Media3VideoPlayer
import top.yogiczy.mytv.tv.ui.screens.videoplayer.player.VideoPlayer

@Stable
class VideoPlayerState(
    private val instance: VideoPlayer,
    private var defaultDisplayModeProvider: () -> VideoPlayerDisplayMode = { VideoPlayerDisplayMode.ORIGINAL },
) {
    /** 显示模式 */
    var displayMode by mutableStateOf(defaultDisplayModeProvider())

    /** 视频宽高比 */
    var aspectRatio by mutableFloatStateOf(16f / 9f)

    /** 错误 */
    var error by mutableStateOf<String?>(null)

    /** 正在缓冲 */
    var isBuffering by mutableStateOf(false)

    /** 正在播放 */
    var isPlaying by mutableStateOf(false)

    /** 总时长 */
    var duration by mutableLongStateOf(0L)

    /** 当前播放位置 */
    var currentPosition by mutableLongStateOf(0L)

    /** 元数据 */
    var metadata by mutableStateOf(VideoPlayer.Metadata())

    fun prepare(url: String) {
        error = null
        instance.prepare(url)
    }

    fun play() {
        instance.play()
    }

    fun pause() {
        instance.pause()
    }

    fun seekTo(position: Long) {
        instance.seekTo(position)
    }

    fun stop() {
        instance.stop()
    }

    fun setVideoSurfaceView(surfaceView: SurfaceView) {
        instance.setVideoSurfaceView(surfaceView)
    }

    private val onReadyListeners = mutableListOf<() -> Unit>()
    private val onErrorListeners = mutableListOf<() -> Unit>()
    private val onInterruptListeners = mutableListOf<() -> Unit>()

    fun onReady(listener: () -> Unit) {
        onReadyListeners.add(listener)
    }

    fun onError(listener: () -> Unit) {
        onErrorListeners.add(listener)
    }

    fun onInterrupt(listener: () -> Unit) {
        onInterruptListeners.add(listener)
    }

    fun initialize() {
        instance.initialize()
        instance.onResolution { width, height ->
            if (width > 0 && height > 0) aspectRatio = width.toFloat() / height
        }
        instance.onError { ex ->
            error = ex?.let { "${it.errorCodeName}(${it.errorCode})" }
                ?.apply { onErrorListeners.forEach { it.invoke() } }

        }
        instance.onReady {
            onReadyListeners.forEach { it.invoke() }
            error = null
            displayMode = defaultDisplayModeProvider()
        }
        instance.onBuffering {
            isBuffering = it
            if (it) error = null
        }
        instance.onPrepared { }
        instance.onIsPlayingChanged { isPlaying = it }
        instance.onDurationChanged { duration = it }
        instance.onCurrentPositionChanged { currentPosition = it }
        instance.onMetadata { metadata = it }
        instance.onInterrupt { onInterruptListeners.forEach { it.invoke() } }
    }

    fun release() {
        onReadyListeners.clear()
        onErrorListeners.clear()
        instance.release()
    }
}

@Composable
fun rememberVideoPlayerState(
    defaultDisplayModeProvider: () -> VideoPlayerDisplayMode = { VideoPlayerDisplayMode.ORIGINAL },
): VideoPlayerState {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val state = remember {
        VideoPlayerState(
            Media3VideoPlayer(context, coroutineScope),
            defaultDisplayModeProvider,
        )
    }

    DisposableEffect(Unit) {
        state.initialize()
        onDispose { state.release() }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) state.play()
            else if (event == Lifecycle.Event.ON_STOP) state.pause()
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    return state
}

enum class VideoPlayerDisplayMode(
    val label: String,
    val value: Int,
) {
    /** 原始 */
    ORIGINAL("原始", 0),

    /** 填充 */
    FILL("填充", 1),

    /** 裁剪 */
    CROP("裁剪", 2),

    /** 4:3 */
    FOUR_THREE("4:3", 3),

    /** 16:9 */
    SIXTEEN_NINE("16:9", 4),

    /** 2.35:1 */
    WIDE("2.35:1", 5);

    companion object {
        fun fromValue(value: Int): VideoPlayerDisplayMode {
            return entries.firstOrNull { it.value == value } ?: ORIGINAL
        }
    }
}