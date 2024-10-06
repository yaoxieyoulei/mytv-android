package top.yogiczy.mytv.tv.ui.screensold.videoplayer.player

import android.view.SurfaceView
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


class IjkVideoPlayer(
    private val coroutineScope: CoroutineScope,
) : VideoPlayer(coroutineScope),
    IMediaPlayer.OnPreparedListener,
    IMediaPlayer.OnCompletionListener,
    IMediaPlayer.OnBufferingUpdateListener,
    IMediaPlayer.OnSeekCompleteListener,
    IMediaPlayer.OnVideoSizeChangedListener,
    IMediaPlayer.OnErrorListener,
    IMediaPlayer.OnInfoListener {
    private val player by lazy {
        IjkMediaPlayer().apply {
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_timeout", 0)
            setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-all-videos", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 5000)
            setOption(
                IjkMediaPlayer.OPT_CATEGORY_FORMAT,
                "timeout",
                Configs.videoPlayerLoadTimeout * 1000L
            )
        }
    }

    private var cacheSurfaceView: SurfaceView? = null
    private var updateJob: Job? = null

    override fun prepare(line: ChannelLine) {
        player.reset()
        player.setDataSource(
            line.url,
            Configs.videoPlayerHeaders.toHeaders() + mapOf(
                "User-Agent" to (line.httpUserAgent ?: Configs.videoPlayerUserAgent),
            )
        )
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
    }

    override fun initialize() {
        super.initialize()
        player.setOnPreparedListener(this)
        player.setOnCompletionListener(this)
        player.setOnBufferingUpdateListener(this)
        player.setOnSeekCompleteListener(this)
        player.setOnVideoSizeChangedListener(this)
        player.setOnErrorListener(this)
        player.setOnInfoListener(this)
    }

    override fun release() {
        player.setOnPreparedListener(null)
        player.setOnCompletionListener(null)
        player.setOnBufferingUpdateListener(null)
        player.setOnSeekCompleteListener(null)
        player.setOnVideoSizeChangedListener(null)
        player.setOnErrorListener(null)
        player.setOnInfoListener(null)
        player.release()
        super.release()
    }

    override fun onPrepared(player: IMediaPlayer) {
        cacheSurfaceView?.let { player.setDisplay(it.holder) }

        val info = player.mediaInfo
        metadata = Metadata(
            videoDecoder = info.mVideoDecoderImpl,
            videoMimeType = info.mMeta.mVideoStream.mCodecName,
            videoWidth = info.mMeta.mVideoStream.mWidth,
            videoHeight = info.mMeta.mVideoStream.mHeight,
            videoFrameRate = info.mMeta.mVideoStream.mFpsNum.toFloat(),
            videoBitrate = info.mMeta.mVideoStream.mBitrate.toInt(),
            audioMimeType = info.mMeta.mAudioStream.mCodecName,
            audioDecoder = info.mAudioDecoderImpl,
            audioSampleRate = info.mMeta.mAudioStream.mSampleRate,
            audioChannels = when (info.mMeta.mAudioStream.mChannelLayout) {
                IjkMediaMeta.AV_CH_LAYOUT_MONO -> 1
                IjkMediaMeta.AV_CH_LAYOUT_STEREO,
                IjkMediaMeta.AV_CH_LAYOUT_2POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_STEREO_DOWNMIX -> 2

                IjkMediaMeta.AV_CH_LAYOUT_2_1,
                IjkMediaMeta.AV_CH_LAYOUT_SURROUND,
                IjkMediaMeta.AV_CH_LAYOUT_3POINT1 -> 3

                IjkMediaMeta.AV_CH_LAYOUT_4POINT0,
                IjkMediaMeta.AV_CH_LAYOUT_4POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_2_2,
                IjkMediaMeta.AV_CH_LAYOUT_QUAD -> 4

                IjkMediaMeta.AV_CH_LAYOUT_5POINT0,
                IjkMediaMeta.AV_CH_LAYOUT_5POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_5POINT0_BACK,
                IjkMediaMeta.AV_CH_LAYOUT_5POINT1_BACK -> 5

                IjkMediaMeta.AV_CH_LAYOUT_6POINT0,
                IjkMediaMeta.AV_CH_LAYOUT_6POINT0_FRONT,
                IjkMediaMeta.AV_CH_LAYOUT_HEXAGONAL,
                IjkMediaMeta.AV_CH_LAYOUT_6POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_6POINT1_BACK,
                IjkMediaMeta.AV_CH_LAYOUT_6POINT1_FRONT -> 6

                IjkMediaMeta.AV_CH_LAYOUT_7POINT0,
                IjkMediaMeta.AV_CH_LAYOUT_7POINT0_FRONT,
                IjkMediaMeta.AV_CH_LAYOUT_7POINT1,
                IjkMediaMeta.AV_CH_LAYOUT_7POINT1_WIDE,
                IjkMediaMeta.AV_CH_LAYOUT_7POINT1_WIDE_BACK -> 7

                IjkMediaMeta.AV_CH_LAYOUT_OCTAGONAL -> 8
                else -> 0
            },
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

    override fun onCompletion(player: IMediaPlayer) {

    }

    override fun onError(player: IMediaPlayer, what: Int, extra: Int): Boolean {
        triggerError(PlaybackException("IJK_ERROR_$what: $extra", what))
        return true
    }

    override fun onBufferingUpdate(player: IMediaPlayer, percent: Int) {

    }

    override fun onSeekComplete(player: IMediaPlayer) {

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

    override fun onInfo(player: IMediaPlayer, what: Int, extra: Int): Boolean {
        return true
    }
}