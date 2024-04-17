package top.yogiczy.mytv.ui.utils

import android.content.Context
import android.util.Log
import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.yogiczy.mytv.R
import top.yogiczy.mytv.ui.screens.toast.ToastState
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

object HttpServer {
    private const val TAG = "HttpServer"

    const val SERVER_PORT = 10481
    private lateinit var server: ApplicationEngine

    fun start(context: Context) {
        server = embeddedServer(Netty, SERVER_PORT) {
            routing {
                get("/") {
                    call.respondBytes(
                        context.resources.openRawResource(R.raw.index).readBytes(),
                        ContentType("text", "html")
                    )
                }

                get("/api/settings/iptvCustomSource") {
                    val source = call.parameters["source"] ?: ""
                    Log.d(TAG, "设置直播源: $source")

                    SP.iptvCustomSource = source

                    if (source.isNotBlank()) {
                        ToastState.I.showToast("直播源设置成功")
                    } else {
                        ToastState.I.showToast("直播源已恢复默认")
                    }

                    call.respondText("success")
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            server.start()
            Log.d(TAG, "服务已启动: 0.0.0.0:${SERVER_PORT}")
        }
    }

    fun stop() {
        server.stop(1000, 1000)
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
            Log.e(TAG, "IP Address: ${ex.message}", ex)
            return defaultIp
        }
    }
}