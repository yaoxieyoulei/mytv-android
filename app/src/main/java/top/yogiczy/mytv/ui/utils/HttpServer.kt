package top.yogiczy.mytv.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import top.yogiczy.mytv.R
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException


object HttpServer : Loggable() {
    const val SERVER_PORT = 10481

    private lateinit var server: NanoHTTPD

    fun start(context: Context) {
        if (this::server.isInitialized) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                server = ServerApplication(context, SERVER_PORT)
                log.i("服务已启动: 0.0.0.0:${SERVER_PORT}")
            } catch (ex: Exception) {
                log.e("服务启动失败: ${ex.message}", ex.cause)
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "设置服务启动失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
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


    private class ServerApplication(private val context: Context, port: Int) : NanoHTTPD(port) {
        init {
            start(SOCKET_READ_TIMEOUT, false)
        }

        private fun isPreflightRequest(session: IHTTPSession): Boolean {
            val headers = session.headers
            return Method.OPTIONS == session.method && headers.contains("origin") && headers.containsKey(
                "access-control-request-method"
            ) && headers.containsKey("access-control-request-headers")
        }

        private fun responseCORS(session: IHTTPSession): Response {
            val resp = wrapResponse(session, newFixedLengthResponse(""))
            val headers = session.headers
            resp.addHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS")
            val requestHeaders = headers["access-control-request-headers"]
            if (requestHeaders != null) {
                resp.addHeader("Access-Control-Allow-Headers", requestHeaders)
            }
            resp.addHeader("Access-Control-Max-Age", "0")
            return resp
        }

        private fun wrapResponse(session: IHTTPSession, resp: Response): Response {
            val headers = session.headers
            resp.addHeader("Access-Control-Allow-Credentials", "true")
            resp.addHeader("Access-Control-Allow-Origin", headers.getOrElse("origin") { "*" })
            val requestHeaders = headers["access-control-request-headers"]
            if (requestHeaders != null) {
                resp.addHeader("Access-Control-Allow-Headers", requestHeaders)
            }
            return resp
        }

        private inline fun <reified T> responseJson(data: T): Response {
            return newFixedLengthResponse(
                Response.Status.OK, "application/json", Json.encodeToString(data)
            )
        }

        private inline fun <reified T> parseBody(session: IHTTPSession): T {
            val files = mutableMapOf<String, String>()
            session.parseBody(files)
            return Json.decodeFromString(files["postData"]!!)
        }

        @SuppressLint("ResourceType")
        override fun serve(session: IHTTPSession): Response {
            if (isPreflightRequest(session)) {
                return responseCORS(session)
            }

            if (session.uri == "/") {
                return newFixedLengthResponse(
                    context.resources.openRawResource(R.raw.index).readBytes().decodeToString(),
                )
            } else if (session.uri.startsWith("/api/settings")) {
                if (session.method == Method.GET) {
                    return wrapResponse(
                        session,
                        responseJson(
                            AllSettings(
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
                            )
                        ),
                    )
                } else if (session.method == Method.POST) {
                    val data = parseBody<AllSettings>(session)
                    SP.appBootLaunch = data.appBootLaunch

                    SP.iptvChannelChangeFlip = data.iptvChannelChangeFlip
                    SP.iptvSourceSimplify = data.iptvSourceSimplify
                    SP.iptvSourceCachedAt = data.iptvSourceCachedAt
                    SP.iptvSourceUrl = data.iptvSourceUrl
                    SP.iptvSourceCacheTime = data.iptvSourceCacheTime

                    SP.epgEnable = data.epgEnable
                    SP.epgXmlCachedAt = data.epgXmlCachedAt
                    SP.epgCachedHash = data.epgCachedHash
                    SP.epgXmlUrl = data.epgXmlUrl
                    SP.epgRefreshTimeThreshold = data.epgRefreshTimeThreshold

                    SP.debugShowFps = data.debugShowFps

                    return wrapResponse(session, newFixedLengthResponse("success"))
                }
            }

            return wrapResponse(
                session,
                newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found")
            )
        }
    }
}

@Serializable
private data class AllSettings(
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
)