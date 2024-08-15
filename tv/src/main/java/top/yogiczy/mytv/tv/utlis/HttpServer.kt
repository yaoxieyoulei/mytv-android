package top.yogiczy.mytv.tv.utlis

import android.content.Context
import com.koushikdutta.async.AsyncServer
import com.koushikdutta.async.http.body.JSONObjectBody
import com.koushikdutta.async.http.body.MultipartFormDataBody
import com.koushikdutta.async.http.server.AsyncHttpServer
import com.koushikdutta.async.http.server.AsyncHttpServerRequest
import com.koushikdutta.async.http.server.AsyncHttpServerResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.yogiczy.mytv.core.data.entities.epg.EpgProgrammeReserveList
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSource
import top.yogiczy.mytv.core.data.entities.epgsource.EpgSourceList
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSource
import top.yogiczy.mytv.core.data.entities.iptvsource.IptvSourceList
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.core.data.utils.Globals
import top.yogiczy.mytv.core.data.utils.Loggable
import top.yogiczy.mytv.core.data.utils.Logger
import top.yogiczy.mytv.core.util.utils.ApkInstaller
import top.yogiczy.mytv.tv.R
import top.yogiczy.mytv.tv.ui.material.Snackbar
import top.yogiczy.mytv.tv.ui.material.SnackbarType
import top.yogiczy.mytv.tv.ui.screens.videoplayer.VideoPlayerDisplayMode
import top.yogiczy.mytv.tv.ui.utils.Configs
import java.io.File
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

object HttpServer : Loggable() {
    private const val SERVER_PORT = 10481
    private val uploadedApkFile = File(Globals.cacheDir, "uploaded_apk.apk")
        .apply { deleteOnExit() }

    val serverUrl: String by lazy { "http://${getLocalIpAddress()}:${SERVER_PORT}" }

    fun start(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val server = AsyncHttpServer()
                server.listen(AsyncServer.getDefault(), SERVER_PORT)

                server.get("/") { _, response ->
                    handleRawResource(response, context, "text/html", R.raw.web_push)
                }
                server.get("/web_push_css.css") { _, response ->
                    handleRawResource(response, context, "text/css", R.raw.web_push_css)
                }
                server.get("/web_push_js.js") { _, response ->
                    handleRawResource(response, context, "text/javascript", R.raw.web_push_js)
                }

                server.get("/api/info") { _, response ->
                    handleGetInfo(response)
                }

                server.post("/api/iptv-source/push") { request, response ->
                    handleIptvSourcePush(request, response)
                }

                server.post("/api/epg-source/push") { request, response ->
                    handleEpgSourcePush(request, response)
                }

                server.post("/api/video-player-user-agent/push") { request, response ->
                    handleVideoPlayerUserAgentPush(request, response)
                }

                server.get("/api/configs") { _, response ->
                    handleConfigsGet(response)
                }

                server.post("/api/configs") { request, response ->
                    handleConfigsPush(request, response)
                }

                server.post("/api/upload/apk") { request, response ->
                    handleUploadApk(request, response, context)
                }

                log.i("设置服务已启动: $serverUrl")
            } catch (ex: Exception) {
                log.e("设置服务启动失败: ${ex.message}", ex)
                launch(Dispatchers.Main) {
                    Snackbar.show("设置服务启动失败", type = SnackbarType.ERROR)
                }
            }
        }
    }

    private fun wrapResponse(response: AsyncHttpServerResponse) = response.apply {
        headers.set("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS")
        headers.set("Access-Control-Allow-Origin", "*")
        headers.set("Access-Control-Allow-Headers", "Origin, Content-Type, X-Auth-Token")
    }

    private fun handleRawResource(
        response: AsyncHttpServerResponse,
        context: Context,
        contentType: String,
        id: Int,
    ) {
        wrapResponse(response).apply {
            setContentType(contentType)
            send(context.resources.openRawResource(id).readBytes().decodeToString())
        }
    }

    private fun handleGetInfo(response: AsyncHttpServerResponse) {
        wrapResponse(response).apply {
            setContentType("application/json")
            send(
                Json.encodeToString(
                    AppInfo(
                        appTitle = Constants.APP_TITLE,
                        appRepo = Constants.APP_REPO,
                        logHistory = Logger.history,
                    )
                )
            )
        }
    }

    private fun handleIptvSourcePush(
        request: AsyncHttpServerRequest,
        response: AsyncHttpServerResponse,
    ) {
        val body = request.getBody<JSONObjectBody>().get()
        val name = body.get("name").toString()
        val type = body.get("type").toString()
        val url = body.get("url").toString()
        val filePath = body.get("filePath").toString()
        val content = body.get("content").toString()

        var newIptvSource: IptvSource? = null

        when (type) {
            "url" -> {
                newIptvSource = IptvSource(name, url)
            }

            "file" -> {
                newIptvSource = IptvSource(name, filePath, true)
            }

            "content" -> {
                val file =
                    File(Globals.cacheDir, "iptv-${System.currentTimeMillis()}.txt")
                file.writeText(content)
                newIptvSource = IptvSource(name, file.path, true)
            }
        }

        newIptvSource?.let {
            Configs.iptvSourceList = IptvSourceList(Configs.iptvSourceList + it)
        }

        wrapResponse(response).send("success")
    }

    private fun handleEpgSourcePush(
        request: AsyncHttpServerRequest,
        response: AsyncHttpServerResponse,
    ) {
        val body = request.getBody<JSONObjectBody>().get()
        val name = body.get("name").toString()
        val url = body.get("url").toString()

        Configs.epgSourceList = EpgSourceList(Configs.epgSourceList.toMutableList().apply {
            add(EpgSource(name, url))
        })

        wrapResponse(response).send("success")
    }

    private fun handleVideoPlayerUserAgentPush(
        request: AsyncHttpServerRequest,
        response: AsyncHttpServerResponse,
    ) {
        val body = request.getBody<JSONObjectBody>().get()
        val ua = body.get("ua").toString()

        Configs.videoPlayerUserAgent = ua

        wrapResponse(response).send("success")
    }

    private fun handleConfigsGet(response: AsyncHttpServerResponse) {
        wrapResponse(response).apply {
            setContentType("application/json")
            val json = Json { encodeDefaults = true }
            send(
                json.encodeToString(
                    AllSettings(
                        appBootLaunch = Configs.appBootLaunch,
                        appLastLatestVersion = Configs.appLastLatestVersion,
                        appAgreementAgreed = Configs.appAgreementAgreed,
                        debugShowFps = Configs.debugShowFps,
                        debugShowVideoPlayerMetadata = Configs.debugShowVideoPlayerMetadata,
                        debugShowLayoutGrids = Configs.debugShowLayoutGrids,
                        iptvLastChannelIdx = Configs.iptvLastChannelIdx,
                        iptvChannelChangeFlip = Configs.iptvChannelChangeFlip,
                        iptvSourceCurrent = Configs.iptvSourceCurrent,
                        iptvSourceList = Configs.iptvSourceList,
                        iptvSourceCacheTime = Configs.iptvSourceCacheTime,
                        iptvPlayableHostList = Configs.iptvPlayableHostList,
                        iptvChannelNoSelectEnable = Configs.iptvChannelNoSelectEnable,
                        iptvChannelFavoriteEnable = Configs.iptvChannelFavoriteEnable,
                        iptvChannelFavoriteListVisible = Configs.iptvChannelFavoriteListVisible,
                        iptvChannelFavoriteList = Configs.iptvChannelFavoriteList,
                        iptvChannelFavoriteChangeBoundaryJumpOut = Configs.iptvChannelFavoriteChangeBoundaryJumpOut,
                        iptvChannelGroupHiddenList = Configs.iptvChannelGroupHiddenList,
                        iptvHybridMode = Configs.iptvHybridMode,
                        epgEnable = Configs.epgEnable,
                        epgSourceCurrent = Configs.epgSourceCurrent,
                        epgSourceList = Configs.epgSourceList,
                        epgRefreshTimeThreshold = Configs.epgRefreshTimeThreshold,
                        epgChannelReserveList = Configs.epgChannelReserveList,
                        uiShowEpgProgrammeProgress = Configs.uiShowEpgProgrammeProgress,
                        uiShowEpgProgrammePermanentProgress = Configs.uiShowEpgProgrammePermanentProgress,
                        uiShowChannelLogo = Configs.uiShowChannelLogo,
                        uiUseClassicPanelScreen = Configs.uiUseClassicPanelScreen,
                        uiDensityScaleRatio = Configs.uiDensityScaleRatio,
                        uiFontScaleRatio = Configs.uiFontScaleRatio,
                        uiTimeShowMode = Configs.uiTimeShowMode,
                        uiFocusOptimize = Configs.uiFocusOptimize,
                        uiScreenAutoCloseDelay = Configs.uiScreenAutoCloseDelay,
                        updateForceRemind = Configs.updateForceRemind,
                        updateChannel = Configs.updateChannel,
                        videoPlayerUserAgent = Configs.videoPlayerUserAgent,
                        videoPlayerLoadTimeout = Configs.videoPlayerLoadTimeout,
                        videoPlayerDisplayMode = Configs.videoPlayerDisplayMode,
                    )
                )
            )
        }
    }

    private fun handleConfigsPush(
        request: AsyncHttpServerRequest,
        response: AsyncHttpServerResponse,
    ) {
        val body = request.getBody<JSONObjectBody>().get()
        val configs = Json.decodeFromString<AllSettings>(body.toString())

        Configs.appBootLaunch = configs.appBootLaunch
        Configs.appLastLatestVersion = configs.appLastLatestVersion
        Configs.appAgreementAgreed = configs.appAgreementAgreed
        Configs.debugShowFps = configs.debugShowFps
        Configs.debugShowVideoPlayerMetadata = configs.debugShowVideoPlayerMetadata
        Configs.debugShowLayoutGrids = configs.debugShowLayoutGrids
        Configs.iptvLastChannelIdx = configs.iptvLastChannelIdx
        Configs.iptvChannelChangeFlip = configs.iptvChannelChangeFlip
        Configs.iptvSourceCurrent = configs.iptvSourceCurrent
        Configs.iptvSourceList = configs.iptvSourceList
        Configs.iptvSourceCacheTime = configs.iptvSourceCacheTime
        Configs.iptvPlayableHostList = configs.iptvPlayableHostList
        Configs.iptvChannelNoSelectEnable = configs.iptvChannelNoSelectEnable
        Configs.iptvChannelFavoriteEnable = configs.iptvChannelFavoriteEnable
        Configs.iptvChannelFavoriteListVisible = configs.iptvChannelFavoriteListVisible
        Configs.iptvChannelFavoriteList = configs.iptvChannelFavoriteList
        Configs.iptvChannelFavoriteChangeBoundaryJumpOut =
            configs.iptvChannelFavoriteChangeBoundaryJumpOut
        Configs.iptvChannelGroupHiddenList = configs.iptvChannelGroupHiddenList
        Configs.iptvHybridMode = configs.iptvHybridMode
        Configs.epgEnable = configs.epgEnable
        Configs.epgSourceCurrent = configs.epgSourceCurrent
        Configs.epgSourceList = configs.epgSourceList
        Configs.epgRefreshTimeThreshold = configs.epgRefreshTimeThreshold
        Configs.epgChannelReserveList = configs.epgChannelReserveList
        Configs.uiShowEpgProgrammeProgress = configs.uiShowEpgProgrammeProgress
        Configs.uiShowEpgProgrammePermanentProgress = configs.uiShowEpgProgrammePermanentProgress
        Configs.uiShowChannelLogo = configs.uiShowChannelLogo
        Configs.uiUseClassicPanelScreen = configs.uiUseClassicPanelScreen
        Configs.uiDensityScaleRatio = configs.uiDensityScaleRatio
        Configs.uiFontScaleRatio = configs.uiFontScaleRatio
        Configs.uiTimeShowMode = configs.uiTimeShowMode
        Configs.uiFocusOptimize = configs.uiFocusOptimize
        Configs.uiScreenAutoCloseDelay = configs.uiScreenAutoCloseDelay
        Configs.updateForceRemind = configs.updateForceRemind
        Configs.updateChannel = configs.updateChannel
        Configs.videoPlayerUserAgent = configs.videoPlayerUserAgent
        Configs.videoPlayerLoadTimeout = configs.videoPlayerLoadTimeout
        Configs.videoPlayerDisplayMode = configs.videoPlayerDisplayMode

        wrapResponse(response).send("success")
    }

    private fun handleUploadApk(
        request: AsyncHttpServerRequest,
        response: AsyncHttpServerResponse,
        context: Context,
    ) {
        val body = request.getBody<MultipartFormDataBody>()

        val os = uploadedApkFile.outputStream()
        val contentLength = request.headers["Content-Length"]?.toLong() ?: 1
        var hasReceived = 0L

        body.setMultipartCallback { part ->
            if (part.isFile) {
                body.setDataCallback { _, bb ->
                    val byteArray = bb.allByteArray
                    hasReceived += byteArray.size
                    Snackbar.show(
                        "正在接收文件: ${(hasReceived * 100f / contentLength).toInt()}%",
                        leadingLoading = true,
                        id = "uploading_apk",
                    )
                    os.write(byteArray)
                }
            }
        }

        body.setEndCallback {
            Snackbar.show("文件接收完成")
            body.dataEmitter.close()
            os.flush()
            os.close()
            ApkInstaller.installApk(context, uploadedApkFile.path)
        }

        wrapResponse(response).send("success")
    }

    private fun getLocalIpAddress(): String {
        val defaultIp = "0.0.0.0"

        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.hostAddress ?: defaultIp
                    }
                }
            }
            return defaultIp
        } catch (ex: SocketException) {
            log.e("IP Address: ${ex.message}", ex)
            return defaultIp
        }
    }
}

@Serializable
private data class AppInfo(
    val appTitle: String,
    val appRepo: String,
    val logHistory: List<Logger.HistoryItem>,
)

@Serializable
private data class AllSettings(
    val appBootLaunch: Boolean = false,
    val appLastLatestVersion: String = "",
    val appAgreementAgreed: Boolean = false,
    val debugShowFps: Boolean = false,
    val debugShowVideoPlayerMetadata: Boolean = false,
    val debugShowLayoutGrids: Boolean = false,
    val iptvLastChannelIdx: Int = 0,
    val iptvChannelChangeFlip: Boolean = false,
    val iptvSourceCurrent: IptvSource = IptvSource(),
    val iptvSourceList: IptvSourceList = IptvSourceList(),
    val iptvSourceCacheTime: Long = 0,
    val iptvPlayableHostList: Set<String> = emptySet(),
    val iptvChannelNoSelectEnable: Boolean = false,
    val iptvChannelFavoriteEnable: Boolean = false,
    val iptvChannelFavoriteListVisible: Boolean = false,
    val iptvChannelFavoriteList: Set<String> = emptySet(),
    val iptvChannelFavoriteChangeBoundaryJumpOut: Boolean = false,
    val iptvChannelGroupHiddenList: Set<String> = emptySet(),
    val iptvHybridMode: Configs.IptvHybridMode = Configs.IptvHybridMode.DISABLE,
    val epgEnable: Boolean = false,
    val epgSourceCurrent: EpgSource = EpgSource(),
    val epgSourceList: EpgSourceList = EpgSourceList(),
    val epgRefreshTimeThreshold: Int = 0,
    val epgChannelReserveList: EpgProgrammeReserveList = EpgProgrammeReserveList(),
    val uiShowEpgProgrammeProgress: Boolean = false,
    val uiShowEpgProgrammePermanentProgress: Boolean = false,
    val uiShowChannelLogo: Boolean = false,
    val uiUseClassicPanelScreen: Boolean = false,
    val uiDensityScaleRatio: Float = 0f,
    val uiFontScaleRatio: Float = 1f,
    val uiTimeShowMode: Configs.UiTimeShowMode = Configs.UiTimeShowMode.HIDDEN,
    val uiFocusOptimize: Boolean = false,
    val uiScreenAutoCloseDelay: Long = 0,
    val updateForceRemind: Boolean = false,
    val updateChannel: String = "",
    val videoPlayerUserAgent: String = "",
    val videoPlayerLoadTimeout: Long = 0,
    val videoPlayerDisplayMode: VideoPlayerDisplayMode = VideoPlayerDisplayMode.ORIGINAL,
)