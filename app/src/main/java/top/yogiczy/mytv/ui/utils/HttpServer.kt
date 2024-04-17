package top.yogiczy.mytv.ui.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.yogiczy.mytv.R
import top.yogiczy.mytv.ui.screens.toast.ToastState
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

object HttpServer {
    const val SERVER_PORT = 10481

    private const val TAG = "HttpServer"
    private lateinit var server: NanoHTTPD

    fun start(context: Context) {
        if (this::server.isInitialized) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                server = ServerApplication(context, SERVER_PORT)
                Log.d(TAG, "服务已启动: 0.0.0.0:${SERVER_PORT}")
            } catch (ex: Exception) {
                Log.e(TAG, "服务启动失败: ${ex.message}", ex.cause)
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "设置服务启动失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun stop() {
        server.stop()
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


    private class ServerApplication(private val context: Context, port: Int) : NanoHTTPD(port) {
        init {
            start(SOCKET_READ_TIMEOUT, false)
        }

        override fun serve(session: IHTTPSession): Response {
            if (session.uri == "/") {
                return newFixedLengthResponse(
                    context.resources.openRawResource(R.raw.index).readBytes().decodeToString(),
                )
            } else if (session.uri.startsWith("/api/settings/iptvCustomSource")) {
                val source = session.parameters["source"]?.firstOrNull() ?: ""
                Log.d(TAG, "设置直播源: $source")

                SP.iptvCustomSource = source

                if (source.isNotBlank()) {
                    ToastState.I.showToast("直播源设置成功")
                } else {
                    ToastState.I.showToast("直播源已恢复默认")
                }

                return newFixedLengthResponse("success")
            }

            return newFixedLengthResponse("not found")
        }
    }
}
