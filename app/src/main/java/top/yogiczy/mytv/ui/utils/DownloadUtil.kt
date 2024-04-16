package top.yogiczy.mytv.ui.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer
import java.io.File
import java.io.FileOutputStream

object DownloadUtil {
    suspend fun downloadTo(url: String, filePath: String, downloadListener: DownloadListener?) =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "下载文件: $url")

            val interceptor = Interceptor { chain ->
                val originalResponse = chain.proceed(chain.request())
                originalResponse.newBuilder()
                    .body(DownloadResponseBody(originalResponse, downloadListener)).build()
            }

            val client = OkHttpClient.Builder().addNetworkInterceptor(interceptor).build()
            val request = Request.Builder().url(url).build()

            try {
                with(client.newCall(request).execute()) {
                    if (!isSuccessful) {
                        throw Exception("下载文件失败: $code")
                    }

                    val file = File(filePath)
                    FileOutputStream(file).use { fos -> fos.write(body!!.bytes()) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "下载文件失败", e)
                throw Exception("下载文件失败，请检查网络连接", e.cause)
            }
        }

    private class DownloadResponseBody(
        private val originalResponse: okhttp3.Response,
        private val downloadListener: DownloadListener?,
    ) : okhttp3.ResponseBody() {
        override fun contentLength() = originalResponse.body!!.contentLength()

        override fun contentType() = originalResponse.body?.contentType()

        override fun source(): BufferedSource {
            return object : ForwardingSource(originalResponse.body!!.source()) {
                var totalBytesRead = 0L

                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                    val progress = (totalBytesRead * 100 / contentLength()).toInt()
                    downloadListener?.onProgress(progress)
                    return bytesRead
                }
            }.buffer()
        }

    }

    abstract class DownloadListener {
        open fun onProgress(progress: Int) {}
    }

    private const val TAG = "DownloadUtil"
}