package top.yogiczy.mytv.tv.ui.screensold.videoplayer.player

import android.content.Context
import android.net.Uri
import android.view.SurfaceView
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DecoderReuseEvaluation
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.util.EventLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yogiczy.mytv.core.data.entities.channel.ChannelLine
import top.yogiczy.mytv.core.util.utils.toHeaders
import top.yogiczy.mytv.tv.ui.utils.Configs

@OptIn(UnstableApi::class)
class Media3VideoPlayer(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
) : VideoPlayer(coroutineScope) {
    private val videoPlayer by lazy {
        val renderersFactory =
            DefaultRenderersFactory(context).setExtensionRendererMode(EXTENSION_RENDERER_MODE_ON)

        ExoPlayer.Builder(context).setRenderersFactory(renderersFactory).build()
            .apply { playWhenReady = true }
    }

    private var currentChannelLine = ChannelLine()
    private val contentTypeAttempts = mutableMapOf<Int, Boolean>()
    private var updatePositionJob: Job? = null

    private fun getDataSourceFactory(): DefaultDataSource.Factory {
        return DefaultDataSource.Factory(
            context,
            DefaultHttpDataSource.Factory().apply {
                setUserAgent(currentChannelLine.httpUserAgent ?: Configs.videoPlayerUserAgent)
                setDefaultRequestProperties(Configs.videoPlayerHeaders.toHeaders())
                setConnectTimeoutMs(Configs.videoPlayerLoadTimeout.toInt())
                setReadTimeoutMs(Configs.videoPlayerLoadTimeout.toInt())
                setKeepPostFor302Redirects(true)
                setAllowCrossProtocolRedirects(true)
            },
        )
    }

    private fun getMediaSource(contentType: Int? = null): MediaSource? {
        val uri = Uri.parse(currentChannelLine.url)
        val mediaItem = MediaItem.fromUri(uri)

        if (uri.toString().startsWith("rtp://")) {
            return RtspMediaSource.Factory().createMediaSource(mediaItem)
        }

        val dataSourceFactory = getDataSourceFactory()
        return when (val type = contentType ?: Util.inferContentType(uri)) {
            C.CONTENT_TYPE_HLS -> {
                HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
            }

            C.CONTENT_TYPE_RTSP -> {
                RtspMediaSource.Factory().createMediaSource(mediaItem)
            }

            C.CONTENT_TYPE_OTHER -> {
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
            }

            else -> {
                triggerError(
                    PlaybackException.UNSUPPORTED_TYPE.copy(
                        errorCodeName = "${PlaybackException.UNSUPPORTED_TYPE.message}_$type"
                    )
                )
                null
            }
        }
    }

    private fun prepare(contentType: Int? = null) {
        val uri = Uri.parse(currentChannelLine.url)
        val mediaSource = getMediaSource(contentType)

        if (mediaSource != null) {
            contentTypeAttempts[contentType ?: Util.inferContentType(uri)] = true
            videoPlayer.setMediaSource(mediaSource)
            videoPlayer.prepare()
            videoPlayer.play()
            triggerPrepared()
        }
        updatePositionJob?.cancel()
        updatePositionJob = null
    }

    private val playerListener = object : Player.Listener {
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            triggerResolution(videoSize.width, videoSize.height)
        }

        override fun onPlayerError(ex: androidx.media3.common.PlaybackException) {
            when (ex.errorCode) {
                // 如果是直播加载位置错误，尝试重新播放
                androidx.media3.common.PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW,
                androidx.media3.common.PlaybackException.ERROR_CODE_DECODING_FAILED,
                androidx.media3.common.PlaybackException.ERROR_CODE_IO_UNSPECIFIED -> {
                    videoPlayer.seekToDefaultPosition()
                    videoPlayer.prepare()
                }

                // 当解析容器不支持时，尝试使用其他解析容器
                androidx.media3.common.PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> {
                    videoPlayer.currentMediaItem?.localConfiguration?.uri?.let {
                        if (contentTypeAttempts[C.CONTENT_TYPE_HLS] != true) {
                            prepare(C.CONTENT_TYPE_HLS)
                        } else if (contentTypeAttempts[C.CONTENT_TYPE_RTSP] != true) {
                            prepare(C.CONTENT_TYPE_RTSP)
                        } else if (contentTypeAttempts[C.CONTENT_TYPE_OTHER] != true) {
                            prepare(C.CONTENT_TYPE_OTHER)
                        } else {
                            val type = Util.inferContentType(it)
                            triggerError(
                                PlaybackException.UNSUPPORTED_TYPE.copy(
                                    errorCodeName = "${PlaybackException.UNSUPPORTED_TYPE.message}_$type"
                                )
                            )
                        }
                    }
                }

                else -> {
                    triggerError(PlaybackException(ex.errorCodeName, ex.errorCode))
                }
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_BUFFERING) {
                triggerError(null)
                triggerBuffering(true)
            } else if (playbackState == Player.STATE_READY) {
                triggerReady()
                triggerDuration(videoPlayer.duration)

                updatePositionJob?.cancel()
                updatePositionJob = coroutineScope.launch {
                    while (true) {
                        val livePosition =
                            System.currentTimeMillis() - videoPlayer.currentLiveOffset

                        triggerCurrentPosition(if (livePosition > 0) livePosition else videoPlayer.currentPosition)
                        delay(500)
                    }
                }
            }

            if (playbackState != Player.STATE_BUFFERING) {
                triggerBuffering(false)
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            triggerIsPlayingChanged(isPlaying)
        }
    }

    private val metadataListener = object : AnalyticsListener {
        override fun onVideoInputFormatChanged(
            eventTime: AnalyticsListener.EventTime,
            format: Format,
            decoderReuseEvaluation: DecoderReuseEvaluation?,
        ) {
            metadata = metadata.copy(
                video = (metadata.video ?: Metadata.Video()).copy(
                    width = format.width,
                    height = format.height,
                    color = format.colorInfo?.toLogString(),
                    frameRate = format.frameRate,
                    // TODO 帧率、比特率目前是从tag中获取，有的返回空，后续需要实时计算
                    bitrate = format.bitrate,
                    mimeType = format.sampleMimeType,
                )
            )

            triggerMetadata(metadata)
        }

        override fun onVideoDecoderInitialized(
            eventTime: AnalyticsListener.EventTime,
            decoderName: String,
            initializedTimestampMs: Long,
            initializationDurationMs: Long,
        ) {
            metadata = metadata.copy(
                video = (metadata.video ?: Metadata.Video()).copy(decoder = decoderName)
            )

            triggerMetadata(metadata)
        }

        override fun onAudioInputFormatChanged(
            eventTime: AnalyticsListener.EventTime,
            format: Format,
            decoderReuseEvaluation: DecoderReuseEvaluation?,
        ) {
            metadata = metadata.copy(
                audio = (metadata.audio ?: Metadata.Audio()).copy(
                    channels = format.channelCount,
                    sampleRate = format.sampleRate,
                    bitrate = format.bitrate,
                    mimeType = format.sampleMimeType,
                )
            )

            triggerMetadata(metadata)
        }

        override fun onAudioDecoderInitialized(
            eventTime: AnalyticsListener.EventTime,
            decoderName: String,
            initializedTimestampMs: Long,
            initializationDurationMs: Long,
        ) {
            metadata = metadata.copy(
                audio = (metadata.audio ?: Metadata.Audio()).copy(decoder = decoderName)
            )

            triggerMetadata(metadata)
        }
    }

    private val eventLogger = EventLogger()

    override fun initialize() {
        super.initialize()
        videoPlayer.addListener(playerListener)
        videoPlayer.addAnalyticsListener(metadataListener)
        videoPlayer.addAnalyticsListener(eventLogger)
    }

    override fun release() {
        videoPlayer.removeListener(playerListener)
        videoPlayer.removeAnalyticsListener(metadataListener)
        videoPlayer.removeAnalyticsListener(eventLogger)
        videoPlayer.stop()
        videoPlayer.release()
        super.release()
    }

    override fun prepare(line: ChannelLine) {
        contentTypeAttempts.clear()
        currentChannelLine = line
        prepare(null)
    }

    override fun play() {
        videoPlayer.play()
    }

    override fun pause() {
        videoPlayer.pause()
    }

    override fun seekTo(position: Long) {
        videoPlayer.seekTo(position)
    }

    override fun setVolume(volume: Float) {
        videoPlayer.volume = volume
    }

    override fun getVolume(): Float {
        return videoPlayer.volume
    }

    override fun stop() {
        videoPlayer.stop()
        updatePositionJob?.cancel()
        super.stop()
    }

    override fun setVideoSurfaceView(surfaceView: SurfaceView) {
        videoPlayer.setVideoSurfaceView(surfaceView)
    }

    override fun setVideoTextureView(textureView: TextureView) {
        videoPlayer.setVideoTextureView(textureView)
    }
}