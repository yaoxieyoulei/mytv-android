package top.yogiczy.mytv.core.data.repositories.git

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import top.yogiczy.mytv.core.data.entities.git.GitRelease
import top.yogiczy.mytv.core.data.network.await
import top.yogiczy.mytv.core.data.repositories.git.parser.GitReleaseParser
import top.yogiczy.mytv.core.data.utils.Loggable

/**
 * git数据获取
 */
class GitRepository : Loggable() {

    /**
     * 获取最新发行版
     */
    suspend fun latestRelease(url: String): GitRelease {
        log.d("获取最新发行版: $url")

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        try {
            val response = client.newCall(request).await()

            if (!response.isSuccessful) throw Exception("${response.code}: ${response.message}")

            val parser = GitReleaseParser.instances.first { it.isSupport(url) }
            return withContext(Dispatchers.IO) {
                parser.parse(response.body!!.string())
            }
        } catch (ex: Exception) {
            log.e("获取最新发行版失败", ex)
            throw Exception("获取最新发行版失败，请检查网络连接", ex)
        }
    }
}