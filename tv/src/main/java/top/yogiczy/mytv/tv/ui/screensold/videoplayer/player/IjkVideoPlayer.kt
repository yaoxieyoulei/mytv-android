package top.yogiczy.mytv.tv.ui.screensold.videoplayer.player

import android.content.Context
import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.SurfaceView
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yogiczy.mytv.core.data.entities.channel.ChannelLine
import top.yogiczy.mytv.core.util.utils.toHeaders
import top.yogiczy.mytv.tv.ui.utils.Configs
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaMeta
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.File


class IjkVideoPlayer(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
) : VideoPlayer(coroutineScope),
    IMediaPlayer.OnPreparedListener,
    IMediaPlayer.OnVideoSizeChangedListener,
    IMediaPlayer.OnErrorListener,
    IMediaPlayer.OnInfoListener {

    private val player by lazy {
        IjkMediaPlayer().apply {
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_timeout", 0)
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0)
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1)
            setOption(
                IjkMediaPlayer.OPT_CATEGORY_FORMAT,
                "timeout",
                Configs.videoPlayerLoadTimeout
            )
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L)
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek")
        }
    }
    private var cacheSurfaceView: SurfaceView? = null
    private var cacheSurfaceTexture: Surface? = null
    private var updateJob: Job? = null
    private var av3aModel = File(context.cacheDir, "model.bin")

    init {
        if (!av3aModel.exists()) {
            runCatching {
                context.assets.open("model.bin").use { inputStream ->
                    av3aModel.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        }
    }

    private fun setOption() {
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-all-videos", 1)
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 1)
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1)
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1)
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1)
        player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
        player.setOption(
            IjkMediaPlayer.OPT_CATEGORY_PLAYER,
            "av3a_model_path",
            av3aModel.absolutePath
        )
    }

    override fun prepare(line: ChannelLine) {
        player.reset()
        player.setDataSource(
            line.url,
            Configs.videoPlayerHeaders.toHeaders() + mapOf(
                "User-Agent" to (line.httpUserAgent ?: Configs.videoPlayerUserAgent),
            )
        )
        setOption()
        player.prepareAsync()

        triggerPrepared()
    }

    override fun play() {
        player.start()
    }

    override fun pause() {
        player.pause()
    }

    override fun seekTo(position: Long) {
        player.seekTo(position)
    }

    override fun setVolume(volume: Float) {
    }

    override fun getVolume(): Float {
        return 1f
    }

    override fun stop() {
        player.stop()
        updateJob?.cancel()
        super.stop()
    }

    override fun setVideoSurfaceView(surfaceView: SurfaceView) {
        cacheSurfaceView = surfaceView
        cacheSurfaceTexture?.release()
        cacheSurfaceTexture = null
    }

    override fun setVideoTextureView(textureView: TextureView) {
        cacheSurfaceView = null
        textureView.surfaceTextureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                cacheSurfaceTexture = Surface(surfaceTexture)
                player.setSurface(cacheSurfaceTexture)
            }

            override fun onSurfaceTextureSizeChanged(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                return true
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
            }
        }
    }

    override fun initialize() {
        super.initialize()
        player.setOnPreparedListener(this)
        player.setOnVideoSizeChangedListener(this)
        player.setOnErrorListener(this)
        player.setOnInfoListener(this)
    }

    override fun release() {
        player.setOnPreparedListener(null)
        player.setOnVideoSizeChangedListener(null)
        player.setOnErrorListener(null)
        player.setOnInfoListener(null)
        player.stop()
        player.release()
        cacheSurfaceTexture?.release()
        super.release()
    }

    override fun onPrepared(player: IMediaPlayer) {
        cacheSurfaceView?.let { player.setDisplay(it.holder) }
        cacheSurfaceTexture?.let { player.setSurface(it) }

        val info = player.mediaInfo
        metadata = Metadata(
            videoDecoder = info.mVideoDecoderImpl ?: "",
            videoMimeType = info.mMeta.mVideoStream?.mCodecName ?: "",
            videoWidth = info.mMeta.mVideoStream?.mWidth ?: 0,
            videoHeight = info.mMeta.mVideoStream?.mHeight ?: 0,
            videoFrameRate = info.mMeta.mVideoStream?.mFpsNum?.toFloat() ?: 0f,
            videoBitrate = info.mMeta.mVideoStream?.mBitrate?.toInt() ?: 0,
            audioMimeType = info.mMeta.mAudioStream?.mCodecName ?: "",
            audioDecoder = info.mAudioDecoderImpl ?: "",
            audioSampleRate = info.mMeta.mAudioStream?.mSampleRate ?: 0,
            audioChannels = when (info.mMeta.mAudioStream?.mChannelLayout) {
                IjkMediaMeta.AV_CH_LAYOUT_MONO -> 1
                IjkMediaMeta.AV_CH_LAYOUT_STEREO,
                IjkMediaMeta.AV_CH_LAYOUT_2POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_STEREO_DOWNMIX -> 2

                IjkMediaMeta.AV_CH_LAYOUT_2_1,
                IjkMediaMeta.AV_CH_LAYOUT_SURROUND -> 3

                IjkMediaMeta.AV_CH_LAYOUT_3POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_4POINT0,
                IjkMediaMeta.AV_CH_LAYOUT_2_2,
                IjkMediaMeta.AV_CH_LAYOUT_QUAD -> 4

                IjkMediaMeta.AV_CH_LAYOUT_4POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_5POINT0 -> 5

                IjkMediaMeta.AV_CH_LAYOUT_HEXAGONAL,
                IjkMediaMeta.AV_CH_LAYOUT_5POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_6POINT0 -> 6

                IjkMediaMeta.AV_CH_LAYOUT_6POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_7POINT0 -> 7

                IjkMediaMeta.AV_CH_LAYOUT_7POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_7POINT1_WIDE,
                IjkMediaMeta.AV_CH_LAYOUT_7POINT1_WIDE_BACK,
                IjkMediaMeta.AV_CH_LAYOUT_OCTAGONAL -> 8

                IjkMediaMeta.AV_CH_AV3A_LAYOUT_5POINT1POINT4 -> 10
                else -> 0
            },
            audioChannelsLabel = when (info.mMeta.mAudioStream?.mChannelLayout) {
                IjkMediaMeta.AV_CH_LAYOUT_MONO -> "单声道"
                IjkMediaMeta.AV_CH_LAYOUT_STEREO -> "立体声"
                IjkMediaMeta.AV_CH_LAYOUT_2POINT1 -> "2.1 声道"
                IjkMediaMeta.AV_CH_LAYOUT_2_1 -> "立体声"
                IjkMediaMeta.AV_CH_LAYOUT_SURROUND -> "环绕声"
                IjkMediaMeta.AV_CH_LAYOUT_3POINT1 -> "3.1 环绕声"
                IjkMediaMeta.AV_CH_LAYOUT_4POINT0 -> "4.0 四声道"
                IjkMediaMeta.AV_CH_LAYOUT_4POINT1 -> "4.1 环绕声"
                IjkMediaMeta.AV_CH_LAYOUT_2_2 -> "四声道"
                IjkMediaMeta.AV_CH_LAYOUT_QUAD -> "四声道"
                IjkMediaMeta.AV_CH_LAYOUT_5POINT0 -> "5.0 环绕声"
                IjkMediaMeta.AV_CH_LAYOUT_5POINT1 -> "5.1 环绕声"
                IjkMediaMeta.AV_CH_LAYOUT_6POINT0 -> "6.0 环绕声"
                IjkMediaMeta.AV_CH_LAYOUT_6POINT1 -> "6.1 环绕声"
                IjkMediaMeta.AV_CH_LAYOUT_7POINT0 -> "7.0 环绕声"
                IjkMediaMeta.AV_CH_LAYOUT_7POINT1 -> "7.1 环绕声"
                IjkMediaMeta.AV_CH_LAYOUT_7POINT1_WIDE -> "宽域 7.1 环绕声"
                IjkMediaMeta.AV_CH_LAYOUT_7POINT1_WIDE_BACK -> "后置 7.1 环绕声"
                IjkMediaMeta.AV_CH_LAYOUT_HEXAGONAL -> "六角环绕声"
                IjkMediaMeta.AV_CH_LAYOUT_OCTAGONAL -> "八角环绕声"
                IjkMediaMeta.AV_CH_LAYOUT_STEREO_DOWNMIX -> "立体声下混音"
                IjkMediaMeta.AV_CH_AV3A_LAYOUT_5POINT1POINT4 -> "三维菁彩声"
                else -> null
            },
            audioBitrate = info.mMeta.mAudioStream?.mBitrate?.toInt() ?: 0,
        )

        triggerMetadata(metadata)
        triggerReady()
        triggerDuration(player.duration)

        updateJob?.cancel()
        updateJob = coroutineScope.launch {
            while (true) {
                triggerIsPlayingChanged(player.isPlaying)
                triggerCurrentPosition(player.currentPosition)
                delay(1000)
            }
        }
    }

    override fun onError(player: IMediaPlayer, what: Int, extra: Int): Boolean {
        triggerError(PlaybackException("IJK_PLAYER_ERROR_$what: $extra", what))
        return true
    }

    override fun onVideoSizeChanged(
        player: IMediaPlayer,
        width: Int,
        height: Int,
        sarNum: Int,
        sarDen: Int
    ) {
        triggerResolution(width, height)
    }

    override fun onInfo(mp: IMediaPlayer?, what: Int, extra: Int): Boolean {
        if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            triggerBuffering(true)
        } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            triggerBuffering(false)
        }

        return true
    }
}