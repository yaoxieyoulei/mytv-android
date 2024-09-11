package top.yogiczy.mytv.tv.utlis

import android.content.Context
import com.koushikdutta.async.AsyncServer
import com.koushikdutta.async.http.body.JSONObjectBody
import com.koushikdutta.async.http.body.MultipartFormDataBody
import com.koushikdutta.async.http.body.StringBody
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
import top.yogiczy.mytv.core.data.utils.ChannelAlias
import top.yogiczy.mytv.core.data.utils.Constants
import top.yogiczy.mytv.core.data.utils.Globals
import top.yogiczy.mytv.core.data.utils.Loggable
import top.yogiczy.mytv.core.data.utils.Logger
import top.yogiczy.mytv.core.util.utils.ApkInstaller
import top.yogiczy.mytv.tv.R
import top.yogiczy.mytv.tv.ui.material.Snackbar
import top.yogiczy.mytv.tv.ui.material.SnackbarType
import top.yogiczy.mytv.tv.ui.screen.components.AppThemeDef
import top.yogiczy.mytv.tv.ui.screensold.videoplayer.VideoPlayerDisplayMode
import top.yogiczy.mytv.tv.ui.utils.Configs
import java.io.File
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

object HttpServer : Loggable() {
    private const val SERVER_PORT = 10481
    private val uploadedApkFile by lazy {
        File(Globals.cacheDir, "uploaded_apk.apk").apply { deleteOnExit() }
    }

    val serverUrl by lazy { "http://${getLocalIpAddress()}:${SERVER_PORT}" }

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

                server.get("/api/channel-alias") { _, response ->
                    handleGetChannelAlias(response)
                }

                server.post("/api/channel-alias") { request, response ->
                    handleUpdateChannelAlias(request, response)
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
                val file = File(Globals.cacheDir, "iptv-${System.currentTimeMillis()}.txt")
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

    private fun handleGetChannelAlias(response: AsyncHttpServerResponse) {
        wrapResponse(response).apply {
            setContentType("application/json")
            send(runCatching { ChannelAlias.aliasFile.readText() }.getOrElse { "" })
        }
    }

    private fun handleUpdateChannelAlias(
        request: AsyncHttpServerRequest,
        response: AsyncHttpServerResponse,
    ) {
        val alias = request.getBody<StringBody>().get()

        ChannelAlias.aliasFile.writeText(alias)

        wrapResponse(response).send("success")
    }

    private fun handleConfigsGet(response: AsyncHttpServerResponse) {
        wrapResponse(response).apply {
            setContentType("application/json")
            val json = Json { encodeDefaults = true }
            send(
                json.encodeToString(
                    AllConfigs(
                        appBootLaunch = Configs.appBootLaunch,
                        appLastLatestVersion = Configs.appLastLatestVersion,
                        appAgreementAgreed = Configs.appAgreementAgreed,
                        debugShowFps = Configs.debugShowFps,
                        debugShowVideoPlayerMetadata = Configs.debugShowVideoPlayerMetadata,
                        debugShowLayoutGrids = Configs.debugShowLayoutGrids,
                        iptvLastChannelIdx = Configs.iptvLastChannelIdx,
                        iptvChannelChangeFlip = Configs.iptvChannelChangeFlip,
                        iptvSourceCacheTime = Configs.iptvSourceCacheTime,
                        iptvSourceCurrent = Configs.iptvSourceCurrent,
                        iptvSourceList = Configs.iptvSourceList,
                        iptvPlayableHostList = Configs.iptvPlayableHostList,
                        iptvChannelNoSelectEnable = Configs.iptvChannelNoSelectEnable,
                        iptvChannelFavoriteEnable = Configs.iptvChannelFavoriteEnable,
                        iptvChannelFavoriteListVisible = Configs.iptvChannelFavoriteListVisible,
                        iptvChannelFavoriteList = Configs.iptvChannelFavoriteList,
                        iptvChannelFavoriteChangeBoundaryJumpOut = Configs.iptvChannelFavoriteChangeBoundaryJumpOut,
                        iptvChannelGroupHiddenList = Configs.iptvChannelGroupHiddenList,
                        iptvHybridMode = Configs.iptvHybridMode,
                        iptvSimilarChannelMerge = Configs.iptvSimilarChannelMerge,
                        iptvChannelLogoProvider = Configs.iptvChannelLogoProvider,
                        iptvChannelLogoOverride = Configs.iptvChannelLogoOverride,
                        epgEnable = Configs.epgEnable,
                        epgSourceCurrent = Configs.epgSourceCurrent,
                        epgSourceList = Configs.epgSourceList,
                        epgRefreshTimeThreshold = Configs.epgRefreshTimeThreshold,
                        epgChannelReserveList = Configs.epgChannelReserveList,
                        uiShowEpgProgrammeProgress = Configs.uiShowEpgProgrammeProgress,
                        uiShowEpgProgrammePermanentProgress = Configs.uiShowEpgProgrammePermanentProgress,
                        uiShowChannelLogo = Configs.uiShowChannelLogo,
                        uiShowChannelPreview = Configs.uiShowChannelPreview,
                        uiUseClassicPanelScreen = Configs.uiUseClassicPanelScreen,
                        uiDensityScaleRatio = Configs.uiDensityScaleRatio,
                        uiFontScaleRatio = Configs.uiFontScaleRatio,
                        uiTimeShowMode = Configs.uiTimeShowMode,
                        uiFocusOptimize = Configs.uiFocusOptimize,
                        uiScreenAutoCloseDelay = Configs.uiScreenAutoCloseDelay,
                        updateForceRemind = Configs.updateForceRemind,
                        updateChannel = Configs.updateChannel,
                        videoPlayerUserAgent = Configs.videoPlayerUserAgent,
                        videoPlayerHeaders = Configs.videoPlayerHeaders,
                        videoPlayerLoadTimeout = Configs.videoPlayerLoadTimeout,
                        videoPlayerDisplayMode = Configs.videoPlayerDisplayMode,
                        themeAppCurrent = Configs.themeAppCurrent,
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
        val configs = Json.decodeFromString<AllConfigs>(body.toString())

        if (configs.appBootLaunch != null)
            Configs.appBootLaunch = configs.appBootLaunch
        if (configs.appLastLatestVersion != null)
            Configs.appLastLatestVersion = configs.appLastLatestVersion
        if (configs.appAgreementAgreed != null)
            Configs.appAgreementAgreed = configs.appAgreementAgreed
        if (configs.debugShowFps != null)
            Configs.debugShowFps = configs.debugShowFps
        if (configs.debugShowVideoPlayerMetadata != null)
            Configs.debugShowVideoPlayerMetadata = configs.debugShowVideoPlayerMetadata
        if (configs.debugShowLayoutGrids != null)
            Configs.debugShowLayoutGrids = configs.debugShowLayoutGrids
        if (configs.iptvLastChannelIdx != null)
            Configs.iptvLastChannelIdx = configs.iptvLastChannelIdx
        if (configs.iptvChannelChangeFlip != null)
            Configs.iptvChannelChangeFlip = configs.iptvChannelChangeFlip
        if (configs.iptvSourceCacheTime != null)
            Configs.iptvSourceCacheTime = configs.iptvSourceCacheTime
        if (configs.iptvSourceCurrent != null)
            Configs.iptvSourceCurrent = configs.iptvSourceCurrent
        if (configs.iptvSourceList != null)
            Configs.iptvSourceList = configs.iptvSourceList
        if (configs.iptvPlayableHostList != null)
            Configs.iptvPlayableHostList = configs.iptvPlayableHostList
        if (configs.iptvChannelNoSelectEnable != null)
            Configs.iptvChannelNoSelectEnable = configs.iptvChannelNoSelectEnable
        if (configs.iptvChannelFavoriteEnable != null)
            Configs.iptvChannelFavoriteEnable = configs.iptvChannelFavoriteEnable
        if (configs.iptvChannelFavoriteListVisible != null)
            Configs.iptvChannelFavoriteListVisible = configs.iptvChannelFavoriteListVisible
        if (configs.iptvChannelFavoriteList != null)
            Configs.iptvChannelFavoriteList = configs.iptvChannelFavoriteList
        if (configs.iptvChannelFavoriteChangeBoundaryJumpOut != null)
            Configs.iptvChannelFavoriteChangeBoundaryJumpOut =
                configs.iptvChannelFavoriteChangeBoundaryJumpOut
        if (configs.iptvChannelGroupHiddenList != null)
            Configs.iptvChannelGroupHiddenList = configs.iptvChannelGroupHiddenList
        if (configs.iptvHybridMode != null)
            Configs.iptvHybridMode = configs.iptvHybridMode
        if (configs.iptvSimilarChannelMerge != null)
            Configs.iptvSimilarChannelMerge = configs.iptvSimilarChannelMerge
        if (configs.iptvChannelLogoProvider != null)
            Configs.iptvChannelLogoProvider = configs.iptvChannelLogoProvider
        if (configs.iptvChannelLogoOverride != null)
            Configs.iptvChannelLogoOverride = configs.iptvChannelLogoOverride
        if (configs.epgEnable != null)
            Configs.epgEnable = configs.epgEnable
        if (configs.epgSourceCurrent != null)
            Configs.epgSourceCurrent = configs.epgSourceCurrent
        if (configs.epgSourceList != null)
            Configs.epgSourceList = configs.epgSourceList
        if (configs.epgRefreshTimeThreshold != null)
            Configs.epgRefreshTimeThreshold = configs.epgRefreshTimeThreshold
        if (configs.epgChannelReserveList != null)
            Configs.epgChannelReserveList = configs.epgChannelReserveList
        if (configs.uiShowEpgProgrammeProgress != null)
            Configs.uiShowEpgProgrammeProgress = configs.uiShowEpgProgrammeProgress
        if (configs.uiShowEpgProgrammePermanentProgress != null)
            Configs.uiShowEpgProgrammePermanentProgress =
                configs.uiShowEpgProgrammePermanentProgress
        if (configs.uiShowChannelLogo != null)
            Configs.uiShowChannelLogo = configs.uiShowChannelLogo
        if (configs.uiShowChannelPreview != null)
            Configs.uiShowChannelPreview = configs.uiShowChannelPreview
        if (configs.uiUseClassicPanelScreen != null)
            Configs.uiUseClassicPanelScreen = configs.uiUseClassicPanelScreen
        if (configs.uiDensityScaleRatio != null)
            Configs.uiDensityScaleRatio = configs.uiDensityScaleRatio
        if (configs.uiFontScaleRatio != null)
            Configs.uiFontScaleRatio = configs.uiFontScaleRatio
        if (configs.uiTimeShowMode != null)
            Configs.uiTimeShowMode = configs.uiTimeShowMode
        if (configs.uiFocusOptimize != null)
            Configs.uiFocusOptimize = configs.uiFocusOptimize
        if (configs.uiScreenAutoCloseDelay != null)
            Configs.uiScreenAutoCloseDelay = configs.uiScreenAutoCloseDelay
        if (configs.updateForceRemind != null)
            Configs.updateForceRemind = configs.updateForceRemind
        if (configs.updateChannel != null)
            Configs.updateChannel = configs.updateChannel
        if (configs.videoPlayerUserAgent != null)
            Configs.videoPlayerUserAgent = configs.videoPlayerUserAgent
        if (configs.videoPlayerHeaders != null)
            Configs.videoPlayerHeaders = configs.videoPlayerHeaders
        if (configs.videoPlayerLoadTimeout != null)
            Configs.videoPlayerLoadTimeout = configs.videoPlayerLoadTimeout
        if (configs.videoPlayerDisplayMode != null)
            Configs.videoPlayerDisplayMode = configs.videoPlayerDisplayMode
        if (configs.themeAppCurrent != null)
            Configs.themeAppCurrent = configs.themeAppCurrent

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
private data class AllConfigs(
    val appBootLaunch: Boolean? = null,
    val appLastLatestVersion: String? = null,
    val appAgreementAgreed: Boolean? = null,
    val debugShowFps: Boolean? = null,
    val debugShowVideoPlayerMetadata: Boolean? = null,
    val debugShowLayoutGrids: Boolean? = null,
    val iptvLastChannelIdx: Int? = null,
    val iptvChannelChangeFlip: Boolean? = null,
    val iptvSourceCacheTime: Long? = null,
    val iptvSourceCurrent: IptvSource? = null,
    val iptvSourceList: IptvSourceList? = null,
    val iptvPlayableHostList: Set<String>? = null,
    val iptvChannelNoSelectEnable: Boolean? = null,
    val iptvChannelFavoriteEnable: Boolean? = null,
    val iptvChannelFavoriteListVisible: Boolean? = null,
    val iptvChannelFavoriteList: Set<String>? = null,
    val iptvChannelFavoriteChangeBoundaryJumpOut: Boolean? = null,
    val iptvChannelGroupHiddenList: Set<String>? = null,
    val iptvHybridMode: Configs.IptvHybridMode? = null,
    val iptvSimilarChannelMerge: Boolean? = null,
    val iptvChannelLogoProvider: String? = null,
    val iptvChannelLogoOverride: Boolean? = null,
    val epgEnable: Boolean? = null,
    val epgSourceCurrent: EpgSource? = null,
    val epgSourceList: EpgSourceList? = null,
    val epgRefreshTimeThreshold: Int? = null,
    val epgChannelReserveList: EpgProgrammeReserveList? = null,
    val uiShowEpgProgrammeProgress: Boolean? = null,
    val uiShowEpgProgrammePermanentProgress: Boolean? = null,
    val uiShowChannelLogo: Boolean? = null,
    val uiShowChannelPreview: Boolean? = null,
    val uiUseClassicPanelScreen: Boolean? = null,
    val uiDensityScaleRatio: Float? = null,
    val uiFontScaleRatio: Float? = null,
    val uiTimeShowMode: Configs.UiTimeShowMode? = null,
    val uiFocusOptimize: Boolean? = null,
    val uiScreenAutoCloseDelay: Long? = null,
    val updateForceRemind: Boolean? = null,
    val updateChannel: String? = null,
    val videoPlayerUserAgent: String? = null,
    val videoPlayerHeaders: String? = null,
    val videoPlayerLoadTimeout: Long? = null,
    val videoPlayerDisplayMode: VideoPlayerDisplayMode? = null,
    val themeAppCurrent: AppThemeDef? = null,
)