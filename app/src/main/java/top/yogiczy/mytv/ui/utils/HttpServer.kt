package top.yogiczy.mytv.ui.utils

import android.content.Context
import android.widget.Toast
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
import top.yogiczy.mytv.AppGlobal
import top.yogiczy.mytv.R
import top.yogiczy.mytv.data.utils.Constants
import top.yogiczy.mytv.ui.screens.toast.ToastState
import java.io.File
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

object HttpServer : Loggable() {
    const val SERVER_PORT = 10481

    private val uploadedApkFile = File(AppGlobal.cacheDir, "uploaded_apk.apk").apply {
        deleteOnExit()
    }

    fun start(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val server = AsyncHttpServer()
                server.listen(AsyncServer.getDefault(), SERVER_PORT)

                server.get("/") { _, response ->
                    handleHomePage(response, context)
                }

                server.get("/api/settings") { _, response ->
                    handleGetSettings(response)
                }

                server.post("/api/settings") { request, response ->
                    handleSetSettings(request, response)
                }

                server.post("/api/upload/apk") { request, response ->
                    handleUploadApk(request, response, context)
                }

                log.i("服务已启动: 0.0.0.0:${SERVER_PORT}")
            } catch (ex: Exception) {
                log.e("服务启动失败: ${ex.message}", ex.cause)
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "设置服务启动失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun wrapResponse(response: AsyncHttpServerResponse) = response.apply {
        headers.set(
            "Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS"
        )
        headers.set("Access-Control-Allow-Origin", "*")
        headers.set(
            "Access-Control-Allow-Headers", "Origin, Content-Type, X-Auth-Token"
        )
    }

    private fun handleHomePage(response: AsyncHttpServerResponse, context: Context) {
        wrapResponse(response).apply {
            setContentType("text/html; charset=utf-8")
            send(context.resources.openRawResource(R.raw.index).readBytes().decodeToString())
        }
    }

    private fun handleGetSettings(response: AsyncHttpServerResponse) {
        wrapResponse(response).apply {
            setContentType("application/json")
            send(
                Json.encodeToString(
                    AllSettings(
                        appTitle = Constants.APP_TITLE,
                        appRepo = Constants.APP_REPO,

                        appBootLaunch = SP.appBootLaunch,

                        iptvChannelChangeFlip = SP.iptvChannelChangeFlip,
                        iptvSourceSimplify = SP.iptvSourceSimplify,
                        iptvSourceCachedAt = SP.iptvSourceCachedAt,
                        iptvSourceUrl = SP.iptvSourceUrl,
                        iptvSourceCacheTime = SP.iptvSourceCacheTime,

                        epgEnable = SP.epgEnable,
                        epgXmlCachedAt = SP.epgXmlCachedAt,
                        epgCachedHash = SP.epgCachedHash,
                        epgXmlUrl = SP.epgXmlUrl,
                        epgRefreshTimeThreshold = SP.epgRefreshTimeThreshold,

                        debugShowFps = SP.debugShowFps,
                        debugShowPlayerInfo = SP.debugShowPlayerInfo,
                    )
                )
            )
        }
    }

    private fun handleSetSettings(
        request: AsyncHttpServerRequest,
        response: AsyncHttpServerResponse,
    ) {
        val body = request.getBody<JSONObjectBody>().get()
        SP.appBootLaunch = body.get("appBootLaunch") as Boolean

        SP.iptvChannelChangeFlip = body.get("iptvChannelChangeFlip").toString().toBoolean()
        SP.iptvSourceSimplify = body.get("iptvSourceSimplify").toString().toBoolean()
        SP.iptvSourceCachedAt = body.get("iptvSourceCachedAt").toString().toLong()
        SP.iptvSourceUrl = body.get("iptvSourceUrl").toString()
        SP.iptvSourceCacheTime = body.get("iptvSourceCacheTime").toString().toLong()

        SP.epgEnable = body.get("epgEnable").toString().toBoolean()
        SP.epgXmlCachedAt = body.get("epgXmlCachedAt").toString().toLong()
        SP.epgCachedHash = body.get("epgCachedHash").toString().toInt()
        SP.epgXmlUrl = body.get("epgXmlUrl").toString()
        SP.epgRefreshTimeThreshold = body.get("epgRefreshTimeThreshold").toString().toInt()

        SP.debugShowFps = body.get("debugShowFps").toString().toBoolean()
        SP.debugShowPlayerInfo = body.get("debugShowPlayerInfo").toString().toBoolean()

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
                    ToastState.I.showToast("正在接收文件: ${(hasReceived * 100f / contentLength).toInt()}%")
                    os.write(byteArray)
                }
            }
        }

        body.setEndCallback {
            ToastState.I.showToast("文件接收完成")
            body.dataEmitter.close()
            os.flush()
            os.close()
            ApkInstaller.installApk(context, uploadedApkFile.path)
        }

        wrapResponse(response).send("success")
    }

    fun getLocalIpAddress(): String {
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
private data class AllSettings(
    val appTitle: String,
    val appRepo: String,

    val appBootLaunch: Boolean,

    val iptvChannelChangeFlip: Boolean,
    val iptvSourceSimplify: Boolean,
    val iptvSourceCachedAt: Long,
    val iptvSourceUrl: String,
    val iptvSourceCacheTime: Long,

    val epgEnable: Boolean,
    val epgXmlCachedAt: Long,
    val epgCachedHash: Int,
    val epgXmlUrl: String,
    val epgRefreshTimeThreshold: Int,

    val debugShowFps: Boolean,
    val debugShowPlayerInfo: Boolean,
)