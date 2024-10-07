package top.yogiczy.mytv.tv.ui.screensold.videoplayer.player

import android.view.SurfaceView
import android.view.TextureView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yogiczy.mytv.core.data.entities.channel.ChannelLine
import top.yogiczy.mytv.tv.ui.utils.Configs

abstract class VideoPlayer(
    private val coroutineScope: CoroutineScope,
) {
    protected var metadata = Metadata()

    open fun initialize() {
        clearAllListeners()
    }

    open fun release() {
        clearAllListeners()
    }

    abstract fun prepare(line: ChannelLine)

    abstract fun play()

    abstract fun pause()

    abstract fun seekTo(position: Long)

    abstract fun setVolume(volume: Float)

    abstract fun getVolume(): Float

    open fun stop() {
        loadTimeoutJob?.cancel()
        interruptJob?.cancel()
        currentPosition = 0L
    }

    abstract fun setVideoSurfaceView(surfaceView: SurfaceView)

    abstract fun setVideoTextureView(textureView: TextureView)

    private var loadTimeoutJob: Job? = null
    private var interruptJob: Job? = null
    private var currentPosition = 0L

    private val onResolutionListeners = mutableListOf<(width: Int, height: Int) -> Unit>()
    private val onErrorListeners = mutableListOf<(error: PlaybackException?) -> Unit>()
    private val onReadyListeners = mutableListOf<() -> Unit>()
    private val onBufferingListeners = mutableListOf<(buffering: Boolean) -> Unit>()
    private val onPreparedListeners = mutableListOf<() -> Unit>()
    private val onIsPlayingChanged = mutableListOf<(isPlaying: Boolean) -> Unit>()
    private val onDurationChanged = mutableListOf<(duration: Long) -> Unit>()
    private val onCurrentPositionChanged = mutableListOf<(position: Long) -> Unit>()
    private val onMetadataListeners = mutableListOf<(metadata: Metadata) -> Unit>()
    private val onInterruptListeners = mutableListOf<() -> Unit>()

    private fun clearAllListeners() {
        onResolutionListeners.clear()
        onErrorListeners.clear()
        onReadyListeners.clear()
        onBufferingListeners.clear()
        onPreparedListeners.clear()
        onIsPlayingChanged.clear()
        onDurationChanged.clear()
        onCurrentPositionChanged.clear()
        onMetadataListeners.clear()
        onInterruptListeners.clear()
    }

    protected fun triggerResolution(width: Int, height: Int) {
        onResolutionListeners.forEach { it(width, height) }
    }

    protected fun triggerError(error: PlaybackException?) {
        onErrorListeners.forEach { it(error) }
        if (error != PlaybackException.LOAD_TIMEOUT) {
            loadTimeoutJob?.cancel()
            loadTimeoutJob = null
        }
    }

    protected fun triggerReady() {
        onReadyListeners.forEach { it() }
        loadTimeoutJob?.cancel()
    }

    protected fun triggerBuffering(buffering: Boolean) {
        onBufferingListeners.forEach { it(buffering) }
    }

    protected fun triggerPrepared() {
        onPreparedListeners.forEach { it() }
        loadTimeoutJob?.cancel()
        loadTimeoutJob = coroutineScope.launch {
            delay(Configs.videoPlayerLoadTimeout)
            triggerError(PlaybackException.LOAD_TIMEOUT)
        }
        interruptJob?.cancel()
        interruptJob = null
        metadata = Metadata()
    }

    protected fun triggerIsPlayingChanged(isPlaying: Boolean) {
        onIsPlayingChanged.forEach { it(isPlaying) }
    }

    protected fun triggerDuration(duration: Long) {
        onDurationChanged.forEach { it(duration) }
    }

    protected fun triggerMetadata(metadata: Metadata) {
        onMetadataListeners.forEach { it(metadata) }
    }

    protected fun triggerCurrentPosition(position: Long) {
        if (currentPosition != position) {
            interruptJob?.cancel()
            interruptJob = coroutineScope.launch {
                delay(Configs.videoPlayerLoadTimeout)
                onInterruptListeners.forEach { it() }
            }
        }
        currentPosition = position
        onCurrentPositionChanged.forEach { it(position) }
    }

    fun onResolution(listener: (width: Int, height: Int) -> Unit) {
        onResolutionListeners.add(listener)
    }

    fun onError(listener: (error: PlaybackException?) -> Unit) {
        onErrorListeners.add(listener)
    }

    fun onReady(listener: () -> Unit) {
        onReadyListeners.add(listener)
    }

    fun onBuffering(listener: (buffering: Boolean) -> Unit) {
        onBufferingListeners.add(listener)
    }

    fun onPrepared(listener: () -> Unit) {
        onPreparedListeners.add(listener)
    }

    fun onIsPlayingChanged(listener: (isPlaying: Boolean) -> Unit) {
        onIsPlayingChanged.add(listener)
    }

    fun onDurationChanged(listener: (duration: Long) -> Unit) {
        onDurationChanged.add(listener)
    }

    fun onCurrentPositionChanged(listener: (position: Long) -> Unit) {
        onCurrentPositionChanged.add(listener)
    }

    fun onMetadata(listener: (metadata: Metadata) -> Unit) {
        onMetadataListeners.add(listener)
    }

    fun onInterrupt(listener: () -> Unit) {
        onInterruptListeners.add(listener)
    }

    data class PlaybackException(val errorCodeName: String, val errorCode: Int) :
        Exception(errorCodeName) {
        companion object {
            val UNSUPPORTED_TYPE = PlaybackException("UNSUPPORTED_TYPE", 10002)
            val LOAD_TIMEOUT = PlaybackException("LOAD_TIMEOUT", 10003)
        }
    }

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